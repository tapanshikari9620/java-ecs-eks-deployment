package com.example.demo.service;

import com.example.demo.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    public MessageResponse getMessage() {
        log.info("Processing message request");
        return new MessageResponse("Hello from Java Demo App!", "SUCCESS");
    }
}
