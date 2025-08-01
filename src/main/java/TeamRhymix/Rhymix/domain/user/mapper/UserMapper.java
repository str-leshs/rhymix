package TeamRhymix.Rhymix.domain.user.mapper;

import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.user.dto.UserDto;
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
        user.setPhone(dto.getPhone());
        user.setBio(dto.getBio());
        user.setProfileImage(dto.getProfileImage());

        // 장르 정규화
        dto.normalizeGenres();
        user.setPreferredGenres(dto.getPreferredGenres());

        user.setSelectedTheme(dto.getSelectedTheme() != null ? dto.getSelectedTheme() : "color1");
        user.setNeighbors(new ArrayList<>());  // 처음 생성 시 빈 리스트
        user.setJoinedAt(new Date());
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

        dto.setSelectedTheme(user.getSelectedTheme());

        dto.setPassword(null);
        dto.setConfirmPassword(null);

        return dto;
    }


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
