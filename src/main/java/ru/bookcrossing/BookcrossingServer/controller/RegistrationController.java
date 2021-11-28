package ru.bookcrossing.BookcrossingServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import javax.validation.Valid;

@RestController
public class RegistrationController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public String addUser(@Valid @RequestBody User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().toString();
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return (String) model.getAttribute("passwordError");
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return (String) model.getAttribute("usernameError");
        }

        return "redirect:/";
    }
}
