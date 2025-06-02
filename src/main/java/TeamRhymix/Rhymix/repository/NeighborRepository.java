package TeamRhymix.Rhymix.repository;

import TeamRhymix.Rhymix.domain.NeighborRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NeighborRepository extends MongoRepository<NeighborRelation, String> {

    List<NeighborRelation> findByUsername(String username); // 특정 사용자의 이웃 목록 조회

    boolean existsByUsernameAndNeighborName(String username, String neighborName); // 중복 체크
}
