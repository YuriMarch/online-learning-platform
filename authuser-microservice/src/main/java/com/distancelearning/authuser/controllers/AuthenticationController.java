package com.distancelearning.authuser.controllers;

import com.distancelearning.authuser.dtos.UserDto;
import com.distancelearning.authuser.enums.UserStatus;
import com.distancelearning.authuser.enums.UserType;
import com.distancelearning.authuser.models.User;
import com.distancelearning.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                               @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class)
                                               UserDto userDto) {
        log.debug("POST registerUser userDto received {}", userDto.toString());
        if (userService.existsByUsername(userDto.getUsername())) {
            log.warn("Username {} is already been used", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already been used.");
        } else if (userService.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} is already been used", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is already been used.");
        }

        var user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserType(UserType.STUDENT);
        user.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.save(user);
        log.debug("POST registerUser userId received {}", user.getUserId());
        log.info("User saved successfully userId: {}", user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
