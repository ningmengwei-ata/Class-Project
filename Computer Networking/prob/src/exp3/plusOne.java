package exp3;



public class plusOne implements Runnable {
    private CountObject countobject;
    public int num;
    public plusOne(CountObject countobject){
        this.countobject=countobject;
        this.num=num+1;

    }
    public void run()
    {
       synchronized (countobject){countobject.sum(num);
        System.out.println(countobject.getBalance());
    }
    }
}

