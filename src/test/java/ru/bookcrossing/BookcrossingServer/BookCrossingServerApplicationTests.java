package ru.bookcrossing.BookcrossingServer;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=BookCrossingServerApplication.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookCrossingServerApplicationTests {

}
