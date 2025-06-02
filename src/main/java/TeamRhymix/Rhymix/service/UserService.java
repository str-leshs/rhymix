package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserByNickname(String nickname);
    User getUserByUsername(String username);
    User createUser(User user);
    boolean emailExists(String email);
    boolean updatePassword(String username, String newPassword);
    User authenticate(String nickname, String password); // 로그인 검증
    User updateUserProfile(String nickname, UserDto userDto);

}
