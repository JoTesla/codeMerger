import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static final List<String> TEXT_EXTENSIONS = Arrays.asList(
            ".txt", ".md", ".java", ".kt", ".xml", ".json", ".yml", ".yaml",
            ".csv", ".html", ".css", ".js", ".properties", ".gradle", ".kts"
    );

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java FileMerger <directory> [output_file]");
            return;
        }

        String directoryPath = args[0];
        String outputFilePath = args.length > 1 ? args[1] : "merged_output.txt";

        List<Path> textFiles = new ArrayList<>();

        // Собираем файлы
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter( f -> !Files.isExecutable(f) )
                    .filter(path -> !path.getFileName().toString().equalsIgnoreCase(".DS_Store") &&
                            !path.getFileName().toString().endsWith("json"))
//                    .filter(Main::hasTextExtension)
                    .forEach(textFiles::add);
        } catch (IOException e) {
            System.err.println("Ошибка при обходе директории: " + e.getMessage());
            return;
        }

        if (textFiles.isEmpty()) {
            System.out.println("Не найдено текстовых файлов.");
            return;
        }

        // Записываем файлы в выходной файл
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath), StandardCharsets.UTF_8)) {
            for (Path file : textFiles) {
                writer.write("\n=== " + file.toString() + " ===\n\n");
                Files.lines(file, StandardCharsets.UTF_8).forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        System.err.println("Ошибка записи файла: " + file);
                    }
                });
            }
            System.out.println("Файлы объединены в " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Ошибка записи в выходной файл: " + e.getMessage());
        }
    }

    // Проверяет расширение файла
    private static boolean hasTextExtension(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return TEXT_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }
}
