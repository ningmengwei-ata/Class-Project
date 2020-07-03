#include<stdio.h>
#include<unistd.h>
int inc=1;
int total=0;
int i;
char *sbrk(int incr);
char *result;
int main(int argc, int ** argv)
{
     while (((int)(result = sbrk(inc))) > 0)
        {
                total += inc;
                printf("incremented by %d, total %d\n", inc, total);
                inc += inc;
        }
        return 0;
}
