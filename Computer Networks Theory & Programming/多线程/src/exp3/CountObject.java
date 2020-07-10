package exp3;
public class CountObject {
    volatile int num;
    //synchronized一个线程 调用这个方法 试图尝试占用对象锁
    //如果成功就执行方法 不然可能会阻塞
    synchronized void  plusOne()  {
        this.num=this.num+1;
        System.out.println("num的值被加1后为："+this.num);
        this.notify();//唤醒被等待的线程
       // this.notifyAll();//唤醒的线程都去抢占对象的锁
        //资源开销较大 所有被唤醒的线程都会去抢占锁 比单纯唤醒一个去抢占锁的开销大
    }//退出方法后锁被释放
    //线程抢占锁
    synchronized void minusOne()  {
       while(num==1)//如果这里是if在只有两个线程一个+1 一个-1 不会出错
        {//当是1时阻塞
            try {
                System.out.println("num的值被减1后为：" + this.num);
                this.wait();//多线程情况 如有两个-1线程 notify唤醒是唤醒在wait后唤醒 如果用while还会再判断 如果用if就不判断直接进行下面的-1操作 就会出错
                // System.out.println("------线程"+Thread.currentThread().getName()+"获得锁，wait()后的代码继续运行："+this.num);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
            this.num=this.num-1;
            System.out.println("num的值被减一后为:"+ this.num);
           this.notify();//如果是用while没有问题 如果是用if可能出现减的线程唤醒减的线程而且没判断值是否为1 出错
    }
    //用notify和wait因为线程的启动和结束开销都较大
    //不可能来一个任务启动一个线程，这里是先启动十几个线程 先都等待 来一个任务 唤醒一个去做
    void printNum(){
        System.out.println(this.num);
    }
}
