package TeamRhymix.Rhymix.domain.post.repository;

import TeamRhymix.Rhymix.domain.chat.entity.Chat;
import TeamRhymix.Rhymix.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;


@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * 오늘의 추천곡을 조회 (userId와 날짜 기준)
     */
    public Post findTodayPostByUserId(String userId, LocalDateTime start, LocalDateTime end) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .andOperator(
                        Criteria.where("createdAt").gte(start),
                        Criteria.where("createdAt").lte(end)
                ));
        return mongoTemplate.findOne(query, Post.class);
    }

    /**
     * 추천곡 저장 (기존 포스트가 존재하면 수정, 없으면 새로 생성)
     */
    public Post save(Post post) {
        return mongoTemplate.save(post);
    }


    /**
     * 특정 사용자의 모든 추천곡(Post) 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 해당 사용자의 Post 목록
     */
    public List<Post> findByUserId(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Post.class);
    }

    public Post findByUserIdAndDate(String userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("createdAt").gte(start).lte(end));
        return mongoTemplate.findOne(query, Post.class);
    }
    public Post findTopByUserIdOrderByCreatedAtDesc(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(1);
        return mongoTemplate.findOne(query, Post.class);
    }

    /**
     * 특정 Post에 댓글(Chat) 추가
     * @param postId 댓글을 추가할 Post의 ID
     * @param chat 추가할 댓글 객체
     */
    public void addChatToPost(String postId, Chat chat) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().push("chats", chat);
        mongoTemplate.updateFirst(query, update, Post.class);
    }




}
