package DSPPTest.util.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser of key-value pairs
 */
public class KVNumParser {

    // delimiter in a record
    protected String delimiter = ",";

    // delimiter between records
    protected String recordDelimiter = "\n|\r\n";

    public KVNumParser() {
    }

    public KVNumParser(String delimiter) {
        this.delimiter = delimiter;
    }

    public KVNumParser(String delimiter, String recordDelimiter) {
        this.delimiter = delimiter;
        this.recordDelimiter = recordDelimiter;
    }

    /**
     * Parse a string to a map of KV pairs
     *
     * @param str the string need to be parsed
     * @return a map of KV pairs
     * @throws Exception a duplicated key occurs
     */
    public Map<String, Double> parse(String str) throws Exception {
        Map<String, Double> ret = new HashMap<>();
        String[] records = str.split(recordDelimiter);
        for (String record : records) {
            String[] kv = record.split(delimiter);
            if (ret.containsKey(kv[0])) {
                throw new Exception("Duplicated key: " + kv[0]);
            }
            double v = Double.parseDouble(kv[1].replaceAll("[()\\[\\]{}]", ""));
            ret.put(kv[0], v);
        }
        return ret;
    }

}
