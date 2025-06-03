package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setJoinedAt(new Date());
        user.setSelectedTheme(dto.getSelectedTheme()); // ✅ dto → entity로 테마 저장
        return user;
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBio(user.getBio());
        dto.setProfileImage(user.getProfileImage());
        dto.setPreferredGenres(user.getPreferredGenres());

        dto.setSelectedTheme(user.getSelectedTheme()); // ✅ entity → dto로 테마 전달

        dto.setPassword(null);          // 비밀번호 노출 방지
        dto.setConfirmPassword(null);   // 비밀번호 노출 방지

        return dto;
    }

    public void updateFromDto(UserDto dto, User user) {
        // 이름, 아이디, 비밀번호는 수정하지 않음
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setBio(dto.getBio());
        user.setProfileImage(dto.getProfileImage());
        user.setPreferredGenres(dto.getPreferredGenres());
        user.setSelectedTheme(dto.getSelectedTheme()); // ✅ update 시 테마 반영
    }
}
