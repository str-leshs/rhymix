package TeamRhymix.Rhymix.domain.playlist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlaylistPageController {

    @GetMapping("/playlist/monthly")
    public String showMonthlyPlaylistPage(@RequestParam int year, @RequestParam int month, Model model) {
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "playlist/monthly";
    }
}
