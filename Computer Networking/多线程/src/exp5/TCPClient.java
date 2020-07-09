package exp5;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
public class TCPClient {
    private static final int PORT = 9090;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
// 连接本地9090端口 timeout为3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),
                PORT), 3000);
        System.out.println("客户端为：" + socket.getLocalAddress() + ":" +
                socket.getLocalPort());
        System.out.println("服务端为：" + socket.getInetAddress() + ":" +
                socket.getPort());
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        BufferedReader socketInput = new BufferedReader(new
                InputStreamReader(inputStream));

        ServerHandler serverHandler=new ServerHandler(socket);
        serverHandler.start();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.next();//这里nextLine阻塞
           //socketPrintStream.println(input);
            serverHandler.send(s);
// 接收服务器返回的消息
            //String s = socketInput.readLine();
            //System.out.println("收到服务器消息:" + s);

        }
// 资源释放
        outputStream.close();
        inputStream.close();
// 释放资源
        socket.close();
        System.out.println("客户端已退出～");
    }
    }

