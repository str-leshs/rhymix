package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController { // ✅ 클래스 선언!

    private final UserService userService;

    @GetMapping("/mypage")
    public String myPage(Model model) {
        User user = userService.getUserByUsername("춘식이"); // 임시 사용자
        model.addAttribute("user", user);
        return "mypage"; // templates/mypage.html
    }
}
