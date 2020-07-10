package exp5;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class client {
  //  private final int port;
//    private client.ServerResponser serverResponser;
//    private List<ClientHandler> serverHandlerList = new ArrayList<>();
//    InputStream inputStream = socket.getInputStream();
//    OutputStream outputStream = socket.getOutputStream();
//    PrintStream socketPrintStream = new PrintStream(outputStream);
//    BufferedReader socketInput = new BufferedReader(new
//            InputStreamReader(inputStream));
//    public client(int port) {
//        this.port = port;
//    }
//    public void start() throws IOException {
//        serverResponser = new Server.serverResponser(port);
//        serverResponser.start();
//    }
//
//    private class serverResponser extends Thread {
//        private ServerSocket client;
//        serverResponser(int port) throws IOException {
//            client = new ClientSocket(port);
//        }
//        @Override
//        public void run() {
//            super.run();
//            System.out.println("阻塞等待客户端连接中...");
//            for (; ; ) {
//                Socket client;
//                try {
//                    client = server.accept();
//                    ClientHandler clientHandler = new ClientHandler(client);
//                    //读取数据并打印
//                    clientHandler.start();
//                    clientHandlerList.add(clientHandler);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("客户端连接异常:" + e.getMessage());
//                }
//            }
//        }
//    }
}
