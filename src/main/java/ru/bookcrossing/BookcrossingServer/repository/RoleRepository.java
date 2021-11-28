package ru.bookcrossing.BookcrossingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.entity.UserRole;

public interface RoleRepository extends JpaRepository<UserRole, Integer> {
}
