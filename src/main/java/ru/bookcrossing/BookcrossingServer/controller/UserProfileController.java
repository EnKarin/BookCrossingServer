package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.request.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.model.response.UserDTOResponse;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Tag(
        name="Получение профиля пользователей",
        description="Позволяет получить профили пользователей или редактировать свой"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/")
public class UserProfileController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserProfileController(UserService user, JwtProvider jwt,
                                 BCryptPasswordEncoder bCrypt) {
        userService = user;
        jwtProvider = jwt;
    }

    @Operation(
            summary = "Получение профиля",
            description = "Возвращает данные профиля по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTOResponse.class))})
    }
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam @Parameter(description = "Идентификатор пользователя," +
            " -1 для собственного") int id){
        UserDTOResponse userDTOResponse = null;
        Optional<User> user;
        if(id == -1){
             user = userService.findByLogin(jwtProvider.getLoginFromToken());
             userDTOResponse = new UserDTOResponse(user.get());
        }
        else{
            user = userService.findById(id);
            if(user.isPresent()) {
                userDTOResponse = new UserDTOResponse(user.get());
            }
            else{
                return new ResponseEntity<>("Пользователь не найден", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(userDTOResponse);
    }

    @Operation(
            summary = "Изменение данных пользователя",
            description = "Возвращает обновленный профиль"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Пароли не совпадают"),
            @ApiResponse(responseCode = "403", description = "Введен неверный старый пароль"),
            @ApiResponse(responseCode = "400", description = "Введены неверные данные"),
            @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTOResponse.class))})
    }
    )
    @PutMapping("/profile")
    public ResponseEntity<?> putProfile(@Valid @RequestBody UserPutRequest userPutRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            StringBuilder allErrorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(f -> allErrorMessage.append(
                    Objects.requireNonNull(f.getDefaultMessage())).append("\n"));
            return new ResponseEntity<>(allErrorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        if (!userPutRequest.getNewPassword().equals(userPutRequest.getPasswordConfirm())) {
            return new ResponseEntity<>("Пароли не совпадают", HttpStatus.CONFLICT);
        }
        Optional<User> user = userService.putUserInfo(userPutRequest);
        if(user.isPresent()){
            return ResponseEntity.ok(new UserDTOResponse(user.get()));
        }
        return new ResponseEntity<>("Старый пароль неверен", HttpStatus.FORBIDDEN);
    }
}
