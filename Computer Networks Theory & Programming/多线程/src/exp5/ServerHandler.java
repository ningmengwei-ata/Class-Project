package exp5;
import java.io.IOException;
import java.net.Socket;
// 处理与服务端交互
public class ServerHandler extends Thread {
    private Socket socket;
    private final ServerReadHandler readHandler;
    private final ServerWriteHandler writeHandler;
    ServerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.readHandler = new ServerReadHandler(socket.getInputStream());
        this.writeHandler = new
                ServerWriteHandler(socket.getOutputStream());
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
