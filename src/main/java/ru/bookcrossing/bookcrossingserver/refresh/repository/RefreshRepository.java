package ru.bookcrossing.bookcrossingserver.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.refresh.model.Refresh;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, String> {

    Optional<Refresh> findByUser(String user);
}
