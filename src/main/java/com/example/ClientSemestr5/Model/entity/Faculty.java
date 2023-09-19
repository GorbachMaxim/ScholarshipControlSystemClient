package com.example.ClientSemestr5.Model.entity;


import lombok.Data;


import java.util.List;

@Data
public class Faculty {

    private int idFaculty;

    private String facultyName;

    private List<User> users;
}
