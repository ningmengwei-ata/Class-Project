import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
public class GrpcClient {

    private final ManagedChannel channel;
  private final NetworkGrpc.NetworkBlockingStub blockingStub;


    public GrpcClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

       blockingStub = NetworkGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public  void greet(String name){
       Request request= Request.newBuilder().setClientName(name).build();
       Reply response = blockingStub.callBack(request);
        System.out.println(response.getMessage());

    }
    public static void main(String[] args) throws InterruptedException {
        GrpcClient client = new GrpcClient("127.0.0.1",9091);
        client.greet("java client");




    }
}

