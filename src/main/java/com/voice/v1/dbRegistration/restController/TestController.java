package com.voice.v1.dbRegistration.restController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestRestController {

    @GetMapping("/")
    public String getMessage(){
        return "hare krsna!";
    }
}