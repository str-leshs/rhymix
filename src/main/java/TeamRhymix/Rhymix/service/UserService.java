package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import java.util.List;
import TeamRhymix.Rhymix.domain.User;

public interface UserService {
    List<User> getAllUsers();
    User getUserByUsername(String username);
    User createUser(User user);
    boolean emailExists(String email);
    User authenticate(String username, String password);
    boolean updatePassword(String username, String newPassword);
    User findByNameAndEmail(String name, String email);

}
