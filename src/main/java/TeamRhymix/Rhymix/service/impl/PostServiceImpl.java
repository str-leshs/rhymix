package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.domain.Post.Comment;
import TeamRhymix.Rhymix.dto.CommentDto;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.repository.PostRepository;
import TeamRhymix.Rhymix.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public Post savePost(PostDto dto) {
        String userId = dto.getUserId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        Post existing = postRepository.findTodayPostByUserId(userId, startOfDay, endOfDay);

        if (existing != null) {
            existing.setTitle(dto.getTitle());
            existing.setArtist(dto.getArtist());
            existing.setCover(dto.getCover());
            existing.setMood(dto.getMood());
            existing.setWeather(dto.getWeather());
            existing.setComment(dto.getComment());
            existing.setCreatedAt(LocalDateTime.now());
            return postRepository.save(existing);
        } else {
            Post newPost = postMapper.toEntity(dto);
            newPost.setComment(dto.getComment());
            newPost.setCreatedAt(LocalDateTime.now());
            return postRepository.save(newPost);
        }
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

    @Override
    public List<CommentDto> getCommentsForTodayPost(String userId) {
        Post post = getTodayPost(userId);
        List<CommentDto> dtoList = new ArrayList<>();

        if (post != null && post.getComments() != null) {
            for (Comment comment : post.getComments()) {
                CommentDto dto = new CommentDto();
                dto.setText(comment.getText());
                dto.setUserNickname(comment.getUserNickname());
                dto.setCreatedAt(comment.getCreatedAt());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    @Override
    public void addCommentToTodayPost(String userId, String text) {
        Post post = getTodayPost(userId);
        if (post != null) {
            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }

            Comment comment = new Comment();
            comment.setText(text);
            comment.setUserNickname(userId);
            comment.setCreatedAt(LocalDateTime.now());

            post.getComments().add(comment);
            postRepository.save(post);
        }
    }
    @Override
    public Post getLatestPost(String userId) {
        return postRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

}
