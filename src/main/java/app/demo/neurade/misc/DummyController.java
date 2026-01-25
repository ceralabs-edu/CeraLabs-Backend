package app.demo.neurade.misc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @GetMapping("/hello/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String hello() {
        return "Hello, Admin!";
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String helloUser() {
        return "Hello, User!";
    }
}
