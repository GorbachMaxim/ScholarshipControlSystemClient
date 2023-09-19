package com.example.ClientSemestr5.Model.entity;


import lombok.Data;

import java.util.List;


@Data
public class Person {

    private int idPerson;

    private String firstname;

    private String lastname;

    private String patronymic;

    private List<User> users;


}
