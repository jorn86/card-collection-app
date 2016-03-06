package org.hertsig.preconstructed;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class JsonFileLister {
    public static void main(String... args) throws IOException {
        Path base = Paths.get("src/main/resources");
        Path json = base.resolve("org/hertsig/preconstructed");
        try (BufferedWriter writer = Files.newBufferedWriter(json.resolve("list"), Charsets.UTF_8)) {
            Files.walkFileTree(json, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".json")) {
                        writer.append(json.relativize(file).toString().replace('\\', '/')).append("\n");
                    }
                    return super.visitFile(file, attrs);
                }
            });
        }
    }
}
