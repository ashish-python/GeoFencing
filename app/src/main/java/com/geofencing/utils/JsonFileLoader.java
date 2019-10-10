package com.geofencing.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class JsonFileLoader {
    public static String loadJsonFile(String fileName) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while((inputLine = in.readLine()) != null){
            sb.append(inputLine);
        }
        in.close();
        return sb.toString();
    }
}
