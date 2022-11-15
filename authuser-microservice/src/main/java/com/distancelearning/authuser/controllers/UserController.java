package com.distancelearning.authuser.controllers;

import com.distancelearning.authuser.dtos.UserDto;
import com.distancelearning.authuser.models.User;
import com.distancelearning.authuser.services.UserService;
import com.distancelearning.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600) //Endpoint can be accessed from any origin to all methods in controller
@Log4j2
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                  @PageableDefault (page = 0, size = 5, sort = "userId",
                                                          direction = Sort.Direction.ASC) Pageable pageable,
                                                  @RequestParam(required = false) UUID courseId) {
        Page<User> userPage = null;
        if (courseId != null){
            userPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
        } else {
            userPage = userService.findAll(spec, pageable);
        }

        //Hateoas implementation
        if (!userPage.isEmpty()){
            for (User user : userPage.toList()){
                user.add(linkTo(methodOn(UserController.class).getUserById(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID userId) {
        Optional<User> userOptional = userService.findById(userId);
        return userOptional.<ResponseEntity<Object>>map(user -> ResponseEntity.status(HttpStatus.OK).body(user)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable UUID userId) {
        log.debug("DELETE deleteUserById userId {} received", userId);
        Optional<User> userOptional = userService.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else {
            userService.delete(userOptional.get());
            log.debug("DELETE deleteUserById userId received {}", userId);
            log.info("User successfully deleted userId: {}", userId);
            return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted.");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId,
                                             @RequestBody @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto) {
        log.debug("PUT updateUser userDto received {}", userDto.toString());
        Optional<User> userOptional = userService.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else {
            var user = userOptional.get();
            user.setFullName(userDto.getFullName());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setCpf(userDto.getCpf());
            user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(user);
            log.debug("PUT updateUser userId received {}", user.getUserId());
            log.info("User updated successfully userId: {}", user.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable UUID userId,
                                                 @RequestBody @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto) {
        Optional<User> userOptional = userService.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else if (!userOptional.get().getPassword().equals(userDto.getOldPassword())){
            log.warn("Error: Mismatched old password userId: {}", userOptional.get().getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password.");
        } else {
            var user = userOptional.get();
            user.setPassword(userDto.getPassword());
            user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(user);
            log.debug("PUT updatePassword userId received {}", user.getUserId());
            log.info("User password updated successfully userId: {}", user.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body("Password successfully updated.");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable UUID userId,
                                              @RequestBody @Validated(UserDto.UserView.ImagePut.class)
                                              @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto) {
        Optional<User> userOptional = userService.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else {
            var user = userOptional.get();
            user.setImageUrl(userDto.getImageUrl());
            user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(user);
            log.debug("PUT updateImage userId received {}", user.getUserId());
            log.info("User image updated successfully userId: {}", user.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
    }
}
