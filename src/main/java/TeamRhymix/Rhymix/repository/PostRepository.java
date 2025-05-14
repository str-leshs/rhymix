package TeamRhymix.Rhymix.repository;

import TeamRhymix.Rhymix.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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
}
