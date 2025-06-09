package TeamRhymix.Rhymix.controller.neighbor;

import TeamRhymix.Rhymix.dto.NeighborDto;
import TeamRhymix.Rhymix.dto.UserDto;
import TeamRhymix.Rhymix.service.NeighborService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/search")
    public String searchPage(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) String keyword,
                             Model model) {

        String nickname = userDetails.getUsername();
        int size = 10;

        Page<NeighborDto> resultPage = neighborService.searchNeighbors(nickname, genre, keyword, page, size);

        model.addAttribute("neighbors", resultPage.getContent()); // 현재 페이지의 항목
        model.addAttribute("currentPage", resultPage.getNumber() + 1);
        model.addAttribute("totalPages", resultPage.getTotalPages());

        return "neighbor/search";
    }

    @GetMapping("/neighbor/neighbor-view/{nickname}")
    public String viewNeighborPage(@PathVariable String nickname, Model model) {
        UserDto user = neighborService.getNeighborProfile(nickname);
        model.addAttribute("user", user);
        return "neighbor/neighbor-view";
    }



}
