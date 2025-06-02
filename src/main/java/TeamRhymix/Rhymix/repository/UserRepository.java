package TeamRhymix.Rhymix.repository;

import TeamRhymix.Rhymix.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

    // ✅ Optional<User>로 리팩토링된 메서드
    public Optional<User> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    public Optional<User> findByNickname(String nickname) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    public List<User> findByNicknameIn(List<String> nicknames) {
        Query query = new Query(Criteria.where("nickname").in(nicknames));
        return mongoTemplate.find(query, User.class);
    }

    public Optional<User> findOptionalByUsername(String username) {
        return findByUsername(username); // 이미 Optional 반환하므로 중첩 제거
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, User.class);
    }

    public User findByNameAndEmail(String name, String email) {
        Query query = new Query(
                Criteria.where("name").is(name)
                        .and("email").is(email)
        );
        return mongoTemplate.findOne(query, User.class);
    }

    public List<User> findByNicknameContainingIgnoreCase(String keyword) {
        Query query = new Query(Criteria.where("nickname").regex(".*" + keyword + ".*", "i"));
        return mongoTemplate.find(query, User.class);
    }
}
