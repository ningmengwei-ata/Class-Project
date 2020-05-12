#include"pm.h"
#include<minix/syslib.h>
#include<minix/callnr.h>
#include<sys/wait.h>
#include<minix/com.h>
#include <minix/vm.h>
#include "mproc.h"
#include <sys/ptrace.h>
#include <sys/resource.h>
#include <signal.h>
#include <stdio.h>
#include<minix/sched.h>
#include <assert.h>

int do_chrt(){
    //deadline<0 直接返回
//if(m_in.m2_l1<0) return 1;
sys_chrt(who_p,m_in.m2_l1);
    return (OK);
}
