package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserProfileRequestDto;
import TeamRhymix.Rhymix.dto.UserProfileResponseDto;
import TeamRhymix.Rhymix.repository.UserRepository;
import TeamRhymix.Rhymix.service.UserProfileService;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final UserRepository userRepository;

    // 사용자 프로필 조회
    @GetMapping("/{username}/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String username) {
        UserProfileResponseDto profile = userProfileService.getUserProfile(username);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    // 사용자 프로필 수정
    @PutMapping("/{username}/profile")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @PathVariable String username,
            @RequestBody UserProfileRequestDto requestDto) {

        UserProfileResponseDto updated = userProfileService.updateUserProfile(username, requestDto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/{username}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable String username,
                                            @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("필수 값이 누락되었습니다.");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        if (!user.getPassword().equals(oldPassword)) {
            return ResponseEntity.status(400).body("기존 비밀번호가 틀렸습니다.");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("비밀번호 변경 완료");
    }

    // 사용자 프로필 이미지(Base64) 업로드
    @PostMapping("/{username}/profile/image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable String username,
                                                @RequestBody Map<String, String> request) {
        String base64 = request.get("imageBase64");

        if (base64 == null || base64.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 데이터가 없습니다.");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setProfileImage(base64);
        userRepository.save(user);

        return ResponseEntity.ok("이미지가 성공적으로 저장되었습니다.");
    }
}
