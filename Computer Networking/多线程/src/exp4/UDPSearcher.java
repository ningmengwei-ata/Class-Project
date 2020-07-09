package exp4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("Searcher Started");
//搜索方不需要指定监听端口
        DatagramSocket ds = new DatagramSocket();
        //发送数据
        String sendData = MessageUtil.buildWithPort(30000);
        byte[] sendBytes = sendData.getBytes();
// 发送至localhost:909111
        DatagramPacket sendPack =new DatagramPacket(
          sendBytes,sendBytes.length,InetAddress.getByName("255.255.255.255"), 9091
        );
         ds.send(sendPack);

        ds = new DatagramSocket(30000);
        byte[] buf =new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(buf,buf.length);
        ds.receive(receivePack);
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int len = receivePack.getLength();
        String data = new String(receivePack.getData(),0,len);
        System.out.println("receive from ip:"+ip+"\tport:"+port+"\tdata:"+data);

// 接收回送数据
       // byte[] buf =new byte[1024];

        //DatagramPacket receivePack = new DatagramPacket(buf,buf.length);
       // ds.receive(receivePack);
        //String ip = receivePack.getAddress().getHostAddress();
        //int port = receivePack.getPort();
        //int len = receivePack.getLength();
        //String data = new String(receivePack.getData(),0,len);
        //System.out.println("receive from ip:"+ip+"\tport:"+port+"\tdata:"+data);
        System.out.println("Searcher finished");
        ds.close();
    }
}