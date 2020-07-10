package exp6;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
public class RPCClient {
    public static void main(String[] temp) {
        IProxy2 service = (IProxy2)
                Proxy.newProxyInstance(IProxy2.class.getClassLoader(), new Class<?>[]
                        {IProxy2.class}, (Object proxy, Method method, Object[] args) -> {
                    try(Socket socket = new Socket()){
                        socket.connect(new InetSocketAddress(9091));
                        ObjectOutputStream os = new
                                ObjectOutputStream(socket.getOutputStream());
                        os.writeUTF(method.getName());
                        os.writeObject(method.getParameterTypes());
                        os.writeObject(args);
                        return new
                                ObjectInputStream(socket.getInputStream()).readObject();
                    }catch (Exception e){
                        return null;
                    }
                });
System.out.println(service.sayHello("1"));
        System.out.println(service.upperString("a"));
    }
}