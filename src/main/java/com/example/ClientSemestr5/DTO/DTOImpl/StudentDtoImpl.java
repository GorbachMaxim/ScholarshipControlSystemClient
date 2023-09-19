package com.example.ClientSemestr5.DTO.DTOImpl;

import com.example.ClientSemestr5.DTO.DTO;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Student;
import com.example.ClientSemestr5.Model.tcp.Request;
import com.example.ClientSemestr5.Model.tcp.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class StudentDtoImpl implements DTO<Student> {
    private Response response;
    private Request request;

    @Override
    public String createRequest(Student entity, RequestType requestType) {
        String result;
        request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }


    @Override
    public ArrayList<Student> getResponseEntityList(String result) {
        ArrayList<Student> students;
        Response response = new Gson().fromJson(result, Response.class);
        students = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<Student>>() {
        }.getType());
        return students;
    }

    @Override
    public Student getResponseEntity(String result) {
        Student student;
        response = new Gson().fromJson(result, Response.class);
        student = new Gson().fromJson(response.getResponseMessage(), Student.class);
        return student;
    }

    @Override
    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result, Response.class);
        return response.getResponseType();
    }
}
