package com.example.ClientSemestr5.DTO;

import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Enum.RequestType;

import java.util.ArrayList;
import java.util.List;


public interface DTO<T> {

    String createRequest(T entity, RequestType requestType);

    T getResponseEntity(String result);

    ResponseType getResponseType(String result);

    public ArrayList<T> getResponseEntityList(String result);
}