package exp3;

public class TestInteract {
    public static void main(String[] args) {
        CountObject item1 = new CountObject();
        item1.num = 100;
        Thread t1 = new Thread() {
            public void run() {
                while(true) {
                    item1.minusOne();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            public void run() {
                while(true) {
                    item1.plusOne();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t2.start();
//       Thread t3 = new Thread() {
//            public void run() {
//                while (true) {
//                    item1.plusOne();
//
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            };
//        t3.start();
    }
}
/*public void run(){
		synchronized (res){
			while(count-- > 0){
				try {
					res.notify();//唤醒等待res资源的线程，把锁交给线程（该同步锁执行完毕自动释放锁）
					System.out.println(" "+number);
					res.wait();//释放CPU控制权，释放res的锁，本线程阻塞，等待被唤醒。
					System.out.println("------线程"+Thread.currentThread().getName()+"获得锁，wait()后的代码继续运行："+number);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//end of while
			return;
		}//synchronized

————————————————
版权声明：本文为CSDN博主「lingzhm」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/lingzhm/java/article/details/44940823*/