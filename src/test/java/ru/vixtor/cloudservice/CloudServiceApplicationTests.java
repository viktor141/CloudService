package ru.vixtor.cloudservice;

import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import ru.vixtor.cloudservice.controller.FileController;
import ru.vixtor.cloudservice.dto.AuthorizationRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudServiceApplicationTests {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    private static final String WRONG_LOGIN = "user5";
    private static final String LOGIN = "user1@netology.ru";
    private static final String PASSWORD = "123";
    private static final String ENDPOINT_LOGIN = "/login";
    private static final String ENDPOINT_LOGOUT = "/logout";
    private static AuthorizationRequest validRequest;
    private static AuthorizationRequest invalidRequest;
    private static final Gson gson = new Gson();

    @BeforeAll
    public static void beforeAll(){
        validRequest = new AuthorizationRequest(LOGIN, PASSWORD);
        invalidRequest = new AuthorizationRequest(WRONG_LOGIN, PASSWORD);
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post(ENDPOINT_LOGIN).contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(validRequest))).andExpect(status().isOk());
    }

    @Test
    void testLoginFail() throws Exception {
        mockMvc.perform(post(ENDPOINT_LOGIN).contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(invalidRequest))).andExpect(status().isUnauthorized());
    }

    @Test
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(post(ENDPOINT_LOGOUT).contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(validRequest))).andExpect(status().is3xxRedirection());
    }


    @Test
    void FilesControllerTest() {
        ServletContext servletContext = webApplicationContext.getServletContext();
        Assertions.assertNotNull(servletContext);
        Assertions.assertNotNull(webApplicationContext.getBean(FileController.class));
    }


}
