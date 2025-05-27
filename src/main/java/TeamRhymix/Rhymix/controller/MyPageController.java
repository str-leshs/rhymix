package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    // ✅ 1. 템플릿 뷰용: 마이페이지 화면
    @GetMapping("/mypage")
    public String myPage(Model model, Authentication authentication) {
        String nickname = authentication.getName();
        System.out.println("✅ /mypage 진입, nickname = " + nickname);
        User user = userService.getUserByNickname(nickname);
        if (user == null) {
            throw new RuntimeException("유저 정보를 찾을 수 없습니다.");
        }

        model.addAttribute("user", user);
        return "mypage/mypage"; // ✅ 정확한 템플릿 경로로 매칭돼야 함
    }




    // ✅ 2. REST API: 로그인된 유저 정보 반환 (AJAX 용)
    @GetMapping("/api/users/me")
    public ResponseEntity<UserDto> getMyPageInfo(Authentication authentication) {
        String username = authentication.getName(); // Spring Security로부터 로그인 유저명 추출
        UserDto userDto = userService.getUserDtoByUsername(username);
        return ResponseEntity.ok(userDto);
    }

}
