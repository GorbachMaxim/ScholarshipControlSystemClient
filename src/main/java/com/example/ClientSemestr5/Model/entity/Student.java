package com.example.ClientSemestr5.Model.entity;

import lombok.Data;

@Data
public class Student {
    private int idStudent;

    private String firstname;

    private String lastname;

    private boolean budget;

    private String marks;

    private Group group;

    private Double averageMark;
}
