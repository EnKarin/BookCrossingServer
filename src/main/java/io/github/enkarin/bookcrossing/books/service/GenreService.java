package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.books.model.Genre;
import io.github.enkarin.bookcrossing.books.repository.GenreRepository;
import io.github.enkarin.bookcrossing.exception.LocaleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreDto[] findAllGenre(final String locale) {
        final Function<Genre, String> localeMapper = switch (locale) {
            case "ru" -> Genre::getRuName;
            case "eng" -> Genre::getEngName;
            default -> throw new LocaleNotFoundException();
        };
        return genreRepository.findAll().stream()
            .map(entity -> new GenreDto(entity.getId(), localeMapper.apply(entity)))
            .toArray(GenreDto[]::new);
    }
}
