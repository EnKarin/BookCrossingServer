package ru.bookcrossing.BookcrossingServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.service.UserService;

@RestController
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String userList(Model model) {
        model.addAttribute("allUsers", userService.findAll());
        return "admin";
    }

    @PostMapping("/admin")
    public String  deleteUser(@RequestParam(defaultValue = "" ) Integer userId,
                              @RequestParam(defaultValue = "" ) String action) {
        if (action.equals("delete")){
            userService.deleteUser(userId);
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/getAllMore/{userId}")
    public String gtUser(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        return "admin";
    }
}
