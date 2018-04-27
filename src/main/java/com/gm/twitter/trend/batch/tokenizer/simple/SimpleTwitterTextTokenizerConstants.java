package com.gm.twitter.trend.batch.tokenizer.simple;

import java.util.regex.Pattern;

public class SimpleTwitterTextTokenizerConstants {
    // '^' is added since Java Regex uses '^word$' for exact matching 'word' in the string 'word',
    // not in the string 'abcword'
    public static Pattern Contractions = Pattern.compile("(?i)^(\\w+)(n't|'ve|'ll|'d|'re|'s|'m)$");
    public static Pattern Whitespace = Pattern.compile("\\s+");
    public static String punctChars = "['“\\\".?!,:;\\-\\(\\)]";
    public static String punctSeq = punctChars + "+";
    public static String entity = "&(amp|lt|gt|quot);";
    // Abbreviations
    public static String boundaryNotDot = "($|\\s|[“\\\"?!,:;]|" + entity + ")";
    public static String aa2 = "[^A-Za-z]([A-Za-z]\\.){1,}[A-Za-z](?=" + boundaryNotDot + ")";
    public static String aa1 = "([A-Za-z]\\.){2,}(?=" + boundaryNotDot + ")";
    public static String urlExtraCrapBeforeEnd = "(" + punctChars + "|" + entity + ")+?";
    //  URLs
    public static String urlStart1 = "(https?://|www\\.)";
    public static String commonTLDs = "(com|co\\.uk|org|net|info|ca|ly|eu)";
    public static String urlStart2 = "[A-Za-z0-9\\.-]+?\\." + commonTLDs + "(?=[/ \\W])";
    public static String urlBody = "[^ \\t\\r\\n<>]*?";
    public static String urlEnd = "(\\.\\.+|[<>]|\\s|$)";
    public static String url = "\\b(" + urlStart1 + "|" + urlStart2 + ")" + urlBody + "(?=(" + urlExtraCrapBeforeEnd + ")?" + urlEnd + ")";
    // Numeric
    public static String timeLike = "\\d+:\\d+";
    public static String num = "\\d+";
    public static String numNum = "\\d+\\.\\d+";
    public static String numberWithCommas = "(\\d+,)+?\\d{3}" + "(?=([^,]|$))";
    // 'Smart Quotes' (http://en.wikipedia.org/wiki/Smart_quotes)
    public static String edgePunctChars = "'\\\"“”‘’<>«»{}\\(\\)\\[\\]";
    public static String edgePunct = "[" + edgePunctChars + "]";
    public static String notEdgePunct = "[a-zA-Z0-9]";
    public static Pattern EdgePunctRight = Pattern.compile("(" + notEdgePunct + ")(" + edgePunct + "+)(\\s|$)");
    public static Pattern EdgePunctLeft = Pattern.compile("(\\s|^)(" + edgePunct + "+)(" + notEdgePunct + ")");
    public static String standardAbbreviations = "\\b([Mm]r|[Mm]rs|[Mm]s|[Dd]r|[Ss]r|[Jj]r|[Rr]ep|[Ss]en|[Ss]t)\\.";
    public static String arbitraryAbbrev = "(" + aa1 + "|" + aa2 + "|" + standardAbbreviations + ")";
    public static String separators = "(--+|―)";
    public static String thingsThatSplitWords = "[^\\s\\.,]";
    public static String embeddedApostrophe = thingsThatSplitWords + "+'" + thingsThatSplitWords + "+";
    // Delimiters
    public static Pattern Protected = Pattern.compile(
            "("
                    + url + "|"
                    + entity + "|"
                    + timeLike + "|"
                    + numNum + "|"
                    + num + "|"
                    + numberWithCommas + "|"
                    + punctSeq + "|"
                    + arbitraryAbbrev + "|"
                    + separators + "|"
                    + embeddedApostrophe + ")");
}
