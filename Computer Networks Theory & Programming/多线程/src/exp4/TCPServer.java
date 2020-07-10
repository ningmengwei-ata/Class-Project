package exp4;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class TCPServer {
    private static final int PORT = 9090;
    public static void main(String[] args) throws IOException {
        ServerSocket server = createServerSocket();
//配置一些参数
        initServerSocket(server);
       //绑定到本地端口
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(),
                PORT), 50);
        System.out.print("服务器已监听端口:");
        System.out.println(server.getInetAddress() + ":" +
                server.getLocalPort());
// 等待客户端连接
        for (; ; ) {
            System.out.println("阻塞等待客户端连接中...");
            Socket client = server.accept();
//在连接后，启动一个线程与客户端交互
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();
        }
    }
    private static ServerSocket createServerSocket() throws IOException {
 //创建基础的serverSocket
        ServerSocket serverSocket = new ServerSocket();
        return serverSocket;
    }
    private static void initServerSocket(ServerSocket serverSocket) throws
            IOException {
        //复用端口
        serverSocket.setReuseAddress(true);
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
    }
    //处理与客户端交互
    private static class ClientHandler extends Thread {
        private Socket socket;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接" + socket.getInetAddress() +
                    ":" + socket.getPort());
            try {
//得到socket输入输出流
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                byte[] bytes = new byte[1024];
                //int len = inputStream.read(bytes);
                //StringBuilder sb = new StringBuilder();
                //sb.append(new String(bytes, 0, len));
                //System.out.println("收到客户端消息：" + sb.toString());
                //while ((len = inputStream.read(bytes)) != -1&&inputStream.read(bytes)!='#') {
                 //   sb.append(new String(bytes, 0, len));
                  //  System.out.println("收到客户端消息：" + sb.toString());
//消息写回

              //  }
                for(int i=0;i<4;i++){
                    int len = inputStream.read(bytes);
                    StringBuilder sb = new StringBuilder();
                    sb.append(new String(bytes, 0, len));

                    System.out.println("收到客户端消息：" + sb.toString());
                    outputStream.write(sb.toString().toUpperCase().getBytes());
                }
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出：" + socket.getInetAddress() +
                    " :" + socket.getPort());
        }
    }
}
