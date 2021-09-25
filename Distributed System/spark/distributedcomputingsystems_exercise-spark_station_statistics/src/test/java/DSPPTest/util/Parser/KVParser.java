package DSPPTest.util.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser of key-value pairs
 */
public class KVParser {

    // delimiter in a record
    protected String delimiter = ",";

    // delimiter between records
    protected String recordDelimiter = "\n|\r\n";

    public KVParser() {
    }

    public KVParser(String delimiter) {
        this.delimiter = delimiter;
    }

    public KVParser(String delimiter, String recordDelimiter) {
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
    public Map<String, String> parse(String str) throws Exception {
        Map<String, String> ret = new HashMap<>();
        String[] records = str.split(recordDelimiter);
        for (String record : records) {
            String[] kv = record.split(delimiter);
            if (ret.containsKey(kv[0])) {
                throw new Exception("Duplicated key: " + kv[0]);
            }
            ret.put(kv[0], kv[1]);
        }
        return ret;
    }

}
