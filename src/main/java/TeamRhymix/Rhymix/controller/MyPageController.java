package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    // ✅ 1. 마이페이지 화면
    @GetMapping("/mypage")
    public String myPage(Model model, Authentication authentication) {
        String nickname = authentication.getName();
        User user = userService.getUserByNickname(nickname);
        if (user == null) {
            throw new RuntimeException("유저 정보를 찾을 수 없습니다.");
        }

        // 🟡 유저 정보 Model에 담기
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("profileImage", user.getProfileImage());
        model.addAttribute("bio", user.getBio());
        model.addAttribute("preferredGenres", user.getPreferredGenres());

        return "mypage/mypage"; // → templates/mypage/mypage.html
    }

    // ✅ 2. 로그인된 유저 정보 반환 (AJAX 용)
    @GetMapping("/api/users/me")
    public ResponseEntity<UserDto> getMyPageInfo(Authentication authentication) {
        String nickname = authentication.getName();
        UserDto userDto = userService.getUserDtoByUsername(nickname);
        return ResponseEntity.ok(userDto);
    }

    // ✅ 3. 이웃 리스트 화면 (랜덤 정렬 + 2컬럼 분할)
    @GetMapping("/mypage/neighborlist")
    public String neighborListPage(Model model) {
        List<NeighborDto> allNeighbors = userService.getAllNeighbors();
        if (allNeighbors == null) allNeighbors = new ArrayList<>();

        // 🔀 1. 랜덤 셔플
        Collections.shuffle(allNeighbors);

        // ✂ 2. 좌우 분할
        List<NeighborDto> leftList = new ArrayList<>();
        List<NeighborDto> rightList = new ArrayList<>();

        for (int i = 0; i < allNeighbors.size(); i++) {
            if (i % 2 == 0) leftList.add(allNeighbors.get(i));
            else rightList.add(allNeighbors.get(i));
        }

        // ✅ 3. 모델에 담기
        model.addAttribute("leftList", leftList);
        model.addAttribute("rightList", rightList);
        return "neighbor/neighborlist";
    }
}
