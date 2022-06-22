package io.github.enkarin.bookcrossing.chat.repository;

import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, UsersCorrKey> {
}
