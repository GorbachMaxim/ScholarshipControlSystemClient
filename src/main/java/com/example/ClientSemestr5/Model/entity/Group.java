package com.example.ClientSemestr5.Model.entity;


import lombok.Data;

import java.util.List;

@Data
public class Group{
    private int idGroup;

    private String name;

    private Faculty faculty;

    private List<Student> students;

    private List<Subject> subjects;
}
