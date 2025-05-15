package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodayPostController {

    @GetMapping("/today")
    public String showTodayPage() {
        return "post/today";
    }
}
