package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.Model.entity.Mark;
import com.example.ClientSemestr5.Model.entity.Student;
import com.example.ClientSemestr5.Model.entity.Subject;
import com.example.ClientSemestr5.utility.MarkGsonAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;

public class SubjectStatisticController extends Menu{

    @FXML
    private BarChart<Number, String> barChart;

    private double groupTotalMark = 0;
    private int groupMarkNumber = 0;

    public void setData(Subject subject){
        XYChart.Series dataSeries = new XYChart.Series();
        dataSeries.setName("Средняя оценка");
        subject.getGroups().forEach(group -> {
            groupTotalMark = 0;
            groupMarkNumber = 0;
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Student.class, new MarkGsonAdapter(group.getSubjects()));
            Gson gson = builder.create();
            group.getStudents().forEach(student -> {
                ArrayList<Mark> list = gson.fromJson(student.getMarks(), new TypeToken<List<Mark>>(){}.getType());
                if (list != null) {
                    for (Mark m : list) {
                        if (m.getMark() != 0) {
                            if (m.getSubjectId() == subject.getIdSubject()) {
                                groupTotalMark += m.getMark();
                                groupMarkNumber++;
                            }
                        }
                    }
                }
            });
            if(groupMarkNumber != 0) {
                Double averageMark = Precision.round(groupTotalMark / groupMarkNumber, 2);
                dataSeries.getData().add(new XYChart.Data("Группа: " + group.getName() + "\nОценка: " + averageMark, averageMark));
            }
        });
        barChart.getData().add(dataSeries);
        barChart.setTitle("Статистика оценок по предмету: " + subject.getName());
    }
}
