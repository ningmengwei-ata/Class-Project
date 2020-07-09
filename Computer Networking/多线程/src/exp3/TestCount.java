package exp3;

public class TestCount {
    public static void main(String[] args) {
        CountObject item = new CountObject();
        item.num = 1000;
        int n= 1000;
        Thread[] addThreads = new Thread[n];
        Thread[] reduceThreads = new Thread[n];

            for (int i = 0; i < n; i++) {

                        Thread t1 = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                synchronized (item) { item.plusOne();}
                                item.printNum();
                            }
                        };
                        t1.start();
                        addThreads[i] = t1;


                        Thread t2 = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                synchronized (item) {item.minusOne();
                                item.printNum();}
                            }
                        };
                        t2.start();
                        reduceThreads[i] = t2;
                    }

              for (Thread t : addThreads) {
                  try {//等待线程做完
                      t.join();//当try语句中出现异常是时，会执行catch中的语句，java运行时系统会自动将catch括号中的Exception e 初始化，也就是实例化Exception类型的对象。
                  } catch (InterruptedException e) {
                      e.printStackTrace();//在命令行打印异常信息在程序中出错的位置及原因。
                  }
              }


            for (Thread t : reduceThreads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        System.out.print("所有线程结束后num:");
        item.printNum();
    }
}

