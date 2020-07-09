package exp5;
import java.io.IOException;
import java.net.Socket;
// 处理与客户端交互
class ClientHandler extends Thread {
    private Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;
    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writeHandler = new
                ClientWriteHandler(socket.getOutputStream());
    }
    @Override
    public void run() {
        super.run();
        readHandler.start();
    }
    public void send(String str) {
        writeHandler.send(str);
    }
}
