package finals;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;


import java.io.*;
import java.net.Socket;

public class serverThread implements Runnable {
    InputStream input;
    OutputStream output;
    Socket clientRequest;

    public serverThread(Socket socket) {
        this.clientRequest = socket;
        try{
            input = clientRequest.getInputStream() ;
            output = clientRequest.getOutputStream() ;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 创建Request对象并解析
        Request request = new Request(input);
        request.parse();
        if (request.getUri().equals(WebServer.SHUTDOWN_COMMAND)) {
            WebServer.closeServer = true;//接受关闭服务器指令
            try {
                clientRequest.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (WebServer.closeServer !=true){
            // 创建 Response 对象
            Response response = new Response(output);
            response.setRequest(request);
            try {
                response.sendStaticResource();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                // 关闭 socket 对象
                clientRequest.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

    }

}