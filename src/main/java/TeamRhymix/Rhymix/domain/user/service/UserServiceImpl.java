package TeamRhymix.Rhymix.domain.user.service;

import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.user.dto.DiaryDto;
import TeamRhymix.Rhymix.domain.user.dto.UserDto;
import TeamRhymix.Rhymix.domain.user.mapper.UserMapper;
import TeamRhymix.Rhymix.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;
    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
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
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public User authenticate(String nickname, String rawPassword) {
        if (nickname == null || rawPassword == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 입력되지 않았습니다.");
        }

        nickname = nickname.trim();
        rawPassword = rawPassword.trim();

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return user;
    }

    @Override
    public void updateTheme(String nickname, String selectedTheme) {
        System.out.println("=== updateTheme 호출됨 ===");
        System.out.println("nickname = " + nickname + ", selectedTheme = " + selectedTheme);

        userRepository.findByNickname(nickname).ifPresentOrElse(user -> {
            user.setSelectedTheme(selectedTheme);
            userRepository.save(user);
            System.out.println("✅ selectedTheme 저장 완료");
        }, () -> System.out.println("❌ 사용자 없음: " + nickname));
    }

    @Override
    public String getSelectedTheme(String username) {
        return userRepository.findByUsername(username)
                .map(User::getSelectedTheme)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
    }

    @Override
    public void updateUserProfile(String nickname, UserDto userDto) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        userMapper.updateFromDto(userDto, user);

        // 장르 정규화 적용
        if (userDto.getPreferredGenres() != null && !userDto.getPreferredGenres().isEmpty()) {
            List<String> normalizedGenres = normalizeGenres(userDto.getPreferredGenres());
            user.setPreferredGenres(normalizedGenres);
        }
        userRepository.save(user);
    }
    //DB 장르 저장 시 정규화
    private List<String> normalizeGenres(List<String> genres) {
        return genres.stream()
                .filter(g -> g != null && !g.isBlank())
                .map(g -> g.toLowerCase().replace("-", "").replace(" ", ""))
                .distinct()
                .toList();
    }

    @Override
    public DiaryDto getDiary(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return new DiaryDto(
                user.getNickname(),
                user.getDiaryTitle(),
                user.getDiaryContent(),
                user.getDiaryImage()
        );
    }

    @Override
    public void updateDiary(String nickname, DiaryDto diaryDto) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        Update update = new Update()
                .set("diaryTitle", diaryDto.getDiaryTitle())
                .set("diaryContent", diaryDto.getDiaryContent())
                .set("diaryImage", diaryDto.getDiaryImage());

        mongoTemplate.updateFirst(query, update, User.class);
    }
}
