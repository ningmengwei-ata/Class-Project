import static java.lang.Math.pow;
public class prob {
   public static void main(String[] args)
   {
       int [] numbers = {35,50,100};
       for(int a:numbers)
       {System.out.println(solution(a)); }
   }
   public static double solution(int a){
       double prob=0;
       for(int i=0;i<=10;i++)
       {
           prob+=comb(a,i)*pow(0.1,i)*pow(0.9,a-i);
       }
       return 1-prob;
   }
   public static double comb(int a,int b)
   {
       if(b==0) {
           return 1;
       }
       else{
              double ans2=fact(a-b)*fact(b);
               double ans1=fact(a);
               return ans1/ans2;}
   }
    public static double fact(int n)
    {
        double sum=1;
        for(int i=2; i<=n; i++)
        {
            sum*=i;
        }
        return sum;
    }
}
