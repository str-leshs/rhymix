package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomizingController {

    @GetMapping("/customizing")
    public String showCustomizingPage() {
        return "my/customizing";  // templates/customizing/customizing.html
    }
}