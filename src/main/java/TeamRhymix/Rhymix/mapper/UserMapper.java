package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        dto.setSelectedTheme(user.getSelectedTheme());
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
//    public UserDto toDto(User user) {
//        UserDto dto = new UserDto();
//        dto.setUsername(user.getUsername());
//        dto.setNickname(user.getNickname());
//        dto.setEmail(user.getEmail());
//        dto.setPhone(user.getPhone() != null ? user.getPhone() : "");
//        dto.setBio(user.getBio() != null ? user.getBio() : "");
//        dto.setProfileImage(user.getProfileImage() != null ? user.getProfileImage() : "/img/default-profile.png");
//        dto.setPreferredGenres(user.getPreferredGenres() != null ? user.getPreferredGenres() : new ArrayList<>());
//
//        dto.setSelectedTheme(user.getSelectedTheme());
//
//        // 보안 상 노출 금지
//        dto.setPassword(null);
//        dto.setConfirmPassword(null);
//
//        return dto;
//    }


    public void updateFromDto(UserDto dto, User user) {
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setBio(dto.getBio());
        user.setProfileImage(dto.getProfileImage());
        user.setPreferredGenres(dto.getPreferredGenres());

        String selectedTheme = dto.getSelectedTheme();
        if (selectedTheme == null || selectedTheme.trim().isEmpty()) {
            user.setSelectedTheme("color1");
        } else {
            user.setSelectedTheme(selectedTheme.trim());
        }
    }

}
