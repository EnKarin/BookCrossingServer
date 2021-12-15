package ru.bookcrossing.BookcrossingServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/adm")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public List<User> userList() {
        return userService.findAll();
    }

    @PostMapping("/delete/{id}")
    public String  deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/";
    }
}
