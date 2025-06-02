package TeamRhymix.Rhymix.service.impl;


import TeamRhymix.Rhymix.domain.NeighborRelation;
import TeamRhymix.Rhymix.service.NeighborService;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.repository.NeighborRepository;
import TeamRhymix.Rhymix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NeighborServiceImpl implements NeighborService {

    private final NeighborRepository neighborRepository;
    private final UserRepository userRepository;

    @Override
    public boolean addNeighbor(String username, String neighborName) {
        if (username.equals(neighborName)) return false; // 자기 자신에게 신청 불가

        if (neighborRepository.existsByUsernameAndNeighborName(username, neighborName)) return false;

        NeighborRelation relation = new NeighborRelation();
        relation.setUsername(username);           // 신청자
        relation.setNeighborName(neighborName);   // 상대 닉네임

        neighborRepository.save(relation);
        return true;
    }


    @Override
    public List<User> getMyNeighbors(String username) {
        List<NeighborRelation> relations = neighborRepository.findByUsername(username);
        List<String> neighborNames = relations.stream()
                .map(NeighborRelation::getNeighborName)
                .collect(Collectors.toList());

        return userRepository.findByNicknameIn(neighborNames); // 이웃 사용자 정보 조회
    }


}
