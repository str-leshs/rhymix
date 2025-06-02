package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;

import java.util.List;

public interface NeighborService {

    boolean addNeighbor(String username, String neighborName);       // 이웃 추가
    List<User> getMyNeighbors(String username);                      // 내 이웃 목록
}
