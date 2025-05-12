package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // ✅ 팀 규칙에 맞춘 base URI
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
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (username == null || password == null) {
            System.out.println("❌ [입력 누락] username 또는 password가 null");
            return ResponseEntity.badRequest().body("입력값이 누락되었습니다.");
        }

        System.out.println("📥 [로그인 시도] username: " + username);
        User user = userService.getUserByUsername(username);
        System.out.println("📤 [DB 유저 검색 결과] user: " + user);

        if (user == null) {
            System.out.println("❌ [유저 없음]");
            return ResponseEntity.status(401).body("존재하지 않는 사용자입니다.");
        }

        if (user.getPassword() == null) {
            System.out.println("❌ [비밀번호 없음]");
            return ResponseEntity.status(500).body("사용자의 비밀번호 정보가 없습니다.");
        }

        if (!user.getPassword().equals(password)) {
            System.out.println("❌ [비밀번호 불일치]");
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

        System.out.println("✅ [로그인 성공]");
        return ResponseEntity.ok(user); // ✅ 로그인 성공 시 유저 정보 반환
    }
}
