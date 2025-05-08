package TeamRhymix.Rhymix.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import TeamRhymix.Rhymix.domain.User;


import java.util.List;

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
}
