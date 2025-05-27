package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.dto.UserProfileRequestDto;
import TeamRhymix.Rhymix.dto.UserProfileResponseDto;

public interface UserProfileService {
    UserProfileResponseDto getUserProfile(String username);
    UserProfileResponseDto updateUserProfile(String username, UserProfileRequestDto requestDto);
}
