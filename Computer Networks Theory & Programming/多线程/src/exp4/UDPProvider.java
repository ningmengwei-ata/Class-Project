package exp4;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("Provider Started");
// 选择一个端口用于数据接收
        DatagramSocket ds = new DatagramSocket(9091);
        byte[] buf =new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(buf,buf.length);
        ds.receive(receivePack);
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int len = receivePack.getLength();
        String data = new String(receivePack.getData(),0,len);
        System.out.println("receive from ip:"+ip+"\tport:"+port+"\tdata:"+data);
       // System.out.println( MessageUtil.parsePort(data));
        //回送数据
        String tag = UUID.randomUUID().toString();
        String responseData = MessageUtil.buildWithTag(tag);
        //System.out.println(MessageUtil.parseTag(responseData));
        DatagramPacket responsePacket = new DatagramPacket(
                responseData.getBytes(),
                responseData.length(),
                receivePack.getAddress(),
                MessageUtil.parsePort(data)
        );
        ds.send(responsePacket);
        System.out.println("Provider finished");
        ds.close();
    }
}