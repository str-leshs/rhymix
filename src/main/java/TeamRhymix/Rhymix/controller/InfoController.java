package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @GetMapping("/info")
    public String infoPage() {
        return "info"; // /info 요청 → info.html
    }

    @GetMapping("/")
    public String rootPage() {
        return "info"; // / 요청 → info.html
    }
}


