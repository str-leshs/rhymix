package TeamRhymix.Rhymix.repository;

import TeamRhymix.Rhymix.domain.Neighbor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NeighborRepository {

    private final MongoTemplate mongoTemplate;

    public NeighborRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<Neighbor> findByOwnerNickname(String ownerNickname) {
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        return Optional.ofNullable(mongoTemplate.findOne(query, Neighbor.class));
    }

    public Neighbor save(Neighbor neighbor) {
        return mongoTemplate.save(neighbor);
    }

    public void deleteByOwnerNickname(String ownerNickname) {
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        mongoTemplate.remove(query, Neighbor.class);
    }
}
