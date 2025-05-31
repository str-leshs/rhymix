package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean updateUserTheme(String username, String selectedTheme) {
        User user = getUserByUsername(username); // 사용자 조회
        if (user == null) {
            return false;
        }
        user.setSelectedTheme(selectedTheme);     // 테마 설정
        userRepository.save(user);               // 저장
        return true;
    }

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
        // ✅ 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User authenticate(String nickname, String rawPassword) {
        System.out.println("🔐 [authenticate] 로그인 시도");

        if (nickname == null || rawPassword == null) {
            System.out.println("⚠ [authenticate] nickname 또는 password null.");
            throw new IllegalArgumentException("아이디 또는 비밀번호가 입력되지 않았습니다.");
        }

        nickname = nickname.trim();
        rawPassword = rawPassword.trim();

        System.out.println("📥 전달받은 nickname: [" + nickname + "]");
        System.out.println("📥 전달받은 password: [" + rawPassword + "]");

        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            System.out.println("❌ DB nickname=[" + nickname + "] 인 사용자를 찾지 못함");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        System.out.println("✅ DB 사용자 확인 nickname=[" + user.getNickname() + "]");

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            System.out.println("❌ 비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        System.out.println("🎉 로그인 성공: " + user.getUsername());
        return user;
    }

    @Override
    public User findByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email);
    }

    @Override
    public UserDto getUserDtoByUsername(String nickname) {
        User user = getUserByNickname(nickname);

        if (user == null) {
            throw new RuntimeException("유저 정보를 찾을 수 없습니다.");
        }

        return new UserDto(
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                null,                     // password
                null,                     // confirmPassword
                user.getPhone(),
                user.getBio(),
                user.getProfileImage(),
                user.getPreferredGenres(),
                user.getSelectedTheme()
        );

    }

    @Override
    public List<NeighborDto> getAllNeighbors() {
        return userRepository.findAll().stream()
                .map(user -> new NeighborDto(
                        user.getNickname(),
                        user.getProfileImage(),
                        user.getPreferredGenres() != null ? user.getPreferredGenres() : List.of()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<NeighborDto> getNeighborsByGenre(String genre) {
        return userRepository.findAll().stream()
                .filter(user -> user.getPreferredGenres() != null && user.getPreferredGenres().contains(genre))
                .map(user -> new NeighborDto(
                        user.getNickname(),
                        user.getProfileImage(),
                        user.getPreferredGenres() != null ? user.getPreferredGenres() : List.of()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getRecommendedUsers(int limit) {
        List<User> all = userRepository.findAll();
        Collections.shuffle(all);
        return all.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<User> getNeighborList(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null || user.getNeighbors() == null) return new ArrayList<>();

        return user.getNeighbors().stream()
                .map(userRepository::findByUsername)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
