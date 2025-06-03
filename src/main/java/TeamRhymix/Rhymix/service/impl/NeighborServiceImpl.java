package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.Neighbor;
import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.mapper.NeighborMapper;
import TeamRhymix.Rhymix.service.NeighborService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NeighborServiceImpl implements NeighborService {

    private final MongoTemplate mongoTemplate;
    private final NeighborMapper neighborMapper;

    @Override
    public List<NeighborDto> getNeighbors(String ownerNickname) {
        // neighbors 컬렉션에서 이웃 닉네임 목록 조회
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        Neighbor neighbor = mongoTemplate.findOne(query, Neighbor.class);

        if (neighbor == null || neighbor.getNeighbors() == null || neighbor.getNeighbors().isEmpty()) {
            return List.of();
        }

        // users 컬렉션에서 이웃 사용자 정보 조회
        Query userQuery = new Query(Criteria.where("nickname").in(neighbor.getNeighbors()));
        List<User> users = mongoTemplate.find(userQuery, User.class);

        // User → NeighborDto 변환
        return users.stream()
                .map(neighborMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addNeighbor(String ownerNickname, String neighborNickname) {
        // 이웃 닉네임을 neighbors 배열에 추가 (중복 방지)
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        Update update = new Update().addToSet("neighbors", neighborNickname);
        mongoTemplate.upsert(query, update, Neighbor.class);
    }
}
