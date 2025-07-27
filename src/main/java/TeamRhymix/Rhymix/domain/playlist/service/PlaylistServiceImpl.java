package TeamRhymix.Rhymix.domain.playlist.service;

import TeamRhymix.Rhymix.domain.playlist.entity.Playlist;
import TeamRhymix.Rhymix.domain.post.entity.Post;
import TeamRhymix.Rhymix.domain.track.entity.Track;
import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.playlist.dto.PlaylistDto;
import TeamRhymix.Rhymix.domain.playlist.dto.PlaylistTrackInfo;
import TeamRhymix.Rhymix.global.exception.ErrorCode;
import TeamRhymix.Rhymix.global.exception.PlaylistException;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .postIds(posts.stream().map(Post::getId).toList())
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

        if (playlistId == null || playlistId.trim().isEmpty()) {
            throw new PlaylistException(ErrorCode.INVALID_ID_FORMAT);
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(playlistId);
        } catch (IllegalArgumentException e) {
            throw new PlaylistException(ErrorCode.INVALID_ID_FORMAT);
        }

        Playlist playlist = mongoTemplate.findById(objectId, Playlist.class);
        if (playlist == null) {
            throw new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND);
        }

        List<String> postIds = playlist.getPostIds(); // 필드명 변경됨

        // 1. postId 목록으로 Post 조회
        List<Post> posts = mongoTemplate.find(
                Query.query(Criteria.where("_id").in(postIds)), Post.class
        );

        // 2. post에서 trackId 추출 후 Track 조회
        List<String> trackIds = posts.stream()
                .map(Post::getTrackId)
                .distinct()
                .toList();

        List<Track> tracks = mongoTemplate.find(
                Query.query(Criteria.where("trackId").in(trackIds)), Track.class
        );

        // 3. trackId → Track 매핑
        Map<String, Track> trackMap = tracks.stream()
                .collect(Collectors.toMap(Track::getTrackId, t -> t));

        // 4. PlaylistTrackInfo 구성
        List<PlaylistTrackInfo> trackInfoList = posts.stream()
                .map(post -> {
                    Track track = trackMap.get(post.getTrackId());
                    if (track == null) {
                        log.warn("트랙 정보가 존재하지 않습니다. trackId: {}", post.getTrackId());
                        return null;
                    }

                    return new PlaylistTrackInfo(
                            track.getTitle(),
                            track.getArtist(),
                            post.getMood(),
                            post.getWeather()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new PlaylistDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getType(),
                trackInfoList
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

        // 기존 동일 테마 플레이리스트 삭제
        Query query = Query.query(
                Criteria.where("userId").is(user.getNickname())
                        .and("type").is(type)
                        .and("title").is(title)
        );
        mongoTemplate.remove(query, Playlist.class);

        // trackId 추출
        List<String> trackIds = posts.stream()
                .map(Post::getTrackId)
                .distinct()
                .toList();

        List<Track> tracks = mongoTemplate.find(
                Query.query(Criteria.where("trackId").in(trackIds)),
                Track.class
        );

        // trackId → Track 매핑
        Map<String, Track> trackMap = tracks.stream()
                .collect(Collectors.toMap(Track::getTrackId, t -> t));

        // PlaylistTrackInfo 구성
        List<PlaylistTrackInfo> trackInfos = posts.stream()
                .map(post -> {
                    Track track = trackMap.get(post.getTrackId());
                    if (track == null) return null;

                    return new PlaylistTrackInfo(
                            track.getTitle(),
                            track.getArtist(),
                            post.getMood(),
                            post.getWeather()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        // Playlist 저장 (postIds 저장)
        Playlist playlist = Playlist.builder()
                .userId(user.getNickname())
                .title(title)
                .type(type)
                .postIds(posts.stream().map(Post::getId).toList())
                .createdAt(LocalDateTime.now())
                .build();

        Playlist saved = mongoTemplate.save(playlist);

        return new PlaylistDto(saved.getId(), title, type, trackInfos);
    }

    @Override
    public PlaylistDto getThemePlaylistPreview(String nickname, String tag) {
        List<Post> posts = mongoTemplate.find(
                Query.query(
                        Criteria.where("userId").is(nickname)
                                .orOperator(
                                        Criteria.where("weather").is(tag),
                                        Criteria.where("mood").is(tag)
                                )
                ), Post.class
        );

        if (posts.isEmpty()) {
            return new PlaylistDto(null, tag + " 테마", "theme", List.of());
        }

        List<String> trackIds = posts.stream()
                .map(Post::getTrackId)
                .distinct()
                .toList();

        List<Track> tracks = mongoTemplate.find(
                Query.query(Criteria.where("trackId").in(trackIds)), Track.class
        );

        Map<String, Track> trackMap = tracks.stream()
                .collect(Collectors.toMap(Track::getTrackId, t -> t));

        List<PlaylistTrackInfo> trackInfos = posts.stream()
                .map(post -> {
                    Track track = trackMap.get(post.getTrackId());
                    if (track == null) return null;
                    return new PlaylistTrackInfo(
                            track.getTitle(),
                            track.getArtist(),
                            post.getMood(),
                            post.getWeather()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new PlaylistDto(null, tag + " 테마", "theme", trackInfos);
    }


}



