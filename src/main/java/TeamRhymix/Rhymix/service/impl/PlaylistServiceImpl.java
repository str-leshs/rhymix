package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.*;
import TeamRhymix.Rhymix.dto.PlaylistWithTracksDto;
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

    @Override
    public Playlist generateMonthlyPlaylist(String nickname, int year, int month) {
        User user = mongoTemplate.findOne(
                new Query(Criteria.where("nickname").is(nickname)),
                User.class
        );
        if (user == null) throw new PlaylistException(ErrorCode.USER_NOT_FOUND);

        LocalDate today = LocalDate.now();
        LocalDate target = LocalDate.of(year, month, 1);

        if (target.isAfter(today)) {
            throw new PlaylistException(ErrorCode.FUTURE_MONTH);
        }

        if (target.getYear() == today.getYear() && target.getMonth() == today.getMonth()) {
            throw new PlaylistException(ErrorCode.CURRENT_MONTH_INCOMPLETE);
        }

        String title = String.format("%d년 %d월 플레이리스트", year, month);

        Query checkQuery = new Query();
        checkQuery.addCriteria(Criteria.where("userId").is(user.getNickname()));
        checkQuery.addCriteria(Criteria.where("type").is("monthly"));
        checkQuery.addCriteria(Criteria.where("title").is(title));

        Playlist existing = mongoTemplate.findOne(checkQuery, Playlist.class);
        if (existing != null) {
            return existing;
        }

        LocalDate start = target;
        LocalDate end = start.plusMonths(1);

        Query postQuery = new Query();
        postQuery.addCriteria(Criteria.where("userId").is(user.getNickname()));
        postQuery.addCriteria(Criteria.where("createdAt").gte(start).lt(end));
        postQuery.with(Sort.by("createdAt").ascending());

        List<Post> posts = mongoTemplate.find(postQuery, Post.class);

        if (posts.isEmpty()) {
            throw new PlaylistException(ErrorCode.NO_POSTS);
        }

        List<String> trackIds = posts.stream()
                .map(Post::getId)
                .toList();

        Playlist playlist = Playlist.builder()
                .userId(user.getNickname())
                .title(title)
                .type("monthly")
                .trackIds(trackIds)
                .createdAt(LocalDateTime.now())
                .build();

        return mongoTemplate.save(playlist);
    }

    @Override
    public PlaylistWithTracksDto getPlaylistWithTracks(String playlistId) {
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
            return null;
        }

        list.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        return list.get(0);
    }
}



