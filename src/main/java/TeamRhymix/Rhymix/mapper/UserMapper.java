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
        user.setPhone(dto.getPhone());
        user.setJoinedAt(new Date());
        return user;
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(null);          // 비밀번호 노출 방지
        dto.setConfirmPassword(null);   // 비밀번호 노출 방지
        return dto;
    }
}


