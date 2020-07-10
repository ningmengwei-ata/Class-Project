package exp5;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Server {
    private final int port;
    private ClientListener clientListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<>();
    public Server(int port) {
        this.port = port;
    }
    public void start() throws IOException {
        clientListener = new ClientListener(port);
        clientListener.start();
    }
    public void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }
    private class ClientListener extends Thread {
        private ServerSocket server;
        ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
        }
        @Override
        public void run() {
            super.run();
            System.out.println("阻塞等待客户端连接中...");
            for (; ; ) {
                Socket client;
                try {
                    client = server.accept();
                    ClientHandler clientHandler = new ClientHandler(client);
                    //读取数据并打印
                    clientHandler.start();
                    clientHandlerList.add(clientHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端连接异常:" + e.getMessage());
                }
            }
        }
    }
}
