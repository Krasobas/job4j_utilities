package ru.job4j.find;

import ru.job4j.util.ArgsName;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Find {
    private static final Logger LOG = LoggerFactory.getLogger(Find.class.getName());
    private final ArgsName argsName;

    public Find(String[] args) {
        this.argsName = validation(args);
    }

    private ArgsName validation(String[] args) {
        ArgsName argsName = ArgsName.of(args);
        String[] keys = {"d", "n", "t", "o"};
        List<String> types = List.of("name", "mask", "regex");
        StringJoiner tip = new StringJoiner(System.lineSeparator());
        tip.add("");
        tip.add("Try again using these keys: -d=root_directory -n=filename -t=search_type -o=target_file");
        tip.add("Three type of search are available: mask, name and regex");
        if (argsName.size() != 4) {
            throw new IllegalArgumentException("Expected 4 arguments but you entered "
                    + argsName.size()
                    + tip);
        }
        for (String key : keys) {
            if (!argsName.contains(key)) {
                throw new IllegalArgumentException("Illegal key was entered!" + tip);
            }
        }
        Path root = Path.of(argsName.get("d"));
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("The entered root directory does not exist!" + tip);
        }

        String type = argsName.get("t");
        if (!types.contains(type)) {
            throw new IllegalArgumentException("Illegal search type!" + tip);
        }

        String path = argsName.get("o");
        if (!Pattern.matches(".+\\.[a-z]+", path)) {
            throw new IllegalArgumentException("Illegal path to target file!" + tip);
        }
        return argsName;
    }

    private List<Path> search(Path root, Predicate<Path> condition) {
        SearchFiles searcher = new SearchFiles(condition);
        try {
            Files.walkFileTree(root, searcher);
        } catch (IOException e) {
            LOG.error("Impossible to walk the file tree.", e);
        }
        return searcher.getPaths();
    }

    private Predicate<Path> getPredicate() {
        String name = argsName.get("n");
        Predicate<Path> condition = p -> false;
        if ("name".equals(argsName.get("t"))) {
            condition = p -> p.toFile().getName().equals(name);
        } else if ("regex".equals(argsName.get("t"))) {
            condition = p -> Pattern.matches(name, p.toFile().getName());
        } else if ("mask".equals(argsName.get("t"))) {
            String mask = name.replaceAll("\\.", "\\\\.")
                              .replaceAll("\\?", ".")
                              .replaceAll("\\*", ".*");
            condition = p -> Pattern.matches(mask, p.toFile().getName());
        }
        return condition;
    }

    public void run() {
        Path root = Path.of(argsName.get("d"));
        String out = argsName.get("o");
        List<Path> rsl = search(root, getPredicate());
        rsl.forEach(System.out::println);
        try (PrintWriter writer = new PrintWriter(out)) {
            rsl.forEach(writer::println);
            writer.flush();
        } catch (IOException e) {
            LOG.error("Impossible to write the log into file.", e);
        }
    }

    public static void main(String[] args) {
        try {
            new Find(args).run();
        } catch (IllegalArgumentException e) {
            LOG.error("Impossible to run the app.", e);
        }
    }
}
