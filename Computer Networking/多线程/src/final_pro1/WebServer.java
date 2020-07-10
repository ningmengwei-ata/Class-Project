package final_pro1;

import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer {
    public static final String shutdown_cmd = "/shutdown";
    public static boolean closeServer;
    public static void main(String argv[]) throws Exception {

        int port=8081;
        // 建立一个套接字
        ServerSocket socket = new ServerSocket(port);

        while (true) {
            try {
            // 监听一个tcp连接的request.
            Socket connection = socket.accept();

            // 处理HTTP请求消息。
            Request request = new Request(connection);
//            if(request.getfilename()==shutdown_cmd)
//                break;
            // 建立一个线程处理请求
            Thread thread = new Thread(request);
            thread.start();
            // 检查是否是关闭服务命令
            if (closeServer) {
                break;
            }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
