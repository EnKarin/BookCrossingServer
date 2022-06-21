package ru.bookcrossing.bookcrossingserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.user.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
