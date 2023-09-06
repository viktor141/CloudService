package ru.vixtor.cloudservice.sevice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.vixtor.cloudservice.component.TokenProvider;
import ru.vixtor.cloudservice.dto.AuthorizationRequest;
import ru.vixtor.cloudservice.dto.AuthorizationResponse;
import ru.vixtor.cloudservice.repository.AuthorizationRepository;


@Slf4j
@Service
@AllArgsConstructor
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;
    private AuthenticationManager authenticationManager;
    private TokenProvider tokenProvider;
    private UserService userService;



    public AuthorizationResponse login(AuthorizationRequest authorizationRequest) {
        final String username = authorizationRequest.getLogin();
        final String password = authorizationRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = tokenProvider.generateToken(userDetails);
        authorizationRepository.putTokenAndUsername(token, username);
        log.info("User {} is authorized", username);
        return new AuthorizationResponse(token);
    }

    public void logout(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        final String username = authorizationRepository.getUserNameByToken(authToken);
        log.info("User {} logout", username);
        authorizationRepository.removeTokenAndUsernameByToken(authToken);

    }
}
