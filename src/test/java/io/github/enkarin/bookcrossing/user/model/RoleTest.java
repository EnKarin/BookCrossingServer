package io.github.enkarin.bookcrossing.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void gettersShouldWork() {
        final Role role = new Role();
        assertThat(role.getAuthority())
                .isNull();
        role.setName("any_role_name");
        assertThat(role.getAuthority())
                .isEqualTo(role.getName())
                .isEqualTo("any_role_name");
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        final Role role1 = new Role();
        final Role role2 = new Role();

        assertThat(role1)
                .isEqualTo(role1);

        //noinspection AssertBetweenInconvertibleTypes
        assertThat(role1)
                .isNotEqualTo(new User());

        assertThat(role1)
                .isEqualTo(role2)
                .hasSameHashCodeAs(role2);

        role1.setRoleId(11);
        assertThat(role1)
                .isNotEqualTo(role2)
                .hasSameHashCodeAs(role2);
    }
}
