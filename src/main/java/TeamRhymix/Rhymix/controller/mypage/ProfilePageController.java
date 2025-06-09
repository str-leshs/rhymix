package TeamRhymix.Rhymix.controller.mypage;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfilePageController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String nickname = userDetails.getUsername();
        User user = userService.getUserByNickname(nickname);

        if (user.getPreferredGenres() == null) {
            user.setPreferredGenres(List.of("", ""));
        }

        //사용자가 선택할 수 있는 장르 목록
        //th:each="g : ${genres}" 형태로 HTML에서 반복 렌더링
        model.addAttribute("user", user);
        model.addAttribute("genres", List.of(
                "kpop", "r&b", "dance", "ballad", "jazz", "rock",
                "classic", "트로트", "hiphop", "pop", "jpop", "ost", "라틴", "인디", "band"
        ));
        return "mypage/profile";
    }
}

