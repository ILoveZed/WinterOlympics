package org.informatics.winterolympics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage() {
        return "admin/users";
    }

    @GetMapping("/games")
    public String gamesPage() {
        return "games";
    }

    @GetMapping("/manager/games")
    public String managerGamesPage() {
        return "manager/games";
    }

    @GetMapping("/manager/applications")
    public String managerApplicationsPage() {
        return "manager/applications";
    }

    @GetMapping("/manager/results")
    public String managerResultsPage() {
        return "manager/results";
    }
}
