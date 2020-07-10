package finals;

import java.io.* ;
import java.net.* ;
import java.util.* ;

public class WebServer {

    /**
     * WEB_ROOT是HTML和其它文件存放的目录. 这里的WEB_ROOT为工作目录下的webroot目录
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator +"webfile";

    // 关闭服务命令
    private static final String shutdown_cmd = "/shutdown";

    public static void main(String[] args) {
        WebServer server = new WebServer();
        //等待连接请求
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8081;
        try {
            //服务器套接字对象
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待一个请求
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //等待连接，连接成功后，返回一个Socket对象
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建Request对象并解析
                Request request = new Request(input);
                request.parse();
                System.out.println(request.getUri());
                // 检查是否是关闭服务命令
                if (request.getUri().equals(shutdown_cmd)) {
                    break;
                }

                // 创建 Response 对象
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                // 关闭 socket 对象
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}