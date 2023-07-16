package com.digicert.controller;

import com.digicert.dto.UserDTO;
import com.digicert.exception.UserNotFoundException;
import com.digicert.model.User;
import com.digicert.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
    private UserController userController;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }


    @Test
    @Order(1)
    void getAllUsers() throws JsonProcessingException {
        logger.info("***** Get All User Test Started *****");
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
        logger.info("***** Get All User Test Completed *****");
    }

    @Test
    @Order(2)
    void getUserById_whenUserExist_thenReturnUser() throws Exception {
        logger.info("***** getUserById_whenUserExist_thenReturnUser Test Started *****");
        UserDTO user = new UserDTO(1L, "Test1", "Test1", "test1@gmail.com");
        when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(user));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        logger.info("***** getUserById_whenUserExist_thenReturnUser Test Completed *****");
    }

    @Test
    @Order(3)
    void getUserById_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        logger.info("***** getUserById_whenUserNotExist_thenReturnUserNotFound Test Started *****");
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
        logger.info("***** getUserById_whenUserNotExist_thenReturnUserNotFound Test Completed *****");
    }


    @Test
    @Order(4)
    void createUser() throws Exception {
        logger.info("***** createUser Test Started *****");
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
        logger.info("***** createUser Test Completed *****");
    }

    @Test
    @Order(5)
    void updateUser_whenUserExist_thenReturnOk() throws Exception {
        logger.info("***** updateUser_whenUserExist_thenReturnOk Test Started *****");
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
        logger.info("***** updateUser_whenUserExist_thenReturnOk Test Completed *****");
    }

    @Test
    @Order(6)
    void updateUser_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        logger.info("***** updateUser_whenUserNotExist_thenReturnUserNotFound Test Started *****");
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
        logger.info("***** updateUser_whenUserNotExist_thenReturnUserNotFound Test Completed *****");
    }

    @Test
    @Order(7)
    void deleteUser_whenUserExist_thenReturnNothing() throws Exception {
        logger.info("***** deleteUser_whenUserExist_thenReturnNothing Test Started *****");
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        //when there is a user that exist to delete
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        logger.info("***** deleteUser_whenUserExist_thenReturnNothing Test Completed *****");
    }

    @Test
    @Order(8)
    void deleteUser_whenUserNotExist_thenReturnUserNotFound() throws Exception {
        logger.info("***** deleteUser_whenUserNotExist_thenReturnUserNotFound Test Started *****");
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
        logger.info("***** deleteUser_whenUserNotExist_thenReturnUserNotFound Test Completed *****");
    }
}