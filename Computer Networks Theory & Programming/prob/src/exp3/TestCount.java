
package exp3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TestCount {
   public static void main(String[] args) {
      CountObject countObject=new CountObject();
        ExecutorService executorService1=Executors.newFixedThreadPool(1000);//创建线程池
        for (int i = 0; i <1000 ; i++) {
            executorService1.execute(new plusOne(countObject));
        }
       executorService1.shutdown();
        ExecutorService executorService2=Executors.newFixedThreadPool(1000);//创建线程池
        for (int i = 0; i <1000 ; i++) {
            executorService2.execute(new minusOne(countObject));
        }

        executorService2.shutdown();
        while(!executorService1.isTerminated()) {}
        System.out.println("所有进程结束后的num");
        System.out.println(countObject.getBalance());
   }
}
       /* Thread[] addThreads = new Thread[n];
        Thread[] reduceThreads = new Thread[n];
        for (int i = 0; i < n; i++) {
            Thread t1 = new Thread(){
                public void run(){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    item.plusOne();
                    item.printNum();
                }
            };
            Thread t2 = new Thread(){
                public void run(){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    item.minusOne();
                    item.printNum();
                }
            };
            t1.start();
            t2.start();
            addThreads[i] = t1;
            reduceThreads[i] = t2;
        }
        for (Thread t : addThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread t : reduceThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print("所有进程结束后的num");
        item.printNum();*/

