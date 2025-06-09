package TeamRhymix.Rhymix.controller.mypage;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    /**
     * [GET] /mypage
     * 로그인한 사용자의 정보를 기반으로 마이페이지 화면 반환
     */
    @GetMapping("/mypage")
    public String showMyPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String nickname = userDetails.getUsername();
            User user = userService.getUserByNickname(nickname);

            if (user != null) {
                model.addAttribute("nickname", user.getNickname());
                model.addAttribute("profileImage", user.getProfileImage());
                model.addAttribute("bio", user.getBio());
                model.addAttribute("preferredGenres", user.getPreferredGenres());
            }
        }

        return "mypage/mypage";
    }
}
