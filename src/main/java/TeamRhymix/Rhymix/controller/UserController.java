package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        User saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    // 전체 유저 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 이메일 존재 확인
    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    // username으로 유저 조회
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
    // 아이디 찾기: 이름 + 이메일로 조회
    @PostMapping("/find-id")
    public ResponseEntity<?> findUsername(@RequestBody Map<String, String> req) {
        String name = req.get("name");
        String email = req.get("email");

        User user = userService .findByNameAndEmail(name, email);
        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(user.getUsername()); // 사용자 아이디 반환
    }

    // 비밀번호 찾기: 아이디, 이메일로 인증
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");

        User user = userService.getUserByUsername(username);
        if (user == null || !email.equals(user.getEmail())) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("인증 성공");
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String newPassword = req.get("newPassword");

        boolean updated = userService.updatePassword(username, newPassword);
        if (!updated) {
            return ResponseEntity.status(404).body("비밀번호 변경 실패");
        }

        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}
