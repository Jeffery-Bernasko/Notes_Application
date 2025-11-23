package org.example.notes_application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/ui/login")
    public String loginPage(){
        return "Login";
    }

    @GetMapping("/ui/signup")
    public String signupPage() {
        return "Signup";
    }

    @GetMapping("/ui/notes")
    public String notesPage() {
        return "Notes";
    }
}
