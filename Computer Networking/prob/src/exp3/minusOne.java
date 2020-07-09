package exp3;

public class minusOne implements Runnable {
    private CountObject countobject;
    public int num;
    public minusOne(CountObject countobject){
        this.countobject=countobject;
        this.num=num-1;

    }
    public void run() {
        synchronized (countobject) {
            countobject.sum(num);
            System.out.println(countobject.getBalance());
        }
    }
}

