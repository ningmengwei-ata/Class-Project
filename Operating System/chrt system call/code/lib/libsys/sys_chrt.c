#include"syslib.h"
int sys_chrt(proc_ep,deadline)
endpoint_t proc_ep;
long deadline;
{
int r;
message m;
//将进程号和 deadline 放入消息结构体
m.m2_i1=proc_ep;
m.m2_l1=deadline;
//通过_kernel_call传递到内核层
r=_kernel_call(SYS_CHRT,&m);
return r;
}
