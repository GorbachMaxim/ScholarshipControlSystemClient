package com.example.ClientSemestr5.utility;


import com.example.ClientSemestr5.Model.entity.Mark;
import com.example.ClientSemestr5.Model.entity.Subject;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MarkGsonAdapter implements JsonSerializer<List<Mark>>, JsonDeserializer<ArrayList<Mark>>  {
    List<Subject> subjects;

    public MarkGsonAdapter(List<Subject> subjects){
        this.subjects = subjects;
    }

    public MarkGsonAdapter(){}

    public JsonElement serialize(List<Mark> marks, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        for(Mark m:marks){
            object.addProperty(String.valueOf(m.getSubjectId()), m.getMark());
        }

        return object;
    }

    @Override
    public ArrayList<Mark> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        ArrayList<Mark> list = new ArrayList<>();
        for(Subject subject : subjects){
            Mark mark = new Mark();
            mark.setSubjectId(subject.getIdSubject());
            mark.setMark(object.get(String.valueOf(subject.getIdSubject())).getAsInt());
            list.add(mark);
        }
        return list;
    }
}