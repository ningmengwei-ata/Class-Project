package finals;
import java.io.* ;
import java.net.* ;
import java.util.* ;



import java.io.InputStream;
import java.io.IOException;

public class Request {
    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    //从InputStream中读取request信息，并从request中获取uri值
    public void parse() {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.print(request.toString());
        uri = parseUri(request.toString());
    }
    /*
     *
     * requestString形式如下：
     * GET /index.html HTTP/1.1
     * Host: localhost:8080
     * Connection: keep-alive
     * Cache-Control: max-age=0
     * ...
     * 该函数目的就是为了获取/index.html字符串
     */
    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }

    private String parseMethod(String requestString) {//GET、POST、HEAD、PUT、DELETE、TRACE、CONNECT、OPTIONS
        int index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            return requestString.substring(0, index1);
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

}