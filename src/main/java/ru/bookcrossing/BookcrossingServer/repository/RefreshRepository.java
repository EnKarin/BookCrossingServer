package ru.bookcrossing.BookcrossingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.entity.Refresh;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, String> {

    Optional<Refresh> findByUser(String user);
}
