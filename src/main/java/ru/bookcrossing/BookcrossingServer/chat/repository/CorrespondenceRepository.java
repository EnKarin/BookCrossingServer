package ru.bookcrossing.BookcrossingServer.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, UsersCorrKey> {
}
