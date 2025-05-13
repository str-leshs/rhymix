package TeamRhymix.Rhymix.repository;

import TeamRhymix.Rhymix.domain.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findByUserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);
}
