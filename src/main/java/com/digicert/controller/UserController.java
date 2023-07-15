package com.digicert.controller;

import com.digicert.dto.UserDTO;
import com.digicert.exception.UserException;
import com.digicert.exception.UserNotFoundException;
import com.digicert.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private static final String ERROR_MESSAGE="User not found with id:";

    // get all users from users table
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //get user by id , if not present then returns UserNotFound
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(ERROR_MESSAGE + id));
        return ResponseEntity.ok(userDTO);
    }

    //creates user , mandatory field added to capture all necessary fields
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // updates user , accepts id and UserDTO
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(ERROR_MESSAGE + id);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    //deletes user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(ERROR_MESSAGE + id);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

}
