package exp6;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class RPCServer {
    public static void main(String[] args) {
        Proxy2Impl service = new Proxy2Impl();
        try (ServerSocket serverSocket = new ServerSocket(9091)){
           // serverSocket.bind(new InetSocketAddress(9091));
            for(int i=0;i<4;i++) {
                try (Socket accept = serverSocket.accept()) {
                    Thread thread = new Thread(new SSocket(accept));
                    thread.start();
                    ObjectInputStream is = new
                            ObjectInputStream(accept.getInputStream());
                    String methodName = is.readUTF();
                    Class<?>[] parameterTypes = (Class<?>[]) is.readObject();
                    Object[] arguments = (Object[]) is.readObject();
                    Object result =
                            Proxy2Impl.class.getMethod(methodName, parameterTypes).invoke(service, arguments);
                    new
                            ObjectOutputStream(accept.getOutputStream()).writeObject(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}