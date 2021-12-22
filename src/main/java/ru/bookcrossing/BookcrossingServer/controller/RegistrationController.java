package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.AuthResponse;
import ru.bookcrossing.BookcrossingServer.model.Login;
import ru.bookcrossing.BookcrossingServer.model.UserDTO;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import javax.validation.Valid;
import java.util.Objects;

@Tag(
        name="Регистрация и аутентификация пользователей",
        description="Позволяет регистрироваться новым пользователям и выдает токен при аутентификации"
)
@RestController
public class RegistrationController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public RegistrationController(UserService userService, JwtProvider provider) {
        this.userService = userService;
        jwtProvider = provider;
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать пользователя"
    )
    @PostMapping("/registration")
    public Object registerUser(@Valid @RequestBody UserDTO userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder allErrorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(f -> allErrorMessage.append(
                    Objects.requireNonNull(f.getDefaultMessage())).append("\n"));
            return new ResponseEntity<>(allErrorMessage.toString(), HttpStatus.NOT_ACCEPTABLE);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            return new ResponseEntity<>("Пароли не совпадают", HttpStatus.NOT_ACCEPTABLE);
        }
        if (!userService.saveUser(userForm)){
            return new ResponseEntity<>("Пользователь с таким именем уже существует", HttpStatus.NOT_ACCEPTABLE);
        }

        userService.saveUser(userForm);

        return new ResponseEntity<>("redirect:/", HttpStatus.OK);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Выдает токен, если пользователь с таким логином существует и пароль корректен"
    )
    @PostMapping("/auth")
    public Object auth(@RequestBody Login request) {
        User userEntity = userService.findByLoginAndPassword(request);
        if(userEntity == null){
            return new ResponseEntity<>("Некорректный логин или пароль", HttpStatus.FORBIDDEN);
        }
        String token = jwtProvider.generateToken(userEntity.getLogin());
        String t = new AuthResponse(token).getToken();
        return new ResponseEntity<>(t, HttpStatus.OK);
    }
}
