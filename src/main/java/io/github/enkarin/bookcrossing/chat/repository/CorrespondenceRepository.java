package io.github.enkarin.bookcrossing.chat.repository;

import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, UsersCorrKey> {
    @Query("FROM Correspondence c WHERE c.usersCorrKey.firstUser = ?1 OR c.usersCorrKey.secondUser = ?1")
    List<Correspondence> findAllByUser(User user, Pageable pageable);

    @Override
    @Query("FROM Correspondence c WHERE c.usersCorrKey.firstUser = :#{#usersCorrKey.firstUser} AND c.usersCorrKey.secondUser = :#{#usersCorrKey.secondUser} " +
        "OR c.usersCorrKey.firstUser = :#{#usersCorrKey.secondUser} AND c.usersCorrKey.secondUser = :#{#usersCorrKey.firstUser}")
    Optional<Correspondence> findById(@Param("usersCorrKey") UsersCorrKey usersCorrKey);
}
