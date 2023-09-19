package com.example.ClientSemestr5.DTO.DTOImpl;

import com.example.ClientSemestr5.Model.entity.User;
import com.example.ClientSemestr5.Model.tcp.Request;
import com.example.ClientSemestr5.Model.tcp.Response;
import com.example.ClientSemestr5.Model.entity.Faculty;
import com.google.gson.Gson;
import com.example.ClientSemestr5.DTO.DTO;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FacultyDtoImpl implements DTO<Faculty> {
    private Request request;
    private Response response;

    @Override
    public String createRequest(Faculty entity, RequestType requestType) {
        String result;
        Request request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }

    @Override
    public Faculty getResponseEntity(String result) {
        Faculty faculty;
        response = new Gson().fromJson(result, Response.class);
        faculty = new Gson().fromJson(response.getResponseMessage(), Faculty.class);
        return faculty;
    }

    @Override
    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result,Response.class);
        return response.getResponseType();
    }


    @Override
    public ArrayList<Faculty> getResponseEntityList(String result) {
        ArrayList<Faculty> faculties;
        Response response = new Gson().fromJson(result, Response.class);
        faculties = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<Faculty>>(){}.getType());
        return faculties;
    }
}
