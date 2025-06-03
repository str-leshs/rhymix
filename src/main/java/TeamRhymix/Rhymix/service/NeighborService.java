package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.dto.NeighborDto;

import java.util.List;

public interface NeighborService {
    List<NeighborDto> getNeighbors(String ownerNickname);
    void addNeighbor(String ownerNickname, String neighborNickname);
    List<NeighborDto> getSuggestedNeighbors(String currentNickname);
    void removeNeighbor(String ownerNickname, String targetNickname);
}
