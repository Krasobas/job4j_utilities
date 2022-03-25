package ru.job4j.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The instance of this class can be use at first to validate that
 * user input corresponds to the contract "key=value".
 * And secondly to keep this input as a map.
 */
public class ArgsName {
    /**
     * The map containing keys and values required by the main method
     */
    private final Map<String, String> values = new HashMap<>();
    /**
     * @return temp is the validated and split pair of a key and a value
     */
    private String[] validation(String arg) {
        if (!Pattern.matches("-[a-zA-Z]+=.+", arg)) {
            throw new IllegalArgumentException("Illegal command was entered! Use this template: -key=value");
        }
        String[] temp = arg.substring(1).split("=");
        if (temp.length != 2) {
            throw new IllegalArgumentException("Illegal command was entered! Use = only to separate key and value");
        }
        return temp;
    }

    /**
     * Under the hood uses validation method and puts its results into values map
     * @see this.validation
     * @see this.values
     */
    private void parse(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No any argument was entered!");
        }
        for (String arg : args) {
            String[] temp = validation(arg);
            values.put(temp[0], temp[1]);
        }

    }

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Illegal key!");
        }
        return values.get(key);
    }

    public int size() {
        return values.size();
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    /**
     * Under the hood creates this class instance and initialises it by using the parse method
     * @param args the arguments required by the main method
     * @return the created and initialised instance of this class
     */
    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }
}
