package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserByNickname(String nickname);
    User getUserByUsername(String username);
    User createUser(User user);
    boolean emailExists(String email);
    User authenticate(String username, String password);
    boolean updatePassword(String username, String newPassword);


}
