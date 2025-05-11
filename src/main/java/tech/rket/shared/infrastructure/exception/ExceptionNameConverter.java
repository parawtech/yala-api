package tech.rket.shared.infrastructure.exception;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExceptionNameConverter {

    private static final List<String> POSITIVE_VERBS = Arrays.asList(
            "Is", "Are", "Was", "Were", "Will", "Would", "Should", "Shall", "May",
            "Might", "Have", "Had", "Has", "Can", "Could", "Do", "Does", "Did"
    );

    private static final List<String> STANDARD_NEGATIVE_VERBS = Arrays.asList(
            "IsNot", "AreNot", "WasNot", "WereNot", "WillNot", "WouldNot", "ShouldNot",
            "ShallNot", "MayNot", "MightNot", "HaveNot", "HadNot", "HasNot", "CanNot",
            "CouldNot", "DoNot", "DoesNot", "DidNot"
    );
    private static final List<String> INCOMPLETE_NEGATIVE_VERBS = Arrays.asList(
            "Isnt", "Arent", "Wasnt", "Werent", "Willnt", "Wouldnt", "Shouldnt",
            "Shallnt", "Maynt", "Mightnt", "Havent", "Hadnt", "Hasnt", "Cannt",
            "Couldnt", "Dont", "Doesnt", "Didnt"
    );

    private static final List<String> PREFIX_WORDS = Arrays.asList(
            "INVALID", "INCOMPLETE", "UNAUTHORIZED", "FAILED", "MISSING", "UNSUPPORTED",
            "UNAVAILABLE", "EXPIRED", "OVERLOADED", "FORBIDDEN", "CONFLICT", "TIMEOUT",
            "CORRUPTED", "EXCEEDED", "DISCONNECTED", "INVALIDATED", "LOCKED", "UNREACHABLE",
            "ABORTED", "REJECTED", "RESTRICTED", "UNRECOGNIZED", "DENIED", "DEPRECATED",
            "BROKEN", "INTERRUPTED", "UNPROCESSED", "UNSUCCESSFUL", "INACCESSIBLE", "MALFORMED",
            "UNHANDLED", "OVERDUE", "DUPLICATED", "UNINITIALIZED", "INCONSISTENT", "UNATTENDED",
            "UNAUTHORIZED", "UNRESOLVED", "UNSTABLE", "UNEXPECTED", "UNAVAILABLE", "UNDELIVERABLE",
            "UNRESPONSIVE", "UNRECOVERABLE", "UNREADABLE", "UNSUITABLE", "UNSYNCHRONIZED", "UNTRACEABLE",
            "UNTRUSTED", "UNSUPPORTED", "UNVERIFIED", "UNWARRANTED", "UNWORKABLE", "ABSENT", "INACTIVE",
            "INADEQUATE", "INCAPABLE", "INCOMPATIBLE", "INCORRECT", "INEFFECTIVE", "INSUFFICIENT",
            "INVALIDATED", "IRRELEVANT", "MALFUNCTIONED", "OVERDUE", "OVERWRITTEN", "TERMINATED",
            "UNAUTHORIZED", "UNPREPARED", "UNSUBSCRIBED", "UNRECOVERABLE", "UNRECOGNIZED", "UNREACHABLE",
            "INOPERATIVE", "DEPRECATED"
    );

    public static String convert(Class<? extends Throwable> clz) {
        return convert(clz.getSimpleName());
    }

    public static String convert(String className) {
        className = className.replaceAll("Exception$", "")
                .replaceAll("Error$", "")
                .replaceAll("Problem$", "");

        String lastPart = "";
        Verb findVerb = findVerb(className);
        if (findVerb != null) {
            lastPart = pascalToCamel(findVerb.standard + className.substring(findVerb.length + findVerb.start));
            className = className.substring(0, findVerb.start);
        }
        String[] parts = getPart(className);
        String partsJoined = String.join(".", parts).replaceAll("\\.\\.", ".");
        if (!lastPart.isBlank()) {
            return String.format("%s.%s", partsJoined, lastPart);
        } else {
            return partsJoined;
        }
    }

    public static String pascalToCamel(String pascalCase) {
        if (pascalCase == null || pascalCase.isEmpty()) {
            return pascalCase;
        }

        char firstChar = Character.toLowerCase(pascalCase.charAt(0));

        return firstChar + pascalCase.substring(1);
    }


    private static String[] getPart(String className) {
        String firstPart;
        String[] parts;

        className = className.replaceAll("_", ".").trim();
        String[] classWords = className.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        boolean isPrefix = PREFIX_WORDS.contains(classWords[0].toUpperCase());
        boolean hasMore = className.length() > classWords[0].length();
        if (isPrefix && hasMore) {
            firstPart = classWords[1];
            String[] classsDot = className.substring(className.indexOf(firstPart) + firstPart.length()).split("\\.");
            parts = new String[classsDot.length + 1];
            parts[parts.length - 1] = "Is" + classWords[0];
            System.arraycopy(classsDot, 0, parts, 0, parts.length - 1);
        } else {
            firstPart = classWords[0];
            parts = className.substring(firstPart.length()).split("\\.");
        }

        List<String> list = new ArrayList<>();
        list.add(firstPart);
        list.addAll(Arrays.asList(parts));
        return list.stream()
                .map(ExceptionNameConverter::pascalToCamel)
                .filter(s->!s.isBlank())
                .toArray(String[]::new);
    }

    private static Verb findVerb(String className) {
        int index;
        for (String v : STANDARD_NEGATIVE_VERBS) {
            if ((index = className.toUpperCase().indexOf(v.toUpperCase())) != -1) {
                return new Verb(index, v.length(), v);
            }
        }
        for (int i = 0; i < INCOMPLETE_NEGATIVE_VERBS.size(); i++) {
            String v = INCOMPLETE_NEGATIVE_VERBS.get(i);
            if ((index = className.toUpperCase().indexOf(v.toUpperCase())) != -1) {
                return new Verb(index, v.length(), STANDARD_NEGATIVE_VERBS.get(i));
            }
        }
        for (String v : POSITIVE_VERBS) {
            if ((index = className.indexOf(v)) != -1) {
                return new Verb(index, v.length(), v);
            }
        }
        return null;
    }

    @AllArgsConstructor
    private static class Verb {
        private final int start;
        private final int length;
        private final String standard;
    }
}
