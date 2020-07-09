import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
public class GrpcServer {
    private Server server;
    private void start() throws IOException {
        int port = 9091;
        server = ServerBuilder.forPort(port)
                .addService(new NetworkImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GrpcServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }
    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    public static void main(String[] args) throws IOException,
            InterruptedException {
        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
    static class NetworkImpl extends NetworkGrpc.NetworkImplBase {
        @Override
        public void callBack(Request req, StreamObserver<Reply>
                responseObserver) {
            Reply reply =
                    Reply.newBuilder().setMessage(req.getClientName()+" get server response").build();
                            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
