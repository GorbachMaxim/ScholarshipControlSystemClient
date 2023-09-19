
package com.example.ClientSemestr5.DTO.DTOImpl;


import com.example.ClientSemestr5.DTO.DTO;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Group;
import com.example.ClientSemestr5.Model.tcp.Request;
import com.example.ClientSemestr5.Model.tcp.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GroupDtoImpl implements DTO<Group> {
    private Response response;
    private Request request;

    @Override
    public String createRequest(Group entity, RequestType requestType) {
        String result;
        request = new Request(requestType, new Gson().toJson(entity));
        result = new Gson().toJson(request);
        return result;
    }


    @Override
    public ArrayList<Group> getResponseEntityList(String result) {
        ArrayList<Group> groups;
        Response response = new Gson().fromJson(result, Response.class);
        groups = new Gson().fromJson(response.getResponseMessage(), new TypeToken<List<Group>>(){}.getType());
        return groups;
    }

    @Override
    public Group getResponseEntity(String result) {
        Group group;
        response = new Gson().fromJson(result, Response.class);
        group = new Gson().fromJson(response.getResponseMessage(), Group.class);
        return group;
    }

    @Override
    public ResponseType getResponseType(String result) {
        response = new Gson().fromJson(result,Response.class);
        return response.getResponseType();
    }
}
