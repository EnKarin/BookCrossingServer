package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.books.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final ModelMapper mapper;

    public GenreDto[] findAllGenre() {
        return genreRepository.findAll().stream()
            .map(entity -> mapper.map(entity, GenreDto.class))
            .toArray(GenreDto[]::new);
    }
}
