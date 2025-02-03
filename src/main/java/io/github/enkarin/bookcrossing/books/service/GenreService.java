package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.books.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreDto findAllGenre() {
        return genreRepository.findAll();
    }
}
