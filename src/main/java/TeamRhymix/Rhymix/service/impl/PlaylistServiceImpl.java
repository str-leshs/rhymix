package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.*;
import TeamRhymix.Rhymix.dto.PlaylistDto;
import TeamRhymix.Rhymix.exception.ErrorCode;
import TeamRhymix.Rhymix.exception.PlaylistException;
import TeamRhymix.Rhymix.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final MongoTemplate mongoTemplate;

    /**
     * 월별 플레이리스트를 생성하는 메서드
     */
    @Override
    public Playlist generateMonthlyPlaylist(String nickname, int year, int month) {
        User user = mongoTemplate.findOne(  //사용자 조회
                new Query(Criteria.where("nickname").is(nickname)),
                User.class
        );
        if (user == null) throw new PlaylistException(ErrorCode.USER_NOT_FOUND);

        // 오늘 날짜 및 타겟 월 계산
        LocalDate today = LocalDate.now();
        LocalDate target = LocalDate.of(year, month, 1);

        // 미래의 월은 생성 불가 (ex. 4월에 5월 플리 보기)
        if (target.isAfter(today)) {
            throw new PlaylistException(ErrorCode.FUTURE_MONTH);
        }

        // 이번 달은 생성 불가 (ex. 4월에 4월 플리 보기)
        if (target.getYear() == today.getYear() && target.getMonth() == today.getMonth()) {
            throw new PlaylistException(ErrorCode.CURRENT_MONTH_INCOMPLETE);
        }

        String title = String.format("%d년 %d월 플레이리스트", year, month);

        // 동일한 플레이리스트가 이미 존재하는지 확인
        Query checkQuery = new Query();
        checkQuery.addCriteria(Criteria.where("userId").is(user.getNickname()));
        checkQuery.addCriteria(Criteria.where("type").is("monthly"));
        checkQuery.addCriteria(Criteria.where("title").is(title));

        Playlist existing = mongoTemplate.findOne(checkQuery, Playlist.class);
        if (existing != null)
            return existing;    // 이미 존재 시 반환


        // 해당 월의 시작 ~ 끝 범위 설정
        LocalDate start = target;
        LocalDate end = start.plusMonths(1);

        // 해당 월의 추천곡(Post) 목록 조회
        Query postQuery = new Query();
        postQuery.addCriteria(Criteria.where("userId").is(user.getNickname()));
        postQuery.addCriteria(Criteria.where("createdAt").gte(start).lt(end));
        postQuery.with(Sort.by("createdAt").ascending());

        List<Post> posts = mongoTemplate.find(postQuery, Post.class);

        // 추천곡이 없으면 예외 발생
        if (posts.isEmpty()) {
            throw new PlaylistException(ErrorCode.NO_POSTS);
        }

        // 추천곡의 리스트 추출
        List<String> trackIds = posts.stream()
                .map(Post::getId)
                .toList();

        // 새 플레이리스트 생성 후 저장
        Playlist playlist = Playlist.builder()
                .userId(user.getNickname())
                .title(title)
                .type("monthly")
                .trackIds(trackIds)
                .createdAt(LocalDateTime.now())
                .build();

        return mongoTemplate.save(playlist);
    }

    /**
     * 플레이리스트 ID로 해당 트랙 정보를 함께 조회
     */
    @Override
    public PlaylistDto getPlaylistWithTracks(String playlistId) {
        log.info("playlistId 요청됨: '{}'", playlistId);

        // 유효성 검사
        if (playlistId == null || playlistId.trim().isEmpty()) {
            throw new PlaylistException(ErrorCode.INVALID_ID_FORMAT);
        }

        ObjectId objectId;  // 문자열을 ObjectId로 변환
        try {
            objectId = new ObjectId(playlistId);
        } catch (IllegalArgumentException e) {
            throw new PlaylistException(ErrorCode.INVALID_ID_FORMAT);
        }

        // 플레이리스트 조회
        Playlist playlist = mongoTemplate.findById(objectId, Playlist.class);
        if (playlist == null) {
            throw new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND);
        }

        // 포함된 트랙(Post) 정보 조회
        Query trackQuery = new Query(Criteria.where("_id").in(playlist.getTrackIds()));
        List<Post> posts = mongoTemplate.find(trackQuery, Post.class);

        return new PlaylistDto(   // DTO 형태로 반환
                playlist.getId(),
                playlist.getTitle(),
                playlist.getType(),
                posts
        );
    }

    /**
     * 최신 월별 플레이리스트 조회
     */
    @Override
    public Playlist findLatestMonthlyPlaylistByNickname(String nickname) {
        List<Playlist> list = mongoTemplate.find(
                Query.query(
                        Criteria.where("userId").is(nickname)
                                .and("type").is("monthly")
                ),
                Playlist.class
        );

        if (list.isEmpty()) {
            return null;
        }

        // 생성일 기준 내림차순 정렬 후 가장 최신 항목 반환
        list.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        return list.get(0);
    }

    /**
     * 기분/날씨 태그를 기반으로 테마 플레이리스트 생성
     */
    @Override
    public PlaylistDto generateThemePlaylist(String nickname, String tag) {
        User user = mongoTemplate.findOne(Query.query(Criteria.where("nickname").is(nickname)), User.class);
        if (user == null) throw new PlaylistException(ErrorCode.USER_NOT_FOUND);

        // 해당 유저의 태그 기반 Post 목록 조회
        List<Post> posts = mongoTemplate.find(
                Query.query(
                        Criteria.where("userId").is(user.getNickname())
                                .orOperator(
                                        Criteria.where("weather").is(tag),
                                        Criteria.where("mood").is(tag)
                                )
                ), Post.class
        );

        if (posts.isEmpty()) {
            throw new PlaylistException(ErrorCode.NO_POSTS);
        }

        String title = tag + " 테마";
        String type = "theme";

        // 기존 theme 플레이리스트 있으면 삭제 (한 사용자가 동일 태그로 하나만 유지)
        Query query = Query.query(
                Criteria.where("userId").is(user.getNickname())
                        .and("type").is(type)
                        .and("title").is(title)
        );
        mongoTemplate.remove(query, Playlist.class);

        Playlist playlist = Playlist.builder()
                .userId(user.getNickname())
                .title(title)
                .type(type)
                .trackIds(posts.stream().map(Post::getId).toList())
                .createdAt(LocalDateTime.now())
                .build();

        Playlist saved = mongoTemplate.save(playlist);

        return new PlaylistDto(
                saved.getId(), title, type, posts
        );
    }

}



