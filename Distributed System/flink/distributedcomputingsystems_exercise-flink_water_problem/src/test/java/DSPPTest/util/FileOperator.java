package DSPPTest.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utils of file operations
 */
public class FileOperator {

    /**
     * Read a local file.
     *
     * @param filePath file path
     * @return file content (encoded in UTF-8) if succeed; empty String otherwise
     */
    public static String readFile2String(String filePath) {
        try {
            short DEFAULT_BUFFER_SIZE = 1024;
            File file = new File(filePath);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            StringWriter writer = new StringWriter();
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n;

            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
            reader.close();

            return writer.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Read files in a folder.
     *
     * @param folderPath folder path
     * @return file content (encoded in UTF-8) if succeed; empty String otherwise
     */
    public static String readFolder2String(String folderPath) {
        try {
            StringBuilder ret = new StringBuilder();
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    ret.append(readFile2String(file.getAbsolutePath()));
                }
            }
            return ret.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get buffered reader
     *
     * @param filePath file path
     * @return buffered reader for the file
     */
    public static BufferedReader getBufferedReader(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
        return new BufferedReader(reader);
    }

    /**
     * Get buffered writer
     *
     * @param filePath file path
     * @return buffered writer for the file
     */
    public static BufferedWriter getBufferedWriter(String filePath) throws IOException {
        File f = new File(filePath);
        f.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(f);
        return new BufferedWriter(fw);
    }

    /**
     * Delete a file
     *
     * @param filePath file path
     * @return true if and only if the file or directory is successfully deleted; false otherwise
     */
    public static boolean deleteFolder(String filePath) {
        return FileUtils.deleteQuietly(new File(filePath));
    }

    public static void copyFile(String srcFilePath, String destFilePath) {
        try {
            FileUtils.copyFile(new File(srcFilePath), new File(destFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
