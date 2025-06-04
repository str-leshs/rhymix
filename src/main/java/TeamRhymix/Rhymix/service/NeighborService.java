package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NeighborService {
    List<NeighborDto> getNeighbors(String ownerNickname);
    void addNeighbor(String ownerNickname, String neighborNickname);
    List<NeighborDto> getSuggestedNeighbors(String currentNickname);
    void removeNeighbor(String ownerNickname, String targetNickname);
    Page<NeighborDto> searchNeighbors(String myNickname, String genre, String keyword, int page, int size);
    int countSearchResults(String myNickname, String genre, String keyword);
    UserDto getNeighborProfile(String nickname);


}
