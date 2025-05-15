package TeamRhymix.Rhymix.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import TeamRhymix.Rhymix.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    public User findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }

    public Optional<User> findOptionalByUsername(String username) {
        return Optional.ofNullable(findByUsername(username));
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, User.class);
    }

    // ✅ 이름 + 이메일로 유저 조회 (아이디 찾기용)
    public User findByNameAndEmail(String name, String email) {
        Query query = new Query(
                Criteria.where("name").is(name).and("email").is(email)
        );
        return mongoTemplate.findOne(query, User.class);
    }
}
