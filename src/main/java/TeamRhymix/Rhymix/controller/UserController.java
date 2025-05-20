package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.mapper.UserMapper;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * 주요 기능: 회원가입, 아이디 중복 확인, 사용자 조회 등
 */
@RestController
@RequestMapping("/api/users") // 이 컨트롤러의 기본 URI 경로
@RequiredArgsConstructor // 생성자 주입 자동 생성
public class UserController {

    private final UserService userService;     // 비즈니스 로직 담당
    private final UserMapper userMapper;       // DTO ↔ Domain 변환 담당

    /**
     * 회원가입
     * -> 클라이언트로부터 UserDto를 입력받아 유효성 검사 후 DB에 저장
     *
     * @param userDto 회원가입 폼 데이터
     * @return 성공 시 저장된 사용자 정보, 실패 시 오류 메시지 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        // [1] 필수값 검증
        if (userDto.getNickname() == null || userDto.getUsername() == null ||
                userDto.getEmail() == null || userDto.getPassword() == null ||
                userDto.getConfirmPassword() == null) {
            return ResponseEntity.badRequest().body("모든 필수 정보를 입력해주세요.");
        }

        // [2] 비밀번호 일치 확인
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        // [3] 아이디(닉네임) 중복 체크
        if (userService.getUserByNickname(userDto.getNickname()) != null) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }

        // [4] 저장 및 응답
        User saved = userService.createUser(userMapper.toEntity(userDto)); // DTO → Entity → 저장
        return ResponseEntity.ok(userMapper.toDto(saved)); // Entity → DTO 응답
    }

    /**
     * 아이디(nickname) 중복 확인
     * - 아이디가 이미 존재하는지 확인 (AJAX 요청용)
     *
     * @param nickname 사용자가 입력한 아이디
     * @return true(이미 존재), false(사용 가능)
     */
    @GetMapping("/exists/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname) != null);
    }

    /**
     * 전체 사용자 목록 조회
     * - DB의 모든 사용자 정보를 리스트 형태로 반환
     *
     * @return 사용자 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> list = userService.getAllUsers().stream()
                .map(userMapper::toDto) // Entity → DTO 변환
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * 단일 사용자 조회 (nickname 기준)
     * - nickname으로 사용자를 조회
     *
     * @param nickname 조회 대상 사용자 아이디
     * @return 존재 시 사용자 정보, 없으면 404 반환
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        if (user == null) return ResponseEntity.notFound().build(); // 사용자 없음
        return ResponseEntity.ok(userMapper.toDto(user));           // 사용자 정보 응답
    }
}

