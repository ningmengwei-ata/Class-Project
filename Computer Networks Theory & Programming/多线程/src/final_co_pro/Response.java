package finals;
import java.io.* ;
import java.net.* ;
import java.util.* ;


import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

/*
  HTTP Response = Status-Line
    *(( general-header | response-header | entity-header ) CRLF)
    CRLF
    [ message-body ]
    Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

public class Response {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;
    int lenghtStream;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
//            将web文件写入到OutputStream字节流中
            File file = new File(WebServer.WEB_ROOT, request.getUri());
            if (file.exists()) { //如果访问路径存在
                fis = new FileInputStream(file);
                lenghtStream = fis.available();//最大获取2GB,File的length()方法可获取大于2GB大小，或者用java.nio
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                if (ch != -1){//添加header
                    String headerMessage = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html; charset=UTF-8\r\n"
                            + "Content-Length: "+ lenghtStream +"\r\n" + "Connection: keep-alive"+"\r\n" + "\r\n";
                    byte[] newBytes = Util.byteSum(headerMessage,bytes);
                    output.write(newBytes, 0, newBytes.length);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                    while (ch != -1) {
                        output.write(bytes, 0, ch);
                        ch = fis.read(bytes, 0, BUFFER_SIZE);
                    }
                }
            } else {
                // file not found
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n" + "\r\n" + "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString());
        } finally {
            if (fis != null)
                fis.close();
        }
    }
}