package BoostMe.services.user;

import BoostMe.api.rest.v1.JwtRequest;
import BoostMe.dtos.RegistrationUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest);
    ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto);
}
