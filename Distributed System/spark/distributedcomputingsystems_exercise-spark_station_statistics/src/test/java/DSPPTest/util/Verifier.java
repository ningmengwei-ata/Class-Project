package DSPPTest.util;

import DSPPTest.util.Parser.DoubleListParser;
import DSPPTest.util.Parser.KVNumParser;
import DSPPTest.util.Parser.KVParser;
import DSPPTest.util.Parser.ListParser;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Utils used to verify the sameness between 2 strings of key-value pairs
 */
public class Verifier {

    /**
     * Parse the output and the answer with the default parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of KV pairs need to be checked
     * @param answer a string of KV pairs as an answer
     */
    public static void verifyKV(String output, String answer) throws Exception {
        KVParser parser = new KVParser();
        verifyKV(output, answer, parser, parser);
    }

    /**
     * Parse the output and the answer with the parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of KV pairs need to be checked
     * @param answer a string of KV pairs as an answer
     * @param parser the parser of the output and the answer
     */
    public static void verifyKV(String output, String answer, KVParser parser) throws Exception {
        verifyKV(output, answer, parser, parser);
    }

    /**
     * Parse the output and the answer with the parsers; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output       a string of KV pairs need to be checked
     * @param answer       a string of KV pairs as an answer
     * @param outputParser the parser of the output
     * @param answerParser the parser of the answer
     */
    public static void verifyKV(String output, String answer, KVParser outputParser, KVParser answerParser)
            throws Exception {
        Map outputMap = outputParser.parse(output);
        Map answerMap = answerParser.parse(answer);

        assertEquals(answerMap.size(), outputMap.size());

        for (Object key : answerMap.keySet()) {
            assertEquals(answerMap.get(key), outputMap.get(key));
        }
    }

    /**
     * Parse the output and the answer with the default parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of KV pairs need to be checked
     * @param answer a string of KV pairs as an answer
     * @param delta  tolerable error
     */
    public static void verifyKV(String output, String answer, double delta) throws Exception {
        KVNumParser parser = new KVNumParser();
        verifyKV(output, answer, parser, parser, delta);
    }

    /**
     * Parse the output and the answer with the parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of KV pairs need to be checked
     * @param answer a string of KV pairs as an answer
     * @param parser the parser of the output and the answer
     * @param delta  tolerable error
     */
    public static void verifyKV(String output, String answer, KVNumParser parser, double delta) throws Exception {
        verifyKV(output, answer, parser, parser, delta);
    }

    /**
     * Parse the output and the answer with the parsers; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output       a string of KV pairs need to be checked
     * @param answer       a string of KV pairs as an answer
     * @param outputParser the parser of the output
     * @param answerParser the parser of the answer
     * @param delta        tolerable error
     */
    public static void verifyKV(String output, String answer, KVNumParser outputParser, KVNumParser answerParser,
                                double delta) throws Exception {
        Map<String, Double> outputMap = outputParser.parse(output);
        Map<String, Double> answerMap = answerParser.parse(answer);

        assertEquals(answerMap.size(), outputMap.size());

        for (String key : answerMap.keySet()) {
            assertEquals(answerMap.get(key), outputMap.get(key), delta);
        }
    }

    /**
     * Parse the output and the answer with the default parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of lists need to be checked
     * @param answer a string of lists as an answer
     */
    public static void verifyList(String output, String answer) {
        ListParser outputParser = new ListParser();
        ListParser answerParser = new ListParser();
        verifyList(output, answer, outputParser, answerParser);
    }

    /**
     * Parse the output and the answer with the default parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of lists need to be checked
     * @param answer a string of lists as an answer
     * @param parser the parser of the output and the answer
     */
    public static void verifyList(String output, String answer, ListParser parser) {
        verifyList(output, answer, parser, parser);
    }

    /**
     * Parse the output and the answer with the parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output       a string of lists need to be checked
     * @param answer       a string of lists as an answer
     * @param outputParser the parser of the output
     * @param answerParser the parser of the answer
     */
    public static void verifyList(String output, String answer, ListParser outputParser, ListParser answerParser) {
        List outputList = outputParser.parse(output);
        List answerList = answerParser.parse(answer);

        assertEquals(outputList.size(), answerList.size());

        boolean[] rmAnswer = new boolean[answerList.size()];
        for (Object o : outputList) {
            boolean found = false;
            for (int i = 0; i < answerList.size(); i++) {
                if (!rmAnswer[i] && o.equals(answerList.get(i))) {
                    rmAnswer[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new AssertionError();
            }
        }
    }

    /**
     * Parse the output and the answer with the parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output a string of lists need to be checked
     * @param answer a string of lists as an answer
     * @param parser the parser of the output
     * @param delta  tolerable error
     */
    public static void verifyDoubleList(String output, String answer, DoubleListParser parser, double delta) {
        verifyDoubleList(output, answer, parser, parser, delta);
    }

    /**
     * Parse the output and the answer with the parser; verify if the output is as same as the answer.
     * An error is thrown if not passed.
     *
     * @param output       a string of lists need to be checked
     * @param answer       a string of lists as an answer
     * @param outputParser the parser of the output
     * @param answerParser the parser of the answer
     * @param delta        tolerable error
     */
    public static void verifyDoubleList(String output, String answer, DoubleListParser outputParser,
                                        DoubleListParser answerParser, double delta) {
        double[][] outputList = outputParser.parse(output);
        double[][] answerList = answerParser.parse(answer);

        assertEquals(outputList.length, answerList.length);

        boolean[] rmAnswer = new boolean[answerList.length];
        for (double[] o : outputList) {
            boolean found = false;
            for (int i = 0; i < answerList.length; i++) {
                if (!rmAnswer[i]) {
                    boolean notSatisfied = false;
                    for (int j = 0; j < o.length; j++) {
                        if (Math.abs(o[j] - answerList[i][j]) > delta) {
                            notSatisfied = true;
                            break;
                        }
                    }
                    if (!notSatisfied) {
                        rmAnswer[i] = true;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                throw new AssertionError();
            }
        }
    }

}
