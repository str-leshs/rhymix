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
        System.out.println("🔍 [findByUsername] 전달된 username: \"" + username + "\"");

        Query query = new Query(Criteria.where("username").is(username));
        System.out.println("🔍 [findByUsername] 생성된 Query: " + query.toString());

        User foundUser = mongoTemplate.findOne(query, User.class);

        if (foundUser != null) {
            System.out.println(" [findByUsername] 사용자 조회 성공: " + foundUser.toString());
        } else {
            System.out.println(" [findByUsername] 사용자 조회 실패 - null 반환됨");
        }

        return foundUser;
    }

    public Optional<User> findOptionalByUsername(String username) {
        return Optional.ofNullable(findByUsername(username));
    }

    public User findByNickname(String nickname) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        return mongoTemplate.findOne(query, User.class);
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, User.class);
    }

    // ✅ 이름 + 이메일로 사용자 찾기
    public User findByNameAndEmail(String name, String email) {
        Query query = new Query(
                Criteria.where("name").is(name)
                        .and("email").is(email)
        );
        return mongoTemplate.findOne(query, User.class);
    }


}
