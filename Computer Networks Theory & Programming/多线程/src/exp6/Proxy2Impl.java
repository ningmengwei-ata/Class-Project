package exp6;
public class Proxy2Impl implements IProxy2 {
    @Override
    public String sayHello(String s) {
        return "Hello" + s;
    }
    @Override
    public String upperString(String s) {
        return s.toUpperCase();
    }
}