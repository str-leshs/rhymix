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

    // ✅ 사용자 테마 업데이트 (nickname 기준)
    @Override
    public boolean updateTheme(String nickname, String selectedTheme) {
        System.out.println("=== updateTheme 호출됨 ===");
        System.out.println("nickname = " + nickname + ", selectedTheme = " + selectedTheme);

        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            System.out.println("❌ 사용자 없음: " + nickname);
            return false;
        }

        user.setSelectedTheme(selectedTheme);
        userRepository.save(user);
        System.out.println("✅ selectedTheme 저장 완료");
        return true;
    }

    // ✅ 선택된 테마 조회
    @Override
    public String getSelectedTheme(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + username);
        }
        return user.getSelectedTheme();
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
        if (nickname == null || rawPassword == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 입력되지 않았습니다.");
        }

        nickname = nickname.trim();
        rawPassword = rawPassword.trim();

        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return user;
    }

    @Override
    public User findByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email);
    }

//    @Override
//    public UserDto getUserDtoByUsername(String nickname) {
//        User user = getUserByNickname(nickname);
//
//        if (user == null) {
//            throw new RuntimeException("유저 정보를 찾을 수 없습니다.");
//        }
//
//        return new UserDto(
//                user.getUsername(),
//                user.getNickname(),
//                user.getEmail(),
//                null,
//                null,
//                user.getPhone(),
//                user.getBio(),
//                user.getProfileImage(),
//                user.getPreferredGenres(),
//                user.getSelectedTheme()
//        );
//    }

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
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
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
