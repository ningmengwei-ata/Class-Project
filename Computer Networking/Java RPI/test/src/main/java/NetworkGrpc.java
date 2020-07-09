import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.29.0)",
    comments = "Source: network.proto")
public final class NetworkGrpc {

  private NetworkGrpc() {}

  public static final String SERVICE_NAME = "testgrpc.Network";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Request,
      Reply> getCallBackMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CallBack",
      requestType = Request.class,
      responseType = Reply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Request,
      Reply> getCallBackMethod() {
    io.grpc.MethodDescriptor<Request, Reply> getCallBackMethod;
    if ((getCallBackMethod = NetworkGrpc.getCallBackMethod) == null) {
      synchronized (NetworkGrpc.class) {
        if ((getCallBackMethod = NetworkGrpc.getCallBackMethod) == null) {
          NetworkGrpc.getCallBackMethod = getCallBackMethod =
              io.grpc.MethodDescriptor.<Request, Reply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CallBack"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Reply.getDefaultInstance()))
              .setSchemaDescriptor(new NetworkMethodDescriptorSupplier("CallBack"))
              .build();
        }
      }
    }
    return getCallBackMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NetworkStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NetworkStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NetworkStub>() {
        @java.lang.Override
        public NetworkStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NetworkStub(channel, callOptions);
        }
      };
    return NetworkStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NetworkBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NetworkBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NetworkBlockingStub>() {
        @java.lang.Override
        public NetworkBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NetworkBlockingStub(channel, callOptions);
        }
      };
    return NetworkBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NetworkFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NetworkFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NetworkFutureStub>() {
        @java.lang.Override
        public NetworkFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NetworkFutureStub(channel, callOptions);
        }
      };
    return NetworkFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class NetworkImplBase implements io.grpc.BindableService {

    /**
     */
    public void callBack(Request request,
        io.grpc.stub.StreamObserver<Reply> responseObserver) {
      asyncUnimplementedUnaryCall(getCallBackMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCallBackMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                Request,
                Reply>(
                  this, METHODID_CALL_BACK)))
          .build();
    }
  }

  /**
   */
  public static final class NetworkStub extends io.grpc.stub.AbstractAsyncStub<NetworkStub> {
    private NetworkStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NetworkStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NetworkStub(channel, callOptions);
    }

    /**
     */
    public void callBack(Request request,
        io.grpc.stub.StreamObserver<Reply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallBackMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NetworkBlockingStub extends io.grpc.stub.AbstractBlockingStub<NetworkBlockingStub> {
    private NetworkBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NetworkBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NetworkBlockingStub(channel, callOptions);
    }

    /**
     */
    public Reply callBack(Request request) {
      return blockingUnaryCall(
          getChannel(), getCallBackMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NetworkFutureStub extends io.grpc.stub.AbstractFutureStub<NetworkFutureStub> {
    private NetworkFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NetworkFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NetworkFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Reply> callBack(
        Request request) {
      return futureUnaryCall(
          getChannel().newCall(getCallBackMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALL_BACK = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NetworkImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NetworkImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL_BACK:
          serviceImpl.callBack((Request) request,
              (io.grpc.stub.StreamObserver<Reply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NetworkBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NetworkBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return MyProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Network");
    }
  }

  private static final class NetworkFileDescriptorSupplier
      extends NetworkBaseDescriptorSupplier {
    NetworkFileDescriptorSupplier() {}
  }

  private static final class NetworkMethodDescriptorSupplier
      extends NetworkBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NetworkMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NetworkGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NetworkFileDescriptorSupplier())
              .addMethod(getCallBackMethod())
              .build();
        }
      }
    }
    return result;
  }
}
