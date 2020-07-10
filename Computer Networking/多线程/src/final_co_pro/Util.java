package finals;
import java.io.* ;
import java.net.* ;
import java.util.* ;


import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;


import java.io.IOException;
import java.net.ServerSocket;

public class Util {
    public static byte[] byteSum (byte[] a,byte[] b){
        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length,b.length);
        return c;
    }

    public static byte[] byteSum (String a_string,byte[] b){
        byte[] a = a_string.getBytes();
        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length,b.length);
        return c;
    }

    public static void scanPort (){
        for(int port=1;port<=65535;port++){
            try{
                ServerSocket serverSocket=new ServerSocket(port);
                serverSocket.close();   //及时关闭ServerSocket
            }catch(IOException e){
                System.out.println("端口"+port+" 已经被其他服务器进程占用");
            }
        }
    }
}