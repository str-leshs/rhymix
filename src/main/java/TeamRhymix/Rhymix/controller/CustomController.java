package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.dto.ThemeDto;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomController {

    private final UserService userService;

//    @GetMapping("my/customizing")
//    public String showCustomizingPage() {
//        return "mypage/customizing";
//    }

    //테마 저장
    @PostMapping("/api/theme")
    public ResponseEntity<?> saveTheme(@RequestBody ThemeDto themeDto, Authentication auth) {
        String nickname = auth.getName();
        userService.updateTheme(nickname, themeDto.getSelectedTheme());
        return ResponseEntity.ok().build();
    }

    //테마 조회
    @GetMapping("/api/theme/me")
    public ResponseEntity<ThemeDto> getTheme(Authentication auth) {
        String nickname = auth.getName();
        String selectedTheme = userService.getSelectedTheme(nickname);
        return ResponseEntity.ok(new ThemeDto(selectedTheme));
    }
}
