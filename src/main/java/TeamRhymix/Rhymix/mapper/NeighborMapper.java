package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.Neighbor;
import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.NeighborDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NeighborMapper {

    /**
     * 이웃 목록 조회 -> User 객체를 NeighborDto로 변환
     */
    public NeighborDto toDto(User user) {
        return new NeighborDto(
                user.getNickname(),
                user.getProfileImage(),
                user.getPreferredGenres()
        );
    }

    /**
     * 이웃 추가 -> User 객체를 Neighbor.MyNeighbor로 변환
     */
    public Neighbor.MyNeighbor toSimple(User user) {
        return new Neighbor.MyNeighbor(
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
