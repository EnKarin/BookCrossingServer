package io.github.enkarin.bookcrossing.configuration;

import io.github.enkarin.bookcrossing.registation.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.user.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TypeMap<UserRegistrationDto, User> typeMap(@Autowired final ModelMapper modelMapper) {
        final TypeMap<UserRegistrationDto, User> userDtoMapper =
                modelMapper.createTypeMap(UserRegistrationDto.class, User.class);
        userDtoMapper.addMappings(ms -> {
            ms.skip(User::setUserRoles);
            ms.skip(User::setAccountNonLocked);
            ms.skip(User::setPassword);
            ms.skip(User::setEnabled);
        });
        return userDtoMapper;
    }
}
