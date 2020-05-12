//
//  mytop.h
//  shell1
//
//

#ifndef mytop_h
#define mytop_h
#include <stdio.h>
#include <unistd.h>
#include <pwd.h>
#include <curses.h>
//#include <timers.h>
#include <stdlib.h>
#include <limits.h>
#include <termcap.h>
#include <termios.h>
#include <time.h>
#include <string.h>
#include <signal.h>
#include <fcntl.h>
#include <errno.h>
#include <dirent.h>
#include <assert.h>

typedef int endpoint_t;
typedef uint64_t u64_t;
typedef long unsigned int vir_bytes;

#define  USED        0x1
#define  IS_TASK    0x2
#define  IS_SYSTEM    0x4
#define  BLOCKED    0x8
#define TYPE_TASK    'T'
#define TYPE_SYSTEM    'S'
#define STATE_RUN    'R'

#define MAX_NR_TASKS 1023
#define SELF    ((endpoint_t) 0x8ace)
#define _MAX_MAGIC_PROC (SELF)
#define _ENDPOINT_GENERATION_SIZE (MAX_NR_TASKS+_MAX_MAGIC_PROC+1)
#define _ENDPOINT_P(e) \
((((e)+MAX_NR_TASKS) % _ENDPOINT_GENERATION_SIZE) - MAX_NR_TASKS)
#define  SLOT_NR(e) (_ENDPOINT_P(e) + 5)
#define _PATH_PROC "/proc"
#define CPUTIME(m, i) (m & (1L << (i)))
const char *cputimenames[] = { "user", "ipc", "kernelcall" };

#define CPUTIMENAMES (sizeof(cputimenames)/sizeof(cputimenames[0]))
unsigned int nr_procs, nr_tasks;

int nr_total=0;
//int slot_a=0;
//int pronum=0;
//int filenum=0;
//proc 结构体
struct proc {
    int p_flags;
    endpoint_t p_endpoint;
    pid_t p_pid;
    u64_t p_cpucycles[CPUTIMENAMES];
    int p_priority;
    endpoint_t p_blocked;
    time_t p_user_time;
    vir_bytes p_memory;
    uid_t p_effuid;
    int p_nice;
    char p_name[16+1];
};

struct proc *proc = NULL, *prev_proc = NULL;
//u64_t 64位 high和low32位 拼接成64位 high+low
static inline u64_t make64(unsigned long lo, unsigned long hi)
{
    return ((u64_t)hi << 32) | (u64_t)lo;
}
//把每个pid/psinfo的信息读出来
//判断读取信息是否可用
void parse_file(pid_t pid)
{
    char path[PATH_MAX], name[256], type, state;
    int version, endpt, effuid;
    unsigned long cycles_hi, cycles_lo;
    FILE *fp;
    struct proc *p;
    int slot;
    int i;

    sprintf(path, "/proc/%d/psinfo", pid);
    //按照/proc/%d/psinfo打开path中的文件
    if ((fp = fopen(path, "r")) == NULL)
        return;
    //version是否为1，如果不是该进程不需要记录
    if (fscanf(fp, "%d", &version) != 1) {
        fclose(fp);
        return;
    }
    //versions错误处理
    if (version != 0) {
        fputs("procfs version mismatch!\n", stderr);
        exit(1);
    }
    //读入类型和端点 判断是否读入的是两个
    if (fscanf(fp, " %c %d", &type, &endpt) != 2) {
        fclose(fp);
        return;
    }
    //统计总file数
    //filenum+=1;
    //原来的slot超出了nr_total
    slot = SLOT_NR(endpt);
    slot++;
    //slot=slot_a;
    //slot_a+=1;//赋值需保证在数组中不会重复
    
    //判断endpoint的值是否合理 在0到nr_total的范围内
    if(slot < 0 || slot >= nr_total) {
        fprintf(stderr, "top: unreasonable endpoint number %d\n", endpt);
        fclose(fp);
        return;
    }
     //slot为该进程结构体在数组中的位置
    p = &proc[slot];//把slot地址赋值给p
    
    if (type == TYPE_TASK)
        //标示task进程
        p->p_flags |= IS_TASK;
    else if (type == TYPE_SYSTEM)
        //标示system进程
        p->p_flags |= IS_SYSTEM;
    //将endpt和pid存入对应进程结构体
    p->p_endpoint = endpt;
    p->p_pid = pid;
   //读入名字 状态 阻塞状态 动态优先级 进程时间 高周期 低周期
    if (fscanf(fp, " %255s %c %d %d %lu %*u %lu %lu",
        name, &state, &p->p_blocked, &p->p_priority,
        &p->p_user_time, &cycles_hi, &cycles_lo) != 7) {

        fclose(fp);
        return;
    }
     //将指定长度的字符串复制到字符数组中
    strncpy(p->p_name, name, sizeof(p->p_name)-1);
    //数组置0
    p->p_name[sizeof(p->p_name)-1] = 0;

    if (state != STATE_RUN)//如果不是run的进程
        p->p_flags |= BLOCKED;//标志阻塞
    //拼接成64位，放在p_cpucycles[]数组中
    p->p_cpucycles[0] = make64(cycles_lo, cycles_hi);
    p->p_memory = 0L;
    //判断是否为有效用户ID
    if (!(p->p_flags & IS_TASK)) {
        int j;
        //读如内存 有效用户ID 和静态优先级
        if ((j=fscanf(fp, " %lu %*u %*u %*c %*d %*u %u %*u %d %*c %*d %*u",
            &p->p_memory, &effuid, &p->p_nice)) != 3) {

            fclose(fp);
            return;
        }

        p->p_effuid = effuid;
    } else p->p_effuid = 0;
//连续读CPUTIMENAMES次cycles_hi,cycle_lo
    for(i = 1; i < CPUTIMENAMES; i++) {
        if(fscanf(fp, " %lu %lu",
            &cycles_hi, &cycles_lo) == 2) {
            //拼接成64位，放在p_cpucycles[]数组中
            p->p_cpucycles[i] = make64(cycles_lo, cycles_hi);
        } else    {
            p->p_cpucycles[i] = 0;
        }
    }
    //读如内存 存入进程结构体
    if ((p->p_flags & IS_TASK)) {
        if(fscanf(fp, " %lu", &p->p_memory) != 1) {
            p->p_memory = 0;
        }
    }
    //按位或
    p->p_flags |= USED;
    

    fclose(fp);
}
void parse_dir(void)
{
    DIR *p_dir;
    struct dirent *p_ent;
    pid_t pid;
    char *end;
    //打开/proc
    if ((p_dir = opendir("/proc/")) == NULL) {
        perror("opendir on /proc");
        exit(1);
    }
    //readdir()返回参数p_dir 目录流的下个目录进入点。
    p_ent=readdir(p_dir);
   while(p_ent != NULL){
//        if(strncpy(p_ent->d_name,"/proc",1)==0)
//            continue;
        //分析出里面所有pid
       pid=strtol(p_ent->d_name,&end,10);
       if(pid!=0 && !end[0]){
            //printf("%l\n",pid);
           //一个pid调用一次parse_file
           parse_file(pid);
      }
       
       p_ent=readdir(p_dir);
   }

    closedir(p_dir);
}
int print_memory(void)
{
    FILE *fp;
    unsigned int pagesize;
    unsigned long total, free, largest, cached;
    //打开meminfo
    if ((fp = fopen("/proc/meminfo", "r")) == NULL)
        return 0;
    //读输入
    if (fscanf(fp, "%u %lu %lu %lu %lu", &pagesize, &total, &free,
            &largest, &cached) != 5) {
        fclose(fp);
        return 0;
    }

    fclose(fp);
    //打印memory信息
    printf("main memory: %ldK total, %ldK free, %ldK contig free, "
        "%ldK cached\n",
        (pagesize * total)/1024, (pagesize * free)/1024,
        (pagesize * largest)/1024, (pagesize * cached)/1024);

    return 1;
}
//tp 结构体
//包含了进程指针p和ticks，对应某个进程和滴答
struct tp {
    struct proc *p;
    u64_t ticks;
};
//计算cputicks 用到当前进程和其他进程的，还涉及CPUTIME

//滴答并不是简单的结构体中的滴答，因为在写文件的时候需要更新。需要通过当前进程来和该进程一起计算
u64_t cputicks(struct proc *p1, struct proc *p2, int timemode)
{
    int i;
    u64_t t = 0;
    //计算每个进程proc的滴答，通过proc和当前进程prev_proc做比较，如果endpoint相等，则在循环中分别计算
    for(i = 0; i < CPUTIMENAMES; i++) {
        if(!CPUTIME(timemode, i))
            continue;
        if(p1->p_endpoint == p2->p_endpoint) {
            t = t + p2->p_cpucycles[i] - p1->p_cpucycles[i];
        } else {
            t = t + p2->p_cpucycles[i];
        }
    }
//    for(i = 0; i < CPUTIMENAMES; i++) {
//           if(!CPUTIME(timemode, i))
//               continue;
//           if(proc->p_endpoint == prev_proc->p_endpoint) {
//               t = t + prev_proc->p_cpucycles[i] - proc->p_cpucycles[i];
//           } else {
//               t = t + prev_proc->p_cpucycles[i];
//           }
//       }
    return t;
}
void print_procs(
    struct proc *proc1, struct proc *proc2, int cputimemode)
{
    int p, nprocs;
    u64_t idleticks = 0;
    u64_t kernelticks = 0;
    u64_t systemticks = 0;
    u64_t userticks = 0;
    u64_t total_ticks = 0;
    int blockedseen = 0;
    static struct tp *tick_procs = NULL;
    if (tick_procs == NULL) {
        //给tick_procs分配内存
        //创建tp结构体tick_procs
        tick_procs = malloc(nr_total * sizeof(tick_procs[0]));
        //tick procs错误处理
        if (tick_procs == NULL) {
            fprintf(stderr, "Out of memory!\n");
            exit(1);
        }
    }

    for(p = nprocs = 0; p < nr_total; p++) {
        u64_t uticks;
        //如果当前进程标志不是used就continue 看下一个进程。
        if(!(proc2[p].p_flags & USED))
            continue;
        tick_procs[nprocs].p = proc2 + p;
        tick_procs[nprocs].ticks = cputicks(&proc1[p], &proc2[p], cputimemode);
        //更新实时uticks
        uticks = cputicks(&proc1[p], &proc2[p], 1);
        //算出总的ticks
        total_ticks = total_ticks + uticks;
        //判断是否为idletick
        //为0一直continue 不用计算
        if(p-5 == 317) {
            idleticks = uticks;
            continue;
        }
        //判断是否为kerneltick
        if(p-5 == ((endpoint_t) -1)) {
            kernelticks = uticks;
        }
//        if(!(proc2[p].p_flags & IS_TASK)) {
//                   if(proc2[p].p_flags & IS_SYSTEM)
//                       systemticks = systemticks + tick_procs[nprocs].ticks;
//                   else
//                       userticks = userticks + tick_procs[nprocs].ticks;
//               }
        //判断是否为systemtick和usertick
        if(!(proc2[p].p_flags & IS_TASK)) {
            if(proc2[p].p_flags & IS_SYSTEM)
                systemticks = systemticks + tick_procs[nprocs].ticks;
            else
                userticks = userticks + tick_procs[nprocs].ticks;
        }

        nprocs++;
    }

    if (total_ticks == 0)
        return;
    //打印user system kernel idle的情况
    printf("CPU states: %6.2f%% user, ", 100.0 * userticks / total_ticks);
    printf("%6.2f%% system, ", 100.0 * systemticks / total_ticks);
    printf("%6.2f%% kernel, ", 100.0 * kernelticks/ total_ticks);
    printf("%6.2f%% idle",100.00-(100.0 * (kernelticks+userticks+systemticks)/ total_ticks));
    printf("\n");
}
 //get_procs将所有需要的信息放在结构体数组proc[]中，每个元素都是一个进程结构体。
void get_procs(void)
{
   
    struct proc *p;
    int i;
    
    p = prev_proc;
    //记录当前进程，赋值给prev_proc
    prev_proc = proc;
    proc = p;
   
    if (proc == NULL) {
        //分配内存
        //每个进程分配一个结构体
        //分配nr_total个单位proc结构体内存空间,并把指针赋予proc
        proc = malloc(nr_total * sizeof(proc[0]));
        //错误处理
        if (proc == NULL) {
            fprintf(stderr, "Out of memory!\n");
            exit(1);
        }
    }
    //先将所有flag置0
    for (i = 0; i < nr_total; i++)
        proc[i].p_flags = 0;
    //调用parse_dir分析pid
    parse_dir();
    
}
void getkinfo(void)
{
    FILE *fp;

    if ((fp = fopen("/proc/kinfo", "r")) == NULL) {
        
        exit(1);
    }
    //读如nr_procs,nr_tasks
    if (fscanf(fp, "%u %u", &nr_procs, &nr_tasks) != 2) {
       
        exit(1);
    }

    fclose(fp);
    //算出总的nr_total
    nr_total = (int) (nr_procs + nr_tasks);
}
int mytop(){
    //跳转到/proc
    if (chdir("/proc") != 0) {
        perror("chdir to /proc" );
        return 1;
    }
    print_memory();
    getkinfo();
    get_procs();
    //当前进程为空的话 就要再调用get_procs
    if(prev_proc==NULL)
        get_procs();
    
    print_procs(prev_proc,proc,1);
    //fflush(NULL);
    
    return 0;
}


#endif /* mytop_h */
