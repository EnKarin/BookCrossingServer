package io.github.enkarin.bookcrossing.user.repository;

import io.github.enkarin.bookcrossing.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    @Query(value = "select u.* from bookcrossing.t_user u " +
        "inner join bookcrossing.t_user_role ur on u.user_id = ur.user_id " +
        "inner join bookcrossing.t_role r on ur.role_id = r.role_id " +
        "where r.name = ? order by u.user_id", nativeQuery = true)
    List<User> findByUserRolesOrderByUserId(String roleName);
}
