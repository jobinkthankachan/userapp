package com.digicert.service;

import com.digicert.dto.UserDTO;
import com.digicert.exception.UserNotFoundException;
import com.digicert.model.User;
import com.digicert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private User setUserObj(UserDTO userDTO){
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    private UserDTO transformUserToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
    private Optional<UserDTO> transformUserToUserDTO(Optional<User> optUser) {
        return optUser.map(user -> new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()));
    }
    public List<UserDTO> getAllUsers() {

        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(user -> {
                    UserDTO userDto = new UserDTO();
                    userDto.setId(user.getId());
                    userDto.setFirstName(user.getFirstName());
                    userDto.setLastName(user.getLastName());
                    userDto.setEmail(user.getEmail());
                    return userDto;
                })
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return transformUserToUserDTO(userRepository.findById(id));
    }

    public UserDTO createUser(UserDTO userDTO) {
        return transformUserToUserDTO(userRepository.save(setUserObj(userDTO)));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = setUserObj(userDTO);
        return userRepository.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return transformUserToUserDTO(userRepository.save(user));
                })
                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + id));
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with id:" + id);
        }
    }
}
