package streams;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StringExercises {
    private final List<String> strings = Arrays.asList("this", "is", "a",
            "list", "of", "strings");

    @Test
    public void stringLengthSort_InnerClass() {     // Java 5, 6, 7
        strings.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        });
        System.out.println(strings);
    }

    @Test
    public void stringLengthSort_lambda() {
        // Use lambda for the Comparator (reverse sort)
        Collections.sort(strings, (s1, s2) -> s2.length() - s1.length());
        System.out.println(strings);
        // Use the "sorted" method on Stream
        List<String> sorted = strings.stream()
                .sorted((s1, s2) -> s1.length() - s2.length())
                .collect(Collectors.toList());
        System.out.println(sorted);
        System.out.println(strings);
    }

    private int compareStrings(String s1, String s2) {
        return s1.length() - s2.length();
    }

    @Test  // Use a lambda that calls 'compareStrings' directly
    public void stringLengthSort_methodCall() {
        List<String> sorted = strings.stream()
                .sorted((s1, s2) -> compareStrings(s1, s2))
                .collect(Collectors.toList());
        System.out.println(sorted);
    }

    @Test  // Use a method ref to 'compareStrings'
    public void stringLengthSort_methodRef() {
        List<String> sorted = strings.stream()
                .sorted(this::compareStrings)
                .collect(Collectors.toList());
        System.out.println(sorted);
    }

    @Test  // Use Comparator.comparingInt
    public void stringLengthSort_comparingInt() {
        List<String> sorted = strings.stream()
                .sorted(Comparator.comparingInt(String::length)
                        .thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
        System.out.println(sorted);
    }

    @Test
    public void demoCollectors() {
        // Get only strings of even length
        // Add them to a LinkedList
        List<String> evens = strings.stream()
                .filter(s -> s.length() % 2 == 0)
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println(evens);

        // Add the strings to a map of string to length
        Map<String, Integer> map = strings.stream()
                //.collect(Collectors.toMap(s -> s, String::length));
                .collect(Collectors.toMap(Function.identity(), String::length));
        System.out.println(map);

        List<String> stringsWithNulls = Arrays.asList("this", null, "is", "a",
                null, "list", null, null, "with", "nulls", null);
        // Filter out nulls, then print even-length strings
        stringsWithNulls.stream()
                .filter(s -> s != null && s.length() % 2 == 0)  // short-circuiting &&
                .forEach(System.out::println);

        stringsWithNulls.stream()
                .filter(s -> s != null)            // short-circuiting filter
                .filter(s -> s.length() % 2 == 0)
                .forEach(System.out::println);

        stringsWithNulls.stream()
                .peek(s -> System.out.println("Before null filter: " + s))
                .filter(Objects::nonNull)
                .peek(s -> System.out.println("After null filter, before length filter: " + s))
                .filter(s -> s.length() % 2 == 0)
                .peek(s -> System.out.println("After length filter: " + s))
                .forEach(System.out::println);

        // Combine the two predicates and use the result to print non-null, even-length strings
        Predicate<String> nullFilter = Objects::nonNull;
        Predicate<String> evenFilter = s -> s.length() % 2 == 0;

        stringsWithNulls.stream()
                .filter(nullFilter.and(evenFilter))  // composition
                .forEach(System.out::println);

        Logger logger = Logger.getLogger(StringExercises.class.getName());
        Consumer<String> logConsumer = logger::info;
        Consumer<String> consolePrinter = System.out::println;
        stringsWithNulls.stream()
                .filter(nullFilter.and(evenFilter))
                .forEach(logConsumer.andThen(consolePrinter));
    }

}
