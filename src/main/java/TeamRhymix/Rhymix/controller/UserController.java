package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.mapper.UserMapper;
import TeamRhymix.Rhymix.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import TeamRhymix.Rhymix.dto.NeighborDto;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        if (userDto.getNickname() == null || userDto.getUsername() == null ||
                userDto.getEmail() == null || userDto.getPassword() == null ||
                userDto.getConfirmPassword() == null) {
            return ResponseEntity.badRequest().body("모든 필수 정보를 입력해주세요.");
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        if (userService.getUserByNickname(userDto.getNickname()) != null) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }

        User saved = userService.createUser(userMapper.toEntity(userDto));
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @GetMapping("/exists/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname) != null);
    }

    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> list = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> req) {
        String username = req.get("username"); // 사용자 이름
        String email = req.get("email");       // 이메일

        User user = userService.getUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(Map.of("nickname", user.getNickname())); // 아이디 반환
    }

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
    @GetMapping("/api/neighbors")
    public List<NeighborDto> getNeighbors() {
        return userService.getAllNeighbors();
    }
    @GetMapping("/api/neighbors/genre")
    public List<NeighborDto> getNeighborsByGenre(@RequestParam String genre) {
        return userService.getNeighborsByGenre(genre);
    }
}
