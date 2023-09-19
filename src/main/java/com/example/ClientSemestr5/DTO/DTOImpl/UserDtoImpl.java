package com.example.ClientSemestr5.DTO.DTOImpl;


import com.example.ClientSemestr5.DTO.DTO;
import com.example.ClientSemestr5.Model.tcp.Request;
import com.example.ClientSemestr5.Model.tcp.Response;
import com.example.ClientSemestr5.Model.entity.Scholarship;
import com.example.ClientSemestr5.Model.entity.User;
import com.google.gson.Gson;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class UserDtoImpl implements DTO<User> {
    private Response response;
    private Request request;

    @Override
    public String createRequest(User entity, RequestType requestType) {
        String result;
        request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }


    @Override
    public ArrayList<User> getResponseEntityList(String result) {
        ArrayList<User> users;
        Response response = new Gson().fromJson(result, Response.class);
        users = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<User>>(){}.getType());
        return users;
    }

    @Override
    public User getResponseEntity(String result) {
        User user;
        response = new Gson().fromJson(result, Response.class);
        user = new Gson().fromJson(response.getResponseMessage(), User.class);
        return user;
    }

    @Override
    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result,Response.class);
        return response.getResponseType();
    }
}
