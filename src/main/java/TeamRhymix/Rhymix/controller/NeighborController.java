package TeamRhymix.Rhymix.controller;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NeighborController {

    @GetMapping("/neighborlist")
    public String neighborList(Model model) {
        List<Map<String, String>> neighbors = new ArrayList<>();

        // 테스트용 더미 데이터
        for (int i = 1; i <= 5; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("profileImage", "/image/placeholder_circle.png");
            item.put("nickname", "게스트" + i);
            neighbors.add(item);
        }

        // 왼쪽 리스트: 최대 9개
        List<Map<String, String>> leftList = neighbors.subList(0, Math.min(9, neighbors.size()));

        // 오른쪽 리스트: 나머지 (10번째 이후)
        List<Map<String, String>> rightList = neighbors.size() > 9
                ? neighbors.subList(9, neighbors.size())
                : Collections.emptyList();

        model.addAttribute("leftList", leftList);
        model.addAttribute("rightList", rightList);

        return "neighbor/neighborlist";
    }
}


