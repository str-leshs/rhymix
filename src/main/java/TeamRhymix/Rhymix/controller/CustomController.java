package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.dto.ThemeDto;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController // ✅ RestController로 변경 (ResponseEntity 자동 반환)
@RequiredArgsConstructor
public class CustomController {

    private final UserService userService;

    // ✅ customizing 페이지 렌더링 (이건 html 반환이므로 따로 남김)
    @GetMapping("/my/customizing")
    public String showCustomizingPage() {
        return "my/customizing"; // templates/my/customizing.html
    }

    // ✅ 테마 저장 API
    @PostMapping("/api/theme")
    public ResponseEntity<?> saveTheme(@RequestBody ThemeDto themeDto, Authentication auth) {
        String nickname = auth.getName();
        userService.updateTheme(nickname, themeDto.getSelectedTheme());
        return ResponseEntity.ok().build();
    }

    // ✅ 테마 조회 API (main 페이지 진입 시 사용)
    @GetMapping("/api/theme/me")
    public ResponseEntity<ThemeDto> getTheme(Authentication auth) {
        String nickname = auth.getName();
        String selectedTheme = userService.getSelectedTheme(nickname);
        return ResponseEntity.ok(new ThemeDto(selectedTheme));
    }
}
