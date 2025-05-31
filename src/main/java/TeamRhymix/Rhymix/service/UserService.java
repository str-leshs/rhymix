package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import java.util.List;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.dto.NeighborDto;


public interface UserService {
    List<User> getAllUsers();
    User getUserByNickname(String nickname);
    User getUserByUsername(String username);
    User findByNameAndEmail(String name, String email);
    User createUser(User user);
    boolean emailExists(String email);
    boolean updatePassword(String username, String newPassword);
    User authenticate(String nickname, String password); // 로그인 검증
    UserDto getUserDtoByUsername(String username);
    List<NeighborDto> getAllNeighbors();
    List<NeighborDto> getNeighborsByGenre(String genre);
    List<User> getRecommendedUsers(int limit);
    List<User> getNeighborList(String username);
    boolean updateUserTheme(String username, String selectedTheme);
}
