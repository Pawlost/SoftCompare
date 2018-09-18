package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {

    public static String load(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        StringBuilder builder = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            builder.append(s);
        }

        return builder.toString();
    }

}
