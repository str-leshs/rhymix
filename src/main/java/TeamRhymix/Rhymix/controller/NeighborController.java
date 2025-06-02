package TeamRhymix.Rhymix.controller;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    @GetMapping("/neighborsearch")
    public String neighborSearch(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        int pageSize = 10; // 한 페이지당 10명

        // 전체 더미 데이터 (테스트용 25명)
        List<Map<String, Object>> allNeighbors = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("nickname", "이웃" + i);
            item.put("tags", Arrays.asList("k-pop", "밴드"));
            allNeighbors.add(item);
        }

        // 페이지 수 계산
        int totalPages = (int) Math.ceil((double) allNeighbors.size() / pageSize);

        // 범위 설정
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allNeighbors.size());

        // 잘라낸 리스트 전달
        List<Map<String, Object>> paginatedNeighbors = allNeighbors.subList(fromIndex, toIndex);

        // 모델에 값 추가
        model.addAttribute("neighbors", paginatedNeighbors);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);

        return "neighbor/search";
    }



}


