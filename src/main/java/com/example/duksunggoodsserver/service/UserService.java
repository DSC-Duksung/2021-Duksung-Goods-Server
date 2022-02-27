package com.example.duksunggoodsserver.service;

import com.example.duksunggoodsserver.exception.CustomException;
import com.example.duksunggoodsserver.model.entity.User;
import com.example.duksunggoodsserver.repository.UserRepository;
import com.example.duksunggoodsserver.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public String signIn(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username);
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signUp(User user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setCreatedBy(user.getUsername());
            userRepository.save(user);
            return jwtTokenProvider.createToken(user.getUsername());
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent())
            return user;
        else
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }

    public Optional<User> getCurrentUser(HttpServletRequest req) {
        Optional<User> user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
        if (user.isPresent())
            return user;
        else
            throw new CustomException("Any user have logged in yet", HttpStatus.BAD_REQUEST);
    }

    public String refresh(String username) {
        return jwtTokenProvider.createToken(username);
    }

}
