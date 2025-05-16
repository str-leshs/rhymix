package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserService userService;

    /**
     * 로그인 API
     * POST /api/users/login
     * @param loginRequest username, password 포함한 요청
     * @return 로그인 성공 시 User 정보, 실패 시 에러 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("nickname");
        String password = loginRequest.get("password");

        if (username == null || password == null) {
            System.out.println("❌ [입력 누락] username 또는 password가 null");
            return ResponseEntity.badRequest().body("입력값이 누락되었습니다.");
        }

        System.out.println("📥 [로그인 시도] username: " + username);

        try {
            // 서비스 - authenticate() 메서드 호출
            User user = userService.authenticate(username, password);

            System.out.println("✅ [로그인 성공]");
            return ResponseEntity.ok(user); // 로그인 성공 시 유저 정보 반환

        } catch (IllegalArgumentException e) {
            System.out.println("❌ [로그인 실패] " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
