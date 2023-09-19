package com.example.ClientSemestr5.Model.tcp;



import com.example.ClientSemestr5.Enum.RequestType;
import lombok.Data;

@Data
public class Request {

    private RequestType requestType;

    private String requestMessage;

    public Request(RequestType requestType, String requestMessage) {
        this.requestType = requestType;
        this.requestMessage = requestMessage;
    }
}
