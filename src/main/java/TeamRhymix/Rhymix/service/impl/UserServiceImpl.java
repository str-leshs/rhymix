package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public boolean updateTheme(String nickname, String selectedTheme) {
        User user = userRepository.findByNickname(nickname).orElse(null); // ✅ 수정
        if (user == null) return false;
        user.setSelectedTheme(selectedTheme);
        userRepository.save(user);
        return true;
    }

    @Override
    public String getSelectedTheme(String username) {
        User user = userRepository.findByUsername(username).orElse(null); // ✅ 수정
        if (user == null) throw new RuntimeException("사용자를 찾을 수 없습니다: " + username);
        return user.getSelectedTheme();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
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
        User user = userRepository.findByNickname(nickname).orElse(null); // ✅ 수정
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
        return user;
    }

    @Override
    public User findByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email);
    }

    @Override
    public UserDto getUserDtoByUsername(String nickname) {
        User user = getUserByNickname(nickname);
        if (user == null) throw new RuntimeException("유저 정보를 찾을 수 없습니다.");
        return new UserDto(
                user.getUsername(), user.getNickname(), user.getEmail(),
                null, null, user.getPhone(), user.getBio(),
                user.getProfileImage(), user.getPreferredGenres(), user.getSelectedTheme()
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
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    @Override
    public List<User> getNeighborList(String username) {
        User user = userRepository.findByUsername(username).orElse(null); // ✅ 수정
        if (user == null || user.getNeighbors() == null) return new ArrayList<>();
        return user.getNeighbors().stream()
                .map(u -> userRepository.findByUsername(u).orElse(null)) // ✅ 수정
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchNeighbors(String keyword, String genre, String currentUsername) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(currentUsername)) // 본인 제외
                .filter(user -> keyword == null || keyword.isEmpty() || user.getNickname().toLowerCase().contains(keyword.toLowerCase()))
                .filter(user -> genre == null || genre.isEmpty()
                        || (user.getPreferredGenres() != null && user.getPreferredGenres().contains(genre)))
                .collect(Collectors.toList());
    }


}
