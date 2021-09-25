package DSPPTest.util.Parser;

import java.util.*;

/**
 * Parser of lists
 */
public class ListParser {

    // delimiter between records
    protected String recordDelimiter = "\n|\r\n";

    public ListParser() {
    }

    public ListParser(String recordDelimiter) {
        this.recordDelimiter = recordDelimiter;
    }

    /**
     * Parse a string to a list of values
     *
     * @param str the string need to be parsed
     * @return a list of values
     */
    public List<String> parse(String str) {
        String[] records = str.split(recordDelimiter);
        List<String> ret = new ArrayList<>(records.length);
        Collections.addAll(ret, records);
        return ret;
    }

}
