package com.example.ClientSemestr5.Model.tcp;

import com.example.ClientSemestr5.Enum.ResponseType;
import lombok.Data;

@Data
public class Response {
    private ResponseType responseType;
    private String responseMessage;

    public Response(ResponseType responseType, String responseMessage) {
        this.responseType = responseType;
        this.responseMessage = responseMessage;
    }
}
