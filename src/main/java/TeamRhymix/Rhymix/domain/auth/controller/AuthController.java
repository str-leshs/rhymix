package TeamRhymix.Rhymix.domain.auth.controller;

import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.user.mapper.UserMapper;
import TeamRhymix.Rhymix.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
     * 로그인된 사용자 정보 조회
     * GET /api/auth/me
     * - Spring Security의 인증 객체에서 사용자 식별
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByNickname(userDetails.getUsername());

        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

}
