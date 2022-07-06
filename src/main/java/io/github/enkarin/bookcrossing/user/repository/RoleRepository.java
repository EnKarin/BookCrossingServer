package io.github.enkarin.bookcrossing.user.repository;

import io.github.enkarin.bookcrossing.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role getRoleByName(String name);
}
