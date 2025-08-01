package TeamRhymix.Rhymix.domain.post.service;

import TeamRhymix.Rhymix.domain.post.entity.Post;
import TeamRhymix.Rhymix.domain.post.dto.PostRequestDto;
import TeamRhymix.Rhymix.domain.post.repository.PostRepository;
import TeamRhymix.Rhymix.domain.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TrackService trackService;
    private final MongoTemplate mongoTemplate;

    @Override
    public Post savePost(PostRequestDto requestDto, String userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        Post existing = postRepository.findTodayPostByUserId(userId, start, end);

        if (existing != null) {
            // 기존 포스트 업데이트
            existing.setMood(requestDto.getMood());
            existing.setWeather(requestDto.getWeather());
            existing.setComment(requestDto.getComment());
            existing.setTrackId(requestDto.getTrackId());
            existing.setCreatedAt(LocalDateTime.now());
            return postRepository.save(existing);
        }

        Post newPost = Post.builder()
                .userId(userId)
                .mood(requestDto.getMood())
                .weather(requestDto.getWeather())
                .comment(requestDto.getComment())
                .trackId(requestDto.getTrackId())
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(newPost);
    }



    @Override
    public Post getTodayPost(String userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        return postRepository.findTodayPostByUserId(userId, start, end);
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public Post getPostByDate(String userId, LocalDate date) {
        return postRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public List<Post> findPostsByUserAndMonth(String userId, LocalDate start, LocalDate end) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("createdAt").gte(start).lt(end));
        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));
        return mongoTemplate.find(query, Post.class);
    }

}
