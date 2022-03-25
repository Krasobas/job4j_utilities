package ru.job4j.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Search {

    public static void validation(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Please enter a root folder and a type of file to search!");
        }
        if (!Files.exists(Paths.get(args[0])) || !Files.isDirectory(Paths.get(args[0]))) {
            throw new IllegalArgumentException(args[0] + " doesn't exist or is not a directory!");
        }
        if (!Pattern.matches("[a-z]+", args[1])) {
            throw new IllegalArgumentException(args[1] + " is not a type!");
        }
    }

    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static void main(String[] args) throws IOException {
        validation(args);
        Path start = Paths.get(args[0]);
        search(start, p -> p.toFile().getName().endsWith(args[1])).forEach(System.out::println);
    }
}
