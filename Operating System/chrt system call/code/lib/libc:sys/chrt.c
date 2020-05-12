
#include <lib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include<sys/time.h>

int chrt(long deadline){
    //struct timespec time={0,0};
    struct  timeval    tv;
    struct  timezone   tz;
message m;
memset(&m,0,sizeof(m));
    //设置alarm
    alarm((unsigned int)deadline);
    //将当前时间记录下来 算deadline
    if(deadline>0){
        gettimeofday(&tv,&tz);
        //clock_gettime(CLOCK_REALTIME, &time);
        //deadline=nowtime+deadline
        deadline = tv.tv_sec + deadline;
    }
    //存deadline
m.m2_l1=deadline;
return(_syscall(PM_PROC_NR,PM_CHRT,&m));
    
}

