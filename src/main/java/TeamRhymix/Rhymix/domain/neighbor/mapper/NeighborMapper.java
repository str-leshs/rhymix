package TeamRhymix.Rhymix.domain.neighbor.mapper;

import TeamRhymix.Rhymix.domain.neighbor.entity.Neighbor;
import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.neighbor.dto.NeighborDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NeighborMapper {

    //User → NeighborDto 변환 (NullPointException 방지)
    public NeighborDto toDto(User user) {
        List<String> genres = (user.getPreferredGenres() != null)
                ? user.getPreferredGenres()
                : List.of();

        return new NeighborDto(
                user.getNickname(),
                user.getProfileImage(),
                genres
        );
    }

    //User → Neighbor.MyNeighbor 변환 (이웃 단순 표시용)
    public Neighbor.MyNeighbor toSimple(User user) {
        return new Neighbor.MyNeighbor(
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
