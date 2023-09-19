package com.example.ClientSemestr5.utility;

import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SessionStorage {
    public static boolean isAdmin = true;
    public static String login;
    private static boolean darkTheme = false;

    public static void setLightTheme() {
        darkTheme = false;
        try(FileWriter writer = new FileWriter("settingsCache.txt"))
        {
            writer.write(String.valueOf(darkTheme));
            writer.flush();
        }
        catch (IOException e) {
            System.out.println("Ошибка записи в файл settingsCache.txt");
        }
    }
    public static void setDarkTheme() {
        darkTheme = true;

        try(FileWriter writer = new FileWriter("settingsCache.txt"))
        {
            writer.write(String.valueOf(darkTheme));
            writer.flush();
        }
        catch (IOException e) {
            System.out.println("ОШибка записи в файл settingsCache.txt");
        }
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }


    public static void init() {
        try(FileReader reader = new FileReader("settingsCache.txt"))
        {
            StringBuilder str = new StringBuilder();
            // читаем посимвольно
            int c;
            while((c=reader.read())!=-1){

                str.append((char)c);
            }
            darkTheme = Boolean.parseBoolean(str.toString());
        }
        catch(IOException ex){
            System.out.println("Ошибка записи из файла settingsCache.txt");
        }
    }

}
