package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.playerMatchService.PlayerMatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    PlayerRepository repository;

    @Mock
    FirebaseAuth firebaseAuth;

    @Mock
    PlayerMatchService playerMatchService;

    @Mock
    AuthenticationFacade authenticationFacade;

    PlayerService sut = new PlayerService(repository, firebaseAuth, PlayerMapper.INSTANCE, playerMatchService, authenticationFacade);

    @Test
    void givenValidEmail_whenIsValidEmail_thenReturnsTrue() {
        assertThat(sut.isValidEmail("test@example.com")).isTrue();
        assertThat(sut.isValidEmail("user.name+tag@subdomain.domain.com")).isTrue();
        assertThat(sut.isValidEmail("simple@example.org")).isTrue();
        assertThat(sut.isValidEmail("valid_email@domain.co.uk")).isTrue();
        assertThat(sut.isValidEmail("user123@domain.com")).isTrue();
        assertThat(sut.isValidEmail("user@sub.domain.com")).isTrue();
    }

    @Test
    void givenInvalidEmail_whenIsValidEmail_thenReturnsFalse() {
        assertThat(sut.isValidEmail("invalid-email")).isFalse();
        assertThat(sut.isValidEmail("user@domain")).isFalse();
        assertThat(sut.isValidEmail("user@.com")).isFalse();
        assertThat(sut.isValidEmail("a@b.c")).isFalse();
        assertThat(sut.isValidEmail("user@domain,com")).isFalse();
        assertThat(sut.isValidEmail("user@domain..com")).isFalse();
        assertThat(sut.isValidEmail("")).isFalse();
        assertThat(sut.isValidEmail(null)).isFalse();
        assertThat(sut.isValidEmail("user@domain.-com")).isFalse();
        assertThat(sut.isValidEmail(".user@domain.com")).isFalse();
        assertThat(sut.isValidEmail("user@domain.com.")).isFalse();
    }

}