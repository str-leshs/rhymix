package TeamRhymix.Rhymix.controller.neighbor;

import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.service.NeighborService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {

    private final NeighborService neighborService;

    // 내 이웃 목록 조회
    @GetMapping
    public ResponseEntity<List<NeighborDto>> getMyNeighbors(@AuthenticationPrincipal UserDetails userDetails) {
        String nickname = userDetails.getUsername();
        return ResponseEntity.ok(neighborService.getNeighbors(nickname));
    }

    // 이웃 추가
    @PostMapping("/add")
    public ResponseEntity<Void> addNeighbor(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String targetNickname) {
        String ownerNickname = userDetails.getUsername();
        neighborService.addNeighbor(ownerNickname, targetNickname);
        return ResponseEntity.ok().build();
    }

    // 이웃 추천 목록 조회
    @GetMapping("/suggested")
    public ResponseEntity<List<NeighborDto>> getSuggestedNeighbors(@AuthenticationPrincipal UserDetails userDetails) {
        String nickname = userDetails.getUsername();
        return ResponseEntity.ok(neighborService.getSuggestedNeighbors(nickname));
    }

    // 이웃 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeNeighbor(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestParam String targetNickname) {
        String ownerNickname = userDetails.getUsername();
        neighborService.removeNeighbor(ownerNickname, targetNickname);
        return ResponseEntity.ok().build();
    }

    // 이웃 검색 + 페이징 처리
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchNeighbors(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String nickname = userDetails.getUsername();
        Page<NeighborDto> result = neighborService.searchNeighbors(nickname, genre, keyword, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        return ResponseEntity.ok(response);
    }


    // 이웃 검색 결과 수 (페이징용 count)
    @GetMapping("/search/count")
    public ResponseEntity<Integer> countSearchResults(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String keyword) {

        String nickname = userDetails.getUsername();
        int total = neighborService.countSearchResults(nickname, genre, keyword);
        return ResponseEntity.ok(total);
    }

}

