package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/find-id")
    public String findIdPage() {
        return "find-id";
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";
    }
}
