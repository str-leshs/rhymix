package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.service.NeighborService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NeighborPageController {

    private final NeighborService neighborService;

    @GetMapping("/neighborlist")
    public String neighborListPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String nickname = userDetails.getUsername();
        List<NeighborDto> neighbors = neighborService.getNeighbors(nickname);

        List<NeighborDto> leftList = new ArrayList<>();
        List<NeighborDto> rightList = new ArrayList<>();

        for (int i = 0; i < neighbors.size(); i++) {
            if (i % 2 == 0) leftList.add(neighbors.get(i));
            else rightList.add(neighbors.get(i));
        }

        model.addAttribute("leftList", leftList);
        model.addAttribute("rightList", rightList);

        System.out.println("이웃 수: " + neighbors.size());
        neighbors.forEach(n -> System.out.println(n.getNickname()));


        return "neighbor/neighborlist";
    }
}
