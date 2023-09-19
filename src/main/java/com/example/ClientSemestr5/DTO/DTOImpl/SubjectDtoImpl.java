package com.example.ClientSemestr5.DTO.DTOImpl;


import com.example.ClientSemestr5.DTO.DTO;
import com.example.ClientSemestr5.Model.entity.Subject;
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

public class SubjectDtoImpl implements DTO<Subject> {
    private Response response;
    private Request request;

    @Override
    public String createRequest(Subject entity, RequestType requestType) {
        String result;
        request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }


    @Override
    public ArrayList<Subject> getResponseEntityList(String result) {
        ArrayList<Subject> subjects;
        Response response = new Gson().fromJson(result, Response.class);
        subjects = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<Subject>>(){}.getType());
        return subjects;
    }

    @Override
    public Subject getResponseEntity(String result) {
        Subject subject;
        response = new Gson().fromJson(result, Response.class);
        subject = new Gson().fromJson(response.getResponseMessage(), Subject.class);
        return subject;
    }

    @Override
    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result,Response.class);
        return response.getResponseType();
    }
}
