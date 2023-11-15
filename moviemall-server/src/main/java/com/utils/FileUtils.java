package com.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    public static void writeFile(String directoryPath, String fileName, List<String> lines, boolean append) throws IOException {
        if (lines == null) return;
        Path filePath = Paths.get(directoryPath, fileName);
        Files.createDirectories(filePath.getParent());

        if (append) {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        }
    }

    public static void writeFile(String directoryPath, String fileName, String content, boolean append) throws IOException {
        if (content == null) return;
        writeFile(directoryPath, fileName, Collections.singletonList(content), append);
    }

    public static void clearFile(String directoryPath, String fileName) throws IOException {
        Path filePath = Paths.get(directoryPath, fileName);
        Files.write(filePath, Collections.emptyList(), StandardCharsets.UTF_8);
    }

    public static void clearDirectory(String directoryPath) throws IOException {
        Path dirPath = Paths.get(directoryPath);

        if (Files.isDirectory(dirPath)) {
            try (Stream<Path> paths = Files.list(dirPath)) {
                paths.forEach(path -> {
                    if (Files.isRegularFile(path)) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}