package com.gm.twitter.trend.batch.tokenizer.simple;

import com.gm.twitter.trend.batch.tokenizer.TwitterTextTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import static com.gm.twitter.trend.batch.tokenizer.simple.SimpleTwitterTextTokenizerConstants.Protected;
import static java.util.Arrays.asList;

/**
 * An implementaiton of Twitter topic tokenizer
 */
public class SimpleTwitterTextTokenizer implements TwitterTextTokenizer {

    /**
     * the tokenize method which filters out white spaces before using simpleTokenize()
     */
    @Override
    public List<String> tokenize(List<String> stopWords, String text) {
        return simpleTokenize(stopWords, squeezeWhitespace(text));
    }

    // Utility methods

    // 'foo' => ' foo '
    private String splitEdgePunct(String input) {
        Matcher splitLeftMatcher = SimpleTwitterTextTokenizerConstants.EdgePunctLeft.matcher(input);
        String splitLeft = splitLeftMatcher.replaceAll("$1$2 $3");

        Matcher splitRightMatcher = SimpleTwitterTextTokenizerConstants.EdgePunctRight.matcher(splitLeft);
        return splitRightMatcher.replaceAll("$1 $2$3");
    }

    // "foo   bar" => "foo bar"
    private String squeezeWhitespace(String input) {
        Matcher whitespaceMatcher = SimpleTwitterTextTokenizerConstants.Whitespace.matcher(input);
        return whitespaceMatcher.replaceAll(" ").trim();
    }

    // For special patterns
    private Vector<String> splitToken(String token) {
        Matcher contractionsMatcher = SimpleTwitterTextTokenizerConstants.Contractions.matcher(token);
        Vector<String> smallTokens = new Vector<String>();

        while (contractionsMatcher.find()) {
            // There should be only two groups in a match for Contractors pattern
            smallTokens.add(contractionsMatcher.group(1).trim());
            smallTokens.add(contractionsMatcher.group(2).trim());
        }

        // if can't find a match, return the original token
        if (smallTokens.size() == 0) {
            smallTokens.add(token.trim());
        }

        return smallTokens;
    }

    // simpleTokenize should be called after using squeezeWhitespace()
    private List<String> simpleTokenize(List<String> stopWords, String text) {

        // Do the no-brainers first
        String splitPunctText = splitEdgePunct(text);
        int textLength = splitPunctText.length();

        // Find the matches for subsequences that should be protected,
        // e.g. URLs, 1.0, U.N.K.L.E., 12:53
        Matcher protectedMatcher = Protected.matcher(splitPunctText);

        // The protected pattern should not be split.
        Vector<Integer> protectedPatterns = new Vector<Integer>();
        while (protectedMatcher.find()) {
            int start = protectedMatcher.start();
            int end = protectedMatcher.end();
            // if this badSpan is not empty
            if (start != end) {
                protectedPatterns.add(new Integer(start));
                protectedPatterns.add(new Integer(end));
            }
        }

        // Create a list of indices to create the "goods", which can be
        // split. We are taking "bad" spans like
        //     List((2,5), (8,10))
        // to create
        //    List(0, 2, 5, 8, 10, 12)
        // where, e.g., "12" here would be the textLength
        List<Integer> indices = new ArrayList<Integer>();
        // add index 0
        indices.add(new Integer(0));
        // add indices from protectedPatterns
        indices.addAll(protectedPatterns);
        // add index length -1
        indices.add(new Integer(textLength));

        // XXX: calculate splitGoods directly without computing 'goods'
        List<String> splitGoods = new ArrayList<String>();
        for (int i = 0; i < indices.size(); i += 2) {
            String strGood = splitPunctText.substring(indices.get(i), indices.get(i + 1));
            List<String> splitGood = asList(strGood.trim().split(" "));
            splitGoods.addAll(splitGood);
        }

        // split based on special patterns (like contractions) and remove all tokens are empty
        List<String> finalTokens = new ArrayList<String>();
        for (String str : splitGoods) {
            Vector<String> tokens = splitToken(str);
            // only add non-empty tokens
            for (String token : tokens) {
                if (!token.isEmpty() && token.length() > 1 && !stopWords.contains(token.toLowerCase())) {
                    finalTokens.add(token);
                }
            }
        }

        return finalTokens;
    }

}