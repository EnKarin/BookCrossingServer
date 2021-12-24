package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Пароли не совпадают"),
            @ApiResponse(responseCode = "406", description = "Пользователь с таким логином уже существует"),
            @ApiResponse(responseCode = "400", description = "Введены некорректные данные"),
            @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу")}
    )
    @PostMapping("/registration")
    public Object registerUser(@Valid @RequestBody UserDTO userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder allErrorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(f -> allErrorMessage.append(
                    Objects.requireNonNull(f.getDefaultMessage())).append("\n"));
            return new ResponseEntity<>(allErrorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            return new ResponseEntity<>("Пароли не совпадают", HttpStatus.CONFLICT);
        }
        if (!userService.saveUser(userForm)){
            return new ResponseEntity<>("Пользователь с таким логином уже существует", HttpStatus.NOT_ACCEPTABLE);
        }

        userService.saveUser(userForm);

        return new ResponseEntity<>("redirect:/", HttpStatus.OK);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Выдает токен, если пользователь с таким логином существует и пароль корректен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Некорректные данные"),
            @ApiResponse(responseCode = "200", description = "Возвращает токен",
                    content = {@Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "token",
                                    value = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IiwiZXhwIjoxNjQwNzk3MjAwfQ"))}
            )}
    )
    @PostMapping("/auth")
    public Object auth(@RequestBody Login request) {
        User userEntity = userService.findByLoginAndPassword(request);
        if(userEntity == null){
            return new ResponseEntity<>("Некорректный логин или пароль", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtProvider.generateToken(userEntity.getLogin());
        String t = new AuthResponse(token).getToken();
        return new ResponseEntity<>(t, HttpStatus.OK);
    }
}
