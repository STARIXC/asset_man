package org.utj.asman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // Returns the view name "login" which maps to src/main/resources/templates/login.html
        return "login";
    }
}