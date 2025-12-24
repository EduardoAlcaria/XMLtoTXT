package main;


import service.ConvertXMLToText;


import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static  int count = 0;
    public static int failedFiles = 0;

    public static void main(String[] args) throws IOException {

        Path inputFolder = Paths.get("input");
        Path outputFolder = Paths.get("output");


        if (Files.notExists(outputFolder)) {
            System.out.println(ANSI_YELLOW + "Creating the output folder " + ANSI_RESET);
            Files.createDirectories(outputFolder);
        }

        if (Files.notExists(inputFolder)) {
            System.out.println(ANSI_YELLOW + "Creating the input folder " + ANSI_RESET);
            Files.createDirectories(inputFolder);
            System.out.println(ANSI_YELLOW + "Please put your entity files in the 'input' folder" + ANSI_RESET);
        }


        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(inputFolder)){
            for (Path path : directoryStream) {
                try {
                    ConvertXMLToText.convert(path, outputFolder);
                    count++;
                } catch (Exception e) {
                    failedFiles++;
                    System.out.println("error: " + e.getMessage());
                }
            }
        }

        if (count > 0) {
            System.out.println(ANSI_GREEN +  "Successes " + count + ANSI_RESET
                    + ANSI_RED + "\nFailed " + failedFiles + ANSI_RESET);
        }

    }

}