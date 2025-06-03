package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.DiaryDto;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.mapper.UserMapper;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    // 사용자 테마 업데이트 (nickname 기준)
    @Override
    public void updateTheme(String nickname, String selectedTheme) {
        System.out.println("=== updateTheme 호출됨 ===");
        System.out.println("nickname = " + nickname + ", selectedTheme = " + selectedTheme);

        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            System.out.println("❌ 사용자 없음: " + nickname);
            return;
        }

        user.setSelectedTheme(selectedTheme);
        userRepository.save(user);
        System.out.println("✅ selectedTheme 저장 완료");
    }

    // 선택된 테마 조회
    @Override
    public String getSelectedTheme(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + username);
        }
        return user.getSelectedTheme();
    }

    @Override
    public void updateUserProfile(String nickname, UserDto userDto) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        userMapper.updateFromDto(userDto, user);
        userRepository.save(user);
    }


    @Override
    public DiaryDto getDiary(String nickname) {
        User user = mongoTemplate.findOne(
                Query.query(Criteria.where("nickname").is(nickname)), User.class);
        if (user == null) throw new RuntimeException("사용자 없음");

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
