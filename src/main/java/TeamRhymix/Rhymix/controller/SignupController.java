package TeamRhymix.Rhymix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignupController {

    @GetMapping("/join/agreement")
    public String showAgreementPage() {
        return "join/agreement";
    }

    @GetMapping("/join/form")
    public String showSignupForm() {
        return "join/form";
    }

}

