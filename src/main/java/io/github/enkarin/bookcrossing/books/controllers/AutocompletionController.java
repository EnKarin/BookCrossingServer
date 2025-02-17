package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.service.AutocompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/autocompletion")
public class AutocompletionController {
    private final AutocompletionService autocompletionService;

    @GetMapping("/title")
    public String[] bookNameAutocompletion(@RequestParam final String name) {
        return autocompletionService.autocompleteBookName(name);
    }

    @GetMapping("/author")
    public String[] bookAuthorAutocompletion(@RequestParam final String name) {
        return autocompletionService.autocompleteBookAuthor(name);
    }
}
