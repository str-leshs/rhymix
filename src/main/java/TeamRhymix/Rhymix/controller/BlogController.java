package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.dto.ThemeDto;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class BlogController {

    private final UserService userService;

    // ✅ customizing 페이지 렌더링
    @GetMapping("/my/customizing")
    public String showCustomizingPage() {
        return "my/customizing"; // templates/my/customizing.html
    }

    // ✅ 테마 저장 API
    @PostMapping("/api/theme")
    public ResponseEntity<?> saveTheme(@RequestBody ThemeDto themeDto, Authentication auth) {
        String nickname = auth.getName();
        userService.updateUserTheme(nickname, themeDto.getSelectedTheme());
        return ResponseEntity.ok().build();
    }
}
