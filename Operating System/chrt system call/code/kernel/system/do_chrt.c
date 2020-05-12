
#include "kernel/system.h"
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <lib.h>
#include <minix/endpoint.h>
#include <string.h>



int do_chrt(struct proc *caller, message *m_ptr)
{
 
     

  struct proc *rp;
  long exp_time;

  exp_time = m_ptr->m2_l1;

//通过 proc_addr 定位内核中进程地址
  rp = proc_addr(m_ptr->m2_i1);
//将exp_time 赋值给该进程的 p_deadline
  rp->p_deadline = exp_time;

  return (OK);
}


