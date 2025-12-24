package service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class IOUtils {
    public static Integer countFiles(){
        AtomicInteger count = new AtomicInteger(0);

        Path outputFolder = Paths.get("entityTXTConverted");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputFolder)){
            stream.forEach(path -> count.getAndIncrement());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count.get();
    }
}
