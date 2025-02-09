package io.github.enkarin.bookcrossing.chat.repository;

import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, UsersCorrKey> {
    @Query("FROM Correspondence c WHERE c.usersCorrKey.firstUser = ?1 OR c.usersCorrKey.secondUser = ?1")
    List<Correspondence> findAllByUser(User user, Pageable pageable);
}
