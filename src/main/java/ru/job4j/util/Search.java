package ru.job4j.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Search {
    private static final Logger LOG = LoggerFactory.getLogger(Search.class.getName());

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

    public static List<Path> search(Path root, Predicate<Path> condition) {
        SearchFiles searcher = new SearchFiles(condition);
        try {
            Files.walkFileTree(root, searcher);
        } catch (IOException e) {
            LOG.error("Impossible to walk file tree.", e);
        }
        return searcher.getPaths();
    }

    public static void main(String[] args) {
        try {
            validation(args);
        } catch (IllegalArgumentException e) {
            LOG.error("Illegal arguments were entered.", e);
        }
        Path start = Paths.get(args[0]);
        search(start, p -> p.toFile().getName().endsWith(args[1])).forEach(System.out::println);
    }
}
