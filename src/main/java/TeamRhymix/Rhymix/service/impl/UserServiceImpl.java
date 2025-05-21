package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findOptionalByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword); // 암호화는 필요시 여기에 적용
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User authenticate(String nickname, String password) {
        System.out.println("🔐 [authenticate] 로그인 시도");

        if (nickname == null || password == null) {
            System.out.println("⚠ [authenticate] nickname 또는 password가 null입니다.");
            throw new IllegalArgumentException("아이디 또는 비밀번호가 입력되지 않았습니다.");
        }

        nickname = nickname.trim();
        password = password.trim();

        System.out.println("📥 전달받은 nickname: [" + nickname + "]");
        System.out.println("📥 전달받은 password: [" + password + "]");

        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            System.out.println("❌ DB에서 nickname=[" + nickname + "] 인 사용자를 찾지 못함");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        System.out.println("✅ DB 사용자 확인 nickname=[" + user.getNickname() + "], password=[" + user.getPassword() + "]");

        if (user.getPassword() == null || !user.getPassword().trim().equals(password)) {
            System.out.println("❌ 비밀번호 불일치 - 입력: [" + password + "] / DB: [" + user.getPassword() + "]");
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        System.out.println("🎉 로그인 성공: " + user.getUsername());
        return user;
    }
}
