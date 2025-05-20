package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserByNickname(String nickname);
    User createUser(User user);
    boolean emailExists(String email);
}
