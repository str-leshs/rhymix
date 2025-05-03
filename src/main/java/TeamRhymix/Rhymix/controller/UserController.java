package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        User saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }


}

