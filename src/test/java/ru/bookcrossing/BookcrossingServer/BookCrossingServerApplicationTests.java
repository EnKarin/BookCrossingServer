package ru.bookcrossing.BookcrossingServer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=BookCrossingServerApplication.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookCrossingServerApplicationTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads() {
    }

    @Test
    void whenInputIsInvalid_thenThrowsException() {
        User user = new User();

        assertThrows(ConstraintViolationException.class, () -> {
            repository.save(user);
            entityManager.flush();
        });
    }

}
