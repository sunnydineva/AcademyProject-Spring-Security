package com.example.spring.service;

import com.example.spring.config.JwtService;
import com.example.spring.dto.LoginRequest;
import com.example.spring.dto.LoginResponse;
import com.example.spring.dto.RegisterRequest;
import com.example.spring.dto.RegisterResponse;
import com.example.spring.entity.Role;
import com.example.spring.entity.User;
import com.example.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
// public interface UserService extends UserDetailsService 
// ще трябва имплементираме loadUserByUsername(String username)

   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest registerRequest){
        User toSave = User.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER) //no right to choose at the moment
                .build();

        User savedUser = userRepository.save(toSave);
        String token = jwtService.generateJwt(savedUser);
        return RegisterResponse.builder().token(token).build();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateJwt(user);
        return  LoginResponse.builder().token(jwtToken).build();
    }
}
