package TeamRhymix.Rhymix.controller;

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
        // 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않았을 경우 로그인 페이지로 리다이렉트
        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        // 인증된 사용자의 nickname 기반으로 사용자 정보 조회
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String nickname = userDetails.getUsername();
            User user = userService.getUserByNickname(nickname); // UserService에서 사용자 정보 조회

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
