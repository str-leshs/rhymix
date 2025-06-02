package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserProfileRequestDto;
import TeamRhymix.Rhymix.dto.UserProfileResponseDto;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username).orElse(null); // ✅ 수정
        if (user == null) return null;

        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setBio(user.getBio());
        response.setProfileImage(user.getProfileImage());
        response.setPreferredGenres(user.getPreferredGenres());

        return response;
    }

    @Override
    public UserProfileResponseDto updateUserProfile(String username, UserProfileRequestDto requestDto) {
        User user = userRepository.findByUsername(username).orElse(null); // ✅ 수정
        if (user == null) return null;

        user.setEmail(requestDto.getEmail());
        user.setNickname(requestDto.getNickname());
        user.setBio(requestDto.getBio());
        user.setProfileImage(requestDto.getProfileImage());
        user.setPreferredGenres(requestDto.getPreferredGenres());

        userRepository.save(user);

        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setBio(user.getBio());
        response.setProfileImage(user.getProfileImage());
        response.setPreferredGenres(user.getPreferredGenres());

        return response;
    }
}
