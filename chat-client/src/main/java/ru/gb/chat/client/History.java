package ru.gb.chat.client;



import java.io.*;
import java.nio.charset.StandardCharsets;


public class History {

    private String login;
    private OutputStream out;



    public void init(String login){//подготовить
        try {
            this.login = login;
            this.out = new FileOutputStream(getFilename(), true);
        } catch (IOException e) {
            throw new RuntimeException("Проблема при работе в истории");
        }
    }


    public void write(String message) {
        try {
            out.write(message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Проблема при работе в истории");
        }
    }

    //метод кот. позволяет вычитать фаил истории
    //и получить строку, кот. подлепим сами

    public String load() {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(getFilename()))) {
            String str;
            while ((str = in.readLine()) != null) {//читаем построчно пока
                //не нарвемся на null
                builder.append(str).append("\n");//и в builder
                //это добавляем строку и перенос строки
            }

            return builder.toString();//в конце вернем builder.toString
        } catch (IOException e) {
            throw new RuntimeException("Проблема при работе в истории");

        }
    }

    public void close() {
        login = null;
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFilename() {
        return ("history" + login + ".txt");
    }
}
