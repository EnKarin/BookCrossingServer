package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.service.ChatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/correspondence/chats")
public class ChatsController {
    private final ChatsService chatsService;

    @GetMapping
    public ChatInfo[] findAllChats(@RequestParam final int pageNumber, @RequestParam final int pageSize, Principal principal) {
        return chatsService.findAllChats(pageNumber, pageSize, principal.getName());
    }
}
