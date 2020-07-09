package exp4;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
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
        String[] strArray=new String[5];
        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        int i=0;
        while (nextLine != null && !nextLine.equals("")) {
            strArray[i]=nextLine;
            //System.out.println(strArray[i]);
            i++;
            nextLine = scanner.nextLine();
        }

        System.out.println("end of input string");
        for(int j=0;j<4;j++)
        {
            outputStream.write(strArray[j].getBytes());
            byte[] bytes = new byte[1024];
// 接收服务器返回的消息
            int len = inputStream.read(bytes);
            StringBuilder sb = new StringBuilder();
            sb.append(new String(bytes, 0, len));
            System.out.println("收到服务器的消息：" + sb.toString());
        }


// 资源释放
        outputStream.close();
        inputStream.close();
// 释放资源
        socket.close();
        System.out.println("客户端已退出～");
    }
}
