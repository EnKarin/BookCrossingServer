package io.github.enkarin.bookcrossing.chat.model;

import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsersCorrKeyTest {

    @Test
    void testEqualsWithEquallyOrder() {
        final User firstUser = new User();
        firstUser.setUserId(11);
        final User secondUser = new User();
        secondUser.setUserId(22);
        final UsersCorrKey first = new UsersCorrKey();
        first.setFirstUser(firstUser);
        first.setSecondUser(secondUser);
        final UsersCorrKey second = new UsersCorrKey();
        second.setFirstUser(firstUser);
        second.setSecondUser(secondUser);

        assertThat(first.equals(second)).isTrue();
    }
}
