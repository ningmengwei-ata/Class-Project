package DSPPTest.util.Parser;

/**
 * Parser of records with multiple dimensions
 */
public class DoubleListParser {

    // number of dimensions
    protected int dimNum = 1;

    // delimiter in a record
    protected String delimiter = ",";

    // delimiter between records
    protected String recordDelimiter = "\n|\r\n";

    public DoubleListParser() {
    }

    public DoubleListParser(int dimNum) {
        this.dimNum = dimNum;
    }

    public DoubleListParser(String delimiter) {
        this.delimiter = delimiter;
    }

    public DoubleListParser(int dimNum, String delimiter, String recordDelimiter) {
        this.dimNum = dimNum;
        this.delimiter = delimiter;
        this.recordDelimiter = recordDelimiter;
    }

    public double[][] parse(String str) {
        String[] records = str.split(recordDelimiter);
        double[][] ret = new double[records.length][dimNum];

        for (int i = 0; i < records.length; i++) {
            String[] v = records[i].split(delimiter);

            for (int j = 0; j < dimNum; j++) {
                ret[i][j] = Double.parseDouble(v[j]);
            }
        }

        return ret;
    }


}
