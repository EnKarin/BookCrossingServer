package ru.bookcrossing.BookcrossingServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.AuthResponse;
import ru.bookcrossing.BookcrossingServer.model.Login;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import javax.validation.Valid;
import java.util.Objects;

@RestController
public class RegistrationController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public RegistrationController(UserService userService, JwtProvider provider) {
        this.userService = userService;
        jwtProvider = provider;
    }

    @GetMapping("/loginSuccessful")
    public String loginSuccessful(){
        return "Successful";
    }

    @PostMapping("/registration")
    public Object registerUser(@Valid @RequestBody User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            StringBuilder allErrorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(f -> allErrorMessage.append(
                    Objects.requireNonNull(f.getDefaultMessage())).append("\n"));
            return allErrorMessage.toString();
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return model.getAttribute("passwordError");
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return model.getAttribute("usernameError");
        }

        userService.saveUser(userForm);

        return "redirect:/";
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody Login request) {
        User userEntity = userService.findByLoginAndPassword(request);
        String token = jwtProvider.generateToken(userEntity.getLogin());
        return new AuthResponse(token);
    }
}
