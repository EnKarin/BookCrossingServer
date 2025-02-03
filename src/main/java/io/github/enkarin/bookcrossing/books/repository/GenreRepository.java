package io.github.enkarin.bookcrossing.books.repository;

import io.github.enkarin.bookcrossing.books.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
