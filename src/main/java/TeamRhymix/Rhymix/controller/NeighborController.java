package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.service.NeighborService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {

    private final NeighborService neighborService;

    @GetMapping
    public ResponseEntity<List<NeighborDto>> getMyNeighbors(@AuthenticationPrincipal UserDetails userDetails) {
        String nickname = userDetails.getUsername();
        return ResponseEntity.ok(neighborService.getNeighbors(nickname));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addNeighbor(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String targetNickname) {
        String ownerNickname = userDetails.getUsername();
        neighborService.addNeighbor(ownerNickname, targetNickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/suggested")
    public ResponseEntity<List<NeighborDto>> getSuggestedNeighbors(@AuthenticationPrincipal UserDetails userDetails) {
        String nickname = userDetails.getUsername();
        return ResponseEntity.ok(neighborService.getSuggestedNeighbors(nickname));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeNeighbor(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestParam String targetNickname) {
        String ownerNickname = userDetails.getUsername();
        neighborService.removeNeighbor(ownerNickname, targetNickname);
        return ResponseEntity.ok().build();
    }

}
