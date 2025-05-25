package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlaylistPageController {

    /**
     * 월별 플레이리스트 HTML 페이지 반환
     */
    @GetMapping("/playlist/monthly")
    public String showMonthlyPlaylistPage(@RequestParam int year, @RequestParam int month, Model model) {
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "playlist/monthly"; // templates/playlist/monthly.html
    }
}
