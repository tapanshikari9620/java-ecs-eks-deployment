package com.example.demo.controller;

import com.example.demo.dto.AppInfoResponse;
import com.example.demo.dto.MessageResponse;
import com.example.demo.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final MessageService messageService;

    @Value("${app.name}")
    private String applicationName;

    @Value("${app.version}")
    private String applicationVersion;

    public ApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public AppInfoResponse home() {
        log.info("Application information requested");
        return appInfo();
    }

    @GetMapping("/health")
    public MessageResponse health() {
        return new MessageResponse("Application is running", "UP");
    }

    @GetMapping("/api/v1/message")
    public MessageResponse message() {
        return messageService.getMessage();
    }

    @GetMapping("/api/v1/info")
    public AppInfoResponse info() {
        return appInfo();
    }

    private AppInfoResponse appInfo() {
        return new AppInfoResponse(applicationName, applicationVersion,
                System.getProperty("java.version"));
    }
}
