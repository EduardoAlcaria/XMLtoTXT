package main;


import org.w3c.dom.*;
import service.ConvertXMLToText;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        Path outputFile = Paths.get("entityTXTConverted");
        Path inputFolder = Paths.get("entityToBeConverted");


        if (Files.notExists(outputFile)) {
            Files.createDirectories(outputFile);
        }

        if (Files.notExists(inputFolder)) {
            Files.createDirectories(inputFolder);
            System.out.println("Please put your entity files on the entityToBeConverted folder");
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(inputFolder)){
            for (Path path : directoryStream) {
                try {
                    ConvertXMLToText.convert(path, outputFile);
                } catch (Exception e) {
                    System.out.println("error: " + e.getMessage());

                }
            }
        }

        System.out.println("done");
    }

}