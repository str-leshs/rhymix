package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.mapper.UserMapper;
import TeamRhymix.Rhymix.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @ResponseBody
    @PostMapping("/api/users/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        if (userDto.getNickname() == null || userDto.getUsername() == null ||
                userDto.getEmail() == null || userDto.getPassword() == null ||
                userDto.getConfirmPassword() == null) {
            return ResponseEntity.badRequest().body("모든 필수 정보를 입력해주세요.");
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        if (userService.getUserByNickname(userDto.getNickname()) != null) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }

        User saved = userService.createUser(userMapper.toEntity(userDto));
        return ResponseEntity.ok(userMapper.toDto(saved));
    }




    // ✅ 테마 저장 (Principal 기반으로 수정)
    @ResponseBody
    @PostMapping("/api/users/me/theme")
    public ResponseEntity<?> updateTheme(@RequestBody Map<String, String> request, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        String username = principal.getName(); // 이제 username 기준
        String selectedTheme = request.get("theme");

        if (selectedTheme == null) {
            return ResponseEntity.badRequest().body("테마 정보가 없습니다.");
        }

        userService.updateTheme(username, selectedTheme); // username 기준으로 호출
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/api/users/exists/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname) != null);
    }

    @ResponseBody
    @GetMapping("/api/users/exists/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    @ResponseBody
    @GetMapping("/api/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> list = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @ResponseBody
    @GetMapping("/api/users/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ResponseBody
    @PostMapping("/api/users/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");

        User user = userService.getUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(Map.of("nickname", user.getNickname()));
    }

    @ResponseBody
    @PostMapping("/api/users/find-password")
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");

        User user = userService.getUserByUsername(username);
        if (user == null || !email.equals(user.getEmail())) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("인증 성공");
    }

    @ResponseBody
    @PostMapping("/api/users/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String newPassword = req.get("newPassword");

        boolean updated = userService.updatePassword(username, newPassword);
        if (!updated) {
            return ResponseEntity.status(404).body("비밀번호 변경 실패");
        }

        return ResponseEntity.ok("비밀번호 변경 완료");
    }

    @ResponseBody
    @GetMapping("/api/neighbors")
    public List<NeighborDto> getNeighbors() {
        return userService.getAllNeighbors();
    }

    @ResponseBody
    @GetMapping("/api/neighbors/genre")
    public List<NeighborDto> getNeighborsByGenre(@RequestParam String genre) {
        return userService.getNeighborsByGenre(genre);
    }

    @ResponseBody
    @GetMapping("/api/recommend-neighbors")
    public List<NeighborDto> getRecommendedNeighbors() {
        return userService.getRecommendedUsers(5).stream()
                .map(user -> new NeighborDto(
                        user.getNickname(),
                        user.getProfileImage(),
                        user.getPreferredGenres() != null ? user.getPreferredGenres() : List.of()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/neighborlist")
    public String getNeighborList(Model model, Principal principal) {
        String username = principal.getName();
        List<User> neighbors = userService.getNeighborList(username);

        List<Map<String, String>> leftList = new ArrayList<>();
        List<Map<String, String>> rightList = new ArrayList<>();

        for (int i = 0; i < neighbors.size(); i++) {
            User user = neighbors.get(i);
            Map<String, String> info = new HashMap<>();
            info.put("nickname", user.getNickname());
            info.put("profileImage", user.getProfileImage());

            if (i % 2 == 0) {
                leftList.add(info);
            } else {
                rightList.add(info);
            }
        }

        model.addAttribute("leftList", leftList);
        model.addAttribute("rightList", rightList);
        return "neighbor/neighborlist";
    }
    @GetMapping("/customizing")
    public String showCustomizingPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername(); // ✅ 로그인 아이디
        User user = userService.getUserByNickname(username);

        if (user == null) {
            System.out.println("❌ 사용자 정보 없음");
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "redirect:/main";
        }

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("username", user.getUsername());
        System.out.println("✅ 사용자 정보 nickname: " + user.getNickname());
        return "my/customizing";
    }


    @GetMapping("/user/mypage/{nickname}")
    public String getMypage(@PathVariable String nickname, Model model) {
        User user = userService.getUserByNickname(nickname);
        if (user == null) {
            return "redirect:/main";
        }

        model.addAttribute("user", user);
        return "mypage/view";
    }
}
