package ru.bookcrossing.BookcrossingServer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.user.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
