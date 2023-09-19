package com.example.ClientSemestr5.Model.entity;


import lombok.Data;




@Data
public class Scholarship {

    private int idScholarship;

    private String mark;

    private double scholarshipAmount;

    private double coefficient;

    private int fromMark;

    private int toMark;
}
