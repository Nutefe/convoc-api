package com.cyberethik.convocapi.playload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiMessage {
    private HttpStatus status;
    private String message;

}
