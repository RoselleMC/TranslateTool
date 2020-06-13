package com.iroselle.TranslateTool.bukkit.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUtils {

    public static String ReadFile(File Path) {
        BufferedReader reader = null;
        StringBuilder lastStr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                lastStr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lastStr.toString();
    }

    public static void writeText(File Path, String content, boolean isAppend) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            outputStream = new FileOutputStream(Path,isAppend);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void writeText(File Path, List<String> contents, boolean isAppend) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            outputStream = new FileOutputStream(Path,isAppend);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            StringBuilder stringBuilder = new StringBuilder();
            for (String content : contents) {
                stringBuilder.append(content).append("\n");
            }
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}
