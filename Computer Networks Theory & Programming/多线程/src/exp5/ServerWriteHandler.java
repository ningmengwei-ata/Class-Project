package exp5;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ServerWriteHandler {
    private boolean done = false;
    private final PrintStream printStream;
    private final ExecutorService threadPool;

    ServerWriteHandler(OutputStream outputStream) {
        this.printStream = new PrintStream(outputStream);
        this.threadPool = Executors.newSingleThreadExecutor();
    }

    void send(String str) {
        threadPool.execute(new WriteRunnable(str));
    }

    class WriteRunnable implements Runnable {
        private final String msg;

        WriteRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            if (ServerWriteHandler.this.done) {
                return;
            }
            try {
                ServerWriteHandler.this.printStream.println(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


