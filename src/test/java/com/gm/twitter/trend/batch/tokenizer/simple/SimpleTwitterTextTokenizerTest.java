package com.gm.twitter.trend.batch.tokenizer.simple;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SimpleTwitterTextTokenizerTest {
    SimpleTwitterTextTokenizer tokenizer = new SimpleTwitterTextTokenizer();
    List<String> stopWords;

    @Before
    public void before() throws IOException{
        stopWords = readAllLines(get("in/IgnoreWords.txt"));
    }

    @Test
    public void testTokenizeWithNoExtraWhitespace() {

        List<String> tokens = tokenizer.tokenize(stopWords,"  words    whitespace  ");
        assertThat("The length of token should be 2", tokens.size(), is(2));
        assertThat(tokens.get(0), is("words"));
        assertThat(tokens.get(1), is("whitespace"));

    }

    @Test
    public void testTokenizeWithEdgePunctuation() {

        List<String> tokens = tokenizer.tokenize(stopWords,"  ' words '    whitespace  ");
        assertThat("The length of token should be 2", tokens.size(), is(2));
        assertThat(tokens.get(0), is("words"));
        assertThat(tokens.get(1), is("whitespace"));

    }

    @Test
    public void testTokenizeWithTime() {

        List<String> tokens = tokenizer.tokenize(stopWords,"  http://abc.com   12:23 words  ");
        assertThat("The length of token should be 1", tokens.size(), is(1));
        assertThat(tokens.get(0), is("words"));

    }

    @Test
    public void testTokenizeWithNumber() {

        List<String> tokens = tokenizer.tokenize(stopWords,"12  12.12 words 12,123 ");
        assertThat("The length of token should be 1", tokens.size(), is(1));
        assertThat(tokens.get(0), is("words"));

    }

    @Test
    public void testTokenizeWithAbbreviations() {

        List<String> tokens = tokenizer.tokenize(stopWords,"Mr. mrs. Ms. Sr. words Jr. ");
        assertThat("The length of token should be 1", tokens.size(), is(1));
        assertThat(tokens.get(0), is("words"));

    }

    @Test
    public void testTokenizeWithIgnoreWords() {

        List<String> tokens = tokenizer.tokenize(stopWords,"is was this we words you yourself ");
        assertThat("The length of token should be 1", tokens.size(), is(1));
        assertThat(tokens.get(0), is("words"));

    }

}
