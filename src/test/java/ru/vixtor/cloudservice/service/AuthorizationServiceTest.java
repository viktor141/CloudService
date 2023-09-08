package ru.vixtor.cloudservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vixtor.cloudservice.component.TokenProvider;
import ru.vixtor.cloudservice.dto.AuthorizationRequest;
import ru.vixtor.cloudservice.dto.AuthorizationResponse;
import ru.vixtor.cloudservice.repository.AuthorizationRepository;
import ru.vixtor.cloudservice.sevice.AuthorizationService;
import ru.vixtor.cloudservice.sevice.UserService;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthorizationServiceTest {
    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private AuthorizationRepository authorizationRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserService userService;

    public static final String TOKEN = "token";
    public static final String LOGIN_1 = "login1";
    public static final String PASSWORD_1 = "p111";
    public static final AuthorizationResponse AUTHORIZATION_RESPONSE = new AuthorizationResponse(TOKEN);
    public static final AuthorizationRequest AUTHORIZATION_REQUEST = new AuthorizationRequest(LOGIN_1, PASSWORD_1);
    public static final UsernamePasswordAuthenticationToken USERNAME_PASSWORD_AUTHENTICATION_TOKEN = new UsernamePasswordAuthenticationToken(LOGIN_1, PASSWORD_1);
    public static final UserDetails USER_DETAILS_1 = User.builder()
            .username(LOGIN_1)
            .password(PASSWORD_1)
            .authorities(new ArrayList<>())
            .build();

    @Test
    void login() {
        Mockito.when(userService.loadUserByUsername(LOGIN_1)).thenReturn(USER_DETAILS_1);
        Mockito.when(tokenProvider.generateToken(USER_DETAILS_1)).thenReturn(TOKEN);
        Assertions.assertEquals(AUTHORIZATION_RESPONSE, new AuthorizationResponse( authorizationService.login(AUTHORIZATION_REQUEST)));
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
        Mockito.verify(authorizationRepository, Mockito.times(1)).putTokenAndUsername(TOKEN, LOGIN_1);
    }

    @Test
    void logout() {
        Mockito.when(authorizationRepository.getUserNameByToken(TOKEN)).thenReturn(LOGIN_1);
        authorizationService.logout(TOKEN);
        Mockito.verify(authorizationRepository, Mockito.times(1)).getUserNameByToken(TOKEN);
        Mockito.verify(authorizationRepository, Mockito.times(1)).removeTokenAndUsernameByToken(TOKEN);
    }
}
