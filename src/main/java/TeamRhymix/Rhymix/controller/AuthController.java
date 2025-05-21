package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.mapper.UserMapper;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * 인증 컨트롤러: 로그인 / 로그아웃 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * 로그인 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String nickname = loginRequest.get("nickname");
        String password = loginRequest.get("password");

        if (nickname == null || password == null) {
            return ResponseEntity.badRequest().body("입력값이 누락되었습니다.");
        }

        try {
            User user = userService.authenticate(nickname, password);
            session.setAttribute("user", user);  // 세션에 유저 정보 저장
            return ResponseEntity.ok(userMapper.toDto(user)); // 안전한 DTO 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 로그아웃 API
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }

    /**
     * 로그인된 사용자 정보 조회
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
