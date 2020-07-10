package exp5;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
class ServerReadHandler extends Thread {
    private final InputStream inputStream;
    ServerReadHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    @Override
    public void run() {
        super.run();
        try {
// 得到输出流用于接收数据
            // BufferedReader流的类 包装inputStream
            BufferedReader socketInput = new BufferedReader(new
                    InputStreamReader(inputStream));
            for (; ; ) {
// 客户端拿到一条数据
                String str = socketInput.readLine();
                if (str == null) {
                    System.out.println("客户端已无法读取数据！" );
                    break;
                }
                System.out.println("收到服务端消息:"+str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

