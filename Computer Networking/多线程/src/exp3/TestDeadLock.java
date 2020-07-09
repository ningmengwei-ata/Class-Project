package exp3;

public class TestDeadLock {
    public static void main(String[] args) {
        CountObject item1 = new CountObject();
        item1.num = 1000;
        CountObject item2 = new CountObject();
        item2.num = 2000;
        CountObject item3=new CountObject();
        item3.num =3000;
        Thread t1 = new Thread() {
            public void run() {
                synchronized (item1) {
                    System.out.println("t1 占用item1");
                    try {
                   //停顿1000ms另一个ํ线程有足够的时间占用item1
                        //等待其他线程拿到锁
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("t1 试图占用item2");
                    System.out.println("t1等待中...");
                    synchronized (item2) {
                        System.out.println("t1获取item2");
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            public void run() {
                synchronized (item2) {
                    System.out.println("t2 占用item2");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("t2 试图占用item1");
                    System.out.println("t2 等待中...");
                    synchronized (item1) {
                        System.out.println("t2 获取item1");
                    }
                }
            }
        };
        t2.start();
        Thread t3=new Thread(){
            public void run(){
                synchronized (item3) {
                    System.out.println("t3 占用item3");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("t3 试图占用item1");
                    System.out.println("t3 等待中...");
                    synchronized (item1) {
                        System.out.println("t3 获取item1");
                    }
                }
            }
        };
            t3.start();
    }
}
