package com.example.ClientSemestr5.DTO;

import com.example.ClientSemestr5.Model.entity.Scholarship;
import com.example.ClientSemestr5.Model.tcp.Request;
import com.example.ClientSemestr5.Model.tcp.Response;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ScholarshipListDto {

    private Response response;
    private Request request;

    public String createRequest(List<Scholarship> entity, RequestType requestType) {
        String result;
        request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }


    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result,Response.class);
        return response.getResponseType();
    }


    public List<Scholarship> getResponseEntity(String result) {
        ArrayList<Scholarship> scholarships;
        Response response = new Gson().fromJson(result, Response.class);
        scholarships = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<Scholarship>>(){}.getType());
        return scholarships;
    }








    }
