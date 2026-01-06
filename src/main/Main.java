package main;


import service.ConvertXMLToText;


import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

class ListAllFiles extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        Main.files.add(file);
        return FileVisitResult.SKIP_SUBTREE;
    }
}

public class Main {
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_RESET = "\u001B[0m";
        public static int count = 0;
        public static int failedFiles = 0;
        public static List<Path> files = new ArrayList<>();



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

            Path outputConcat = Paths.get("outputConcat.txt");

            if (Files.notExists(outputConcat)){
                Files.createFile(outputConcat);
            }
            Files.walkFileTree(outputFolder, new ListAllFiles());



            for (Path path : files) {
                System.out.println(path.toAbsolutePath().toFile());
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputConcat.toFile(), true))) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }

        }


}