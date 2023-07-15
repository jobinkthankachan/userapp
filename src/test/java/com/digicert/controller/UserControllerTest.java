package com.digicert.controller;

import com.digicert.dto.UserDTO;
import com.digicert.exception.UserNotFoundException;
import com.digicert.model.User;
import com.digicert.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserController userController;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }


    @Test
    void getAllUsers() throws JsonProcessingException {
        List<UserDTO> userList = new ArrayList<>();
        userList.add(new UserDTO(1L, "Test", "Test", "test1@gmail.com"));
        userList.add(new UserDTO(2L, "Test", "Test", "test2@gmail.com"));
        userList.add(new UserDTO(3L, "Test", "Test", "test3@gmail.com"));
        userList.add(new UserDTO(4L, "Test", "Test", "test4@gmail.com"));

        when(userService.getAllUsers()).thenReturn(userList);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedjson = objectMapper.writeValueAsString(userList);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedjson));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserById_whenUserExist_thenReturnUser() throws Exception {
        UserDTO user = new UserDTO(1L, "Test1", "Test1", "test1@gmail.com");
        when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(user));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

    }

    @Test
    void getUserById_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        Long userId =1L;
        when(userService.getUserById(userId)).thenReturn(java.util.Optional.empty());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        } catch (Exception e) {
            if (e.getCause() instanceof UserNotFoundException) {

                String expectedErrorMessage = "User not found with id:" + userId;
                assertThat(e.getCause().getMessage()).isEqualTo(expectedErrorMessage);
            } else {
                // throwing exception since it's not UserNotFoundException
                throw e;
            }
        }
    }


    @Test
    void createUser() throws Exception {
        UserDTO user = new UserDTO(1L,"Test1","Test1","test1@gmail.com");
        when(userService.createUser(any(UserDTO.class))).thenReturn(user);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(requestJson));

    }

    @Test
    void updateUser_whenUserExist_thenReturnOk() throws Exception {
        UserDTO user = new UserDTO(1L, "Test", "Test", "test1@gmail.com");
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(user);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(requestJson));

    }

    @Test
    void updateUser_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        Long userId = 1L;
        UserDTO user = new UserDTO(userId, "Test1", "Test1", "test1@gmail.com");
        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenThrow(UserNotFoundException.class);

        UserController userController = new UserController(userService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(user);

        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }catch (Exception e) {
            if (e.getCause() instanceof UserNotFoundException) {
                String expectedErrorMessage = "User not found with id:" + userId;
                assertThat(e.getCause().getMessage()).isEqualTo(expectedErrorMessage);
            } else {
                // throwing exception since it's not UserNotFoundException
                throw e;
            }
        }
    }

    @Test
    void deleteUser_whenUserExist_thenReturnNothing() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        //when there is a user that exist to delete
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());


    }

    @Test
    void deleteUser_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        //when user doesnt exist to delete , throw user not found exception
        Long userId = 1L;
        Mockito.doThrow(UserNotFoundException.class).when(userService).deleteUser(userId);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        } catch (Exception e) {
                if (e.getCause() instanceof UserNotFoundException) {
                    String expectedErrorMessage = "User not found with id:" + userId;
                    assertThat(e.getCause().getMessage()).isEqualTo(expectedErrorMessage);
                } else {
                    // throwing exception since it's not UserNotFoundException
                    throw e;
                }
            }
    }
}