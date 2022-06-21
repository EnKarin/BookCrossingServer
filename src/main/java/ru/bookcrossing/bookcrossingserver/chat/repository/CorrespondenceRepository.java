package ru.bookcrossing.bookcrossingserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.chat.model.Correspondence;
import ru.bookcrossing.bookcrossingserver.chat.model.UsersCorrKey;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, UsersCorrKey> {
}
