package com.example.demo.controller;

import com.example.demo.dto.MessageResponse;
import com.example.demo.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void homeReturnsApplicationInformation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("java-demo-app"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.javaVersion", notNullValue()));
    }

    @Test
    void healthReturnsUpStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void messageReturnsExpectedJson() throws Exception {
        when(messageService.getMessage()).thenReturn(
                new MessageResponse("Hello from Java Demo App!", "SUCCESS"));
        mockMvc.perform(get("/api/v1/message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from Java Demo App!"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void infoReturnsApplicationInformation() throws Exception {
        mockMvc.perform(get("/api/v1/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("java-demo-app"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.javaVersion", notNullValue()));
    }
}
