package TeamRhymix.Rhymix.domain.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/main")
    public String showMainPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login"; // 인증 실패 시 로그인 페이지로 이동
        }

        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("nickname", userDetails.getUsername());
        }

        return "main"; // 인증된 경우에만 메인 페이지 표시
    }

}