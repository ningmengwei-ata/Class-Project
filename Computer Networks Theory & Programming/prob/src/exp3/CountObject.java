package exp3;

public class CountObject {
    public int balance=1000;
   public synchronized int sum(int num){
       int newNum=balance+num;
       try {
           Thread.sleep(10);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       balance=newNum;
       return num;
   }
   public double getBalance(){
       return balance;
   }

}
