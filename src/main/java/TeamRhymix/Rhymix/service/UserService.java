package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.User;
import java.util.List;

import TeamRhymix.Rhymix.dto.DiaryDto;


public interface UserService {
    List<User> getAllUsers();
    User getUserByNickname(String nickname);
    User getUserByUsername(String username);
//    User findByNameAndEmail(String name, String email);
    User createUser(User user);
    boolean emailExists(String email);
    boolean updatePassword(String username, String newPassword);
    User authenticate(String nickname, String password); // 로그인 검증
    void updateTheme(String username, String selectedTheme);
    String getSelectedTheme(String username);
    DiaryDto getDiary(String nickname);
    void updateDiary(String nickname, DiaryDto diaryDto);

}

