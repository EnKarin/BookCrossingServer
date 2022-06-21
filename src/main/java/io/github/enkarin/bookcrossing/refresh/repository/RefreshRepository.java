package io.github.enkarin.bookcrossing.refresh.repository;

import io.github.enkarin.bookcrossing.refresh.model.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, String> {

    Optional<Refresh> findByUser(String user);
}
