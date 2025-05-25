package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.*;
import TeamRhymix.Rhymix.dto.PlaylistWithTracksDto;
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
     * 전월 posts를 기반으로 플레이리스트 생성
     */
    @Override
    public Playlist generateMonthlyPlaylist(String nickname, int year, int month) {
        User user = mongoTemplate.findOne(
                new Query(Criteria.where("nickname").is(nickname)),
                User.class
        );
        if (user == null) throw new RuntimeException("사용자 없음");

        String title = String.format("%d년 %d월 플레이리스트", year, month);

        // 이미 생성된 월별 플레이리스트 있는지 확인 (userId도 문자열 기준)
        Query checkQuery = new Query();
        checkQuery.addCriteria(Criteria.where("userId").is(user.getNickname()));
        checkQuery.addCriteria(Criteria.where("type").is("monthly"));
        checkQuery.addCriteria(Criteria.where("title").is(title));

        Playlist existing = mongoTemplate.findOne(checkQuery, Playlist.class);
        if (existing != null) {
            log.info("이미 존재하는 월별 플레이리스트 반환: {}, user: {}", existing.getId().toString(), nickname);
            return existing;
        }

        // 해당 월의 추천곡(Post) 조회
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        Query postQuery = new Query();
        postQuery.addCriteria(Criteria.where("userId").is(user.getNickname())); // 👈 문자열 기반 비교
        postQuery.addCriteria(Criteria.where("createdAt").gte(start).lt(end));
        postQuery.with(Sort.by("createdAt").ascending());

        List<Post> posts = mongoTemplate.find(postQuery, Post.class);

        if (posts.isEmpty()) {
            log.warn("추천곡이 없어 플레이리스트를 생성하지 않습니다. user: {}, year: {}, month: {}", nickname, year, month);
            throw new RuntimeException("해당 월에는 등록된 추천곡이 없습니다.");
        }

        List<String> trackIds = posts.stream()
                .map(Post::getId)
                .toList();

        Playlist playlist = Playlist.builder()
                .userId(user.getNickname()) // 👈 문자열로 저장
                .title(title)
                .type("monthly")
                .trackIds(trackIds)
                .createdAt(LocalDateTime.now())
                .build();

        Playlist saved = mongoTemplate.save(playlist);
        log.info("새로 생성된 플레이리스트 ID: {}", saved.getId().toString());
        return saved;
    }

    /**
     * playlistId로 플레이리스트 조회 및 트랙 리스트 조립
     */
    @Override
    public PlaylistWithTracksDto getPlaylistWithTracks(String playlistId) {
        log.info("playlistId 요청됨: '{}'", playlistId);

        if (playlistId == null || playlistId.trim().isEmpty()) {
            log.warn("playlistId가 null 또는 비어 있음");
            throw new RuntimeException("플레이리스트 ID가 유효하지 않습니다");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(playlistId);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 ObjectId 형식: {}", playlistId);
            throw new RuntimeException("유효하지 않은 ID 형식입니다");
        }

        Playlist playlist = mongoTemplate.findById(objectId, Playlist.class);
        if (playlist == null) {
            log.warn("playlistId={} 에 해당하는 플레이리스트 없음", playlistId);
            throw new RuntimeException("플레이리스트 없음");
        }

        Query trackQuery = new Query(Criteria.where("_id").in(playlist.getTrackIds()));
        List<Post> posts = mongoTemplate.find(trackQuery, Post.class);

        return new PlaylistWithTracksDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getType(),
                posts
        );
    }
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
            log.warn("📭 월별 플레이리스트 없음. user: {}", nickname);
            return null;
        }

        list.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())); // 최신순 정렬

        Playlist latest = list.get(0);
        log.info("최신 월별 플레이리스트 반환: {}, user: {}", latest.getId(), nickname);
        return latest;
    }

}

