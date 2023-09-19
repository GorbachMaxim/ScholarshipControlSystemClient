package com.example.ClientSemestr5.Model.entity;

import lombok.Data;


import java.util.List;


@Data
public class Subject {
    private int idSubject;

    private String name;

    private int hoursNumber;

    private List<Group> groups;
}
