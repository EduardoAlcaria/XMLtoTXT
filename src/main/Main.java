package main;


import org.w3c.dom.*;
import service.ConvertXMLToText;
import service.IOUtils;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static int failedFiles = 0;

    public static void main(String[] args) throws IOException {

        Path inputFolder = Paths.get("entityToBeConverted");
        Path outputFile = Paths.get("entityTXTConverted");


        if (Files.notExists(outputFile)) {
            Files.createDirectories(outputFile);
        }

        if (Files.notExists(inputFolder)) {
            Files.createDirectories(inputFolder);
            System.out.println(ANSI_YELLOW + "Please put your entity files on the 'entityToBeConverted' folder" + ANSI_RESET);
        }


        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(inputFolder)){
            for (Path path : directoryStream) {
                try {
                    ConvertXMLToText.convert(path, outputFile);
                } catch (Exception e) {
                    failedFiles++;
                    System.out.println("error: " + e.getMessage());
                }
            }
        }
        if (Files.size(inputFolder) > 0) {
            System.out.println(ANSI_GREEN +  "Successes " + IOUtils.countFiles() + ANSI_RESET
                    + ANSI_RED + "\nFailed " + failedFiles + ANSI_RESET);
        }

    }

}