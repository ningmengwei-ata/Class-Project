//
//  main.c
//  shell1
//

//

#include "myshell.h"
#include "mytop.h"
#define HISTORY_NUM 20
#define MAX_LINE 100
#define STD_INPUT 0
#define STD_OUTPUT 1
cmd_all * cmd_var ;
int my_init(void);
char history[HISTORY_NUM][MAX_LINE];
int shm_id;
char *shm_buff;
int history_num=0;
int k=0;
int mark=0;
int background=0;
char currentdir[20];
//typedef unsigned long u64_t;

//list of builtin commands
char *builtinStr[]={"cd","exit","history","mytop"};


int my_cd(void)
{
    char dir[300];
    char *dirArray[10];
    int i;
    //cd 到输入的path
    if( 0 != chdir( cmd_var->cmd_all[0].cmd[1]) )
    {//错误处理
        perror("chdir");
        return -1;
    }
    else
    {

        getcwd(dir,300);//将当前工作目录的绝对路径复制到参数dir所指的内存空间中
        my_splitStr(dirArray,dir,"/");

      //找到当前目录
       for(i=0; ;i++)
       {
        if(dirArray[i]==NULL)
            break;
        }
        //保存当前目录到currentdir
       strcpy( currentdir , dirArray[i-1] );
       printf("cd succeeded\n");

       return 0;
    }
}

int my_init(){
       char dir[300];
       char *dirArray[10];
       int i;
       cmd_var->cmdPtr=0;//指针初始化
        getcwd(dir,300);//将当前工作目录的绝对路径复制到参数dir所指的内存空间中
        my_splitStr(dirArray,dir,"/");

         //找到当前目录
          for(i=0; ;i++)
          {
           if(NULL == dirArray[i])
               break;
          }
         strcpy( currentdir , dirArray[i-1] );
    return 0;
}

int my_exit(void)
{
    printf("exit succeeded\n");

    shmdt(shm_buff);//断开映射关系
    shmctl(shm_id,IPC_RMID,0);//删除内存片段
    exit(EXIT_SUCCESS);
    return 0;
}



int my_readLine(char * line)
{
    int index = 0;
    char c;
    int j=0;
    while (1)
    {
        c = getchar();
        
        if (c == '\n')
        {
            //保存输入的命令到history数组
            history[k][j]=c;
            k++;
            history_num++;
           //为了实现history n 的l命令
            if((line[0]=='h')&&(line[1]=='i')&&(line[2]=='s')&&(line[3]=='t')&&(line[4]=='o')&&(line[5]=='r')&&(line[6]=='y'))
               mark=line[8]-'0';
            line[index] = '\n';
            break;
        }
        else
        {
             //保存输入的命令到history数组
            line[index] = c;
            history[k][j]=c;
            j++;
        }
        index++;
    }

    return 0;
}

//将子字符串复制到一个char数组中
//ResultString是目标数组的指针
int my_subString(char *ResultString, char *str , int start , int end)
{
    int i,j=0;
    for(i=start;i<=end;i++)
    {
        ResultString[j]=str[i];
        j++;
    }
    //string结束最后一位置\0
    ResultString[j]='\0';
    return 0;
}

//strtok首次调用时，str指向要分解的字符串，之后再次调用要把str设成NULL即可。
int my_splitStr(char *resultArr[], char *str , char *split)
{
    char * token;
    //strtok首次调用时，str指向要分解的字符串
    //char *strtok(char *str, const char *delim) 分解字符串 str 为一组字符串，delim 为分隔符。
    token=strtok(str,split);
    int position=0;
    while(token!=NULL)
    {//分割后字符存入数组
        resultArr[position]=token;
        //str设成NULL
        token=strtok(NULL,split);
        position++;
    }
    //最后一位设为Null因为execvp执行要以null结尾
    resultArr[position]=NULL;
    return 0;
}


int my_analyCmd(char * line )
{
    int previousEnd=0;
    int i,j=0;
    //读到换行符为止
    for(i=0;line[i]!='\n';i++)
    { if(line[i]=='&')
    {
    //如果是后台程序 background置1
        background=1;
    }
        //检测到|或<或> 就将这个之前的字符串存入cmdStr中
        //再通过空格分割cmd_str存入的指令存入cmd中
        //再将后一个子字符串分割后存入cmd
        if(line[i]=='|'||line[i]=='>'||line[i]=='<')
        {
        my_subString(cmd_var->cmd_all[j].cmdStr,line, previousEnd, i-1);
my_splitStr(cmd_var->cmd_all[j].cmd,cmd_var->cmd_all[j].cmdStr," ");
            cmd_var->cmd_all[j].nextSign=line[i];
            j++;
            previousEnd=i+1;
        }
    }
       
    //检测到一行结束 把这行命令存入subString(不包括最后的\n)
    my_subString(cmd_var->cmd_all[j].cmdStr,line, previousEnd, i-1);
    my_splitStr(cmd_var->cmd_all[j].cmd,cmd_var->cmd_all[j].cmdStr," ");
    return 0;
}

int my_builtinCmd(void)
{
    int i;
    
    for(i=0;i<4;i++)
    {
        //找到对应的builtinCmd
        if(0==strcmp(builtinStr[i], cmd_var->cmd_all[0].cmd[0]) )
            break;
    }
    //判断是四个内置命令的哪一个
    switch(i)
    {
    case 0:
        my_cd();
        return 0;
        break;
    case 1:
        my_exit();
        return 0;
        break;
   case 2:
        my_history();
        return 0;
        break;
   case 3:
        mytop();
        return 0;
        break;
    default:
        return -1;
        break;
    }
    
}

int my_history(){
    int i;
    int j;
    j=0;
    //按照history 的n打印最近的n条历史命令
     for(i=history_num-mark;i<history_num;)
     {
             if(history[i][j]=='\n'){
                 printf("\n");
                 i++;
                 j=0;
             }
             else{
                 //printf("%d\n",history_count[i][j]);
                 printf("%c",history[i][j]);
                 j++;
             }
         }
    return 0;
}



int fd[2];
int my_execute()
{
    int pid,localPtr;

    if('|'==cmd_var->cmd_all[cmd_var->cmdPtr].nextSign)
    {
        localPtr=cmd_var->cmdPtr;
        pipe(&fd[0]);//新建一个管道

        pid=fork();

        if(0==pid)//在子进程中执行下一个命令（紧随“|”的那个命令
            //将标准输入重定向到管道的读端
        {
            cmd_var->cmdPtr++;
             close(fd[1]);//子进程关闭写端
            close(STD_INPUT);
            dup(fd[0]);//将标准输入指向fd[0]
            close(fd[0]);//不再需要该文件描述符
           //递归调用my_execute 可以多管道
            my_execute();
            exit(EXIT_SUCCESS);
        }
        else//在父进程中执行当前命令，把标准输出重定向到管道的写端
        {
            //signal(SIGCHLD, SIG_IGN);
            close(fd[0]);//关闭管道读端
            close(STD_OUTPUT);//新的标准输出
            dup(fd[1]);//将标准输出指向fd[1]
            close(fd[1]);//不再需要该文件描述符
       //运行cmd[0],失败会返回-1
            if(0!=execvp(cmd_var->cmd_all[localPtr].cmd[0],cmd_var->cmd_all[localPtr].cmd))
                printf("No such command!\n");

            exit(EXIT_SUCCESS);
        }
    }
    else
    if('<'==cmd_var->cmd_all[cmd_var->cmdPtr].nextSign)
    { //将标准输入重定向到 file
        localPtr=cmd_var->cmdPtr;
        char fileName[20];
        //把文件名复制到fileName
     strcpy(fileName,cmd_var->cmd_all[localPtr+1].cmd[0]);
         //从标准输入重定位到fileName输入
        freopen(fileName,"r",stdin);
  //执行命令 execvp第一个是命令第二个是参数表，如果返回不是0就没有这个命令
        if(0!=execvp(cmd_var->cmd_all[localPtr].cmd[0],cmd_var->cmd_all[localPtr].cmd))
            printf("No such command!\n");

        exit(EXIT_SUCCESS);
        
    }
    else if('>'==cmd_var->cmd_all[cmd_var->cmdPtr].nextSign)
    {//从标准输出重定向到文件
        localPtr=cmd_var->cmdPtr;
        char fileName[20];
        //把文件名复制到fileName
        strcpy(fileName,cmd_var->cmd_all[localPtr+1].cmd[0]);
        //从标准输出重定位到fileName输出
        freopen(fileName,"w",stdout);
       //执行命令 execvp第一个是命令第二个是参数表，如果返回不是0就没有这个命令
        if(0!=execvp(cmd_var->cmd_all[localPtr].cmd[0],cmd_var->cmd_all[localPtr].cmd))
            printf("No such command!\n");
        
        exit(EXIT_SUCCESS);
    }
    else
    {//如果没有重定向和管道 先保存当前localPtr
        localPtr=cmd_var->cmdPtr;
       //执行命令 execvp第一个是命令第二个是参数表，如果返回0就没有这个命令
        if(0!=execvp(cmd_var->cmd_all[localPtr].cmd[0],cmd_var->cmd_all[localPtr].cmd))
            printf("No such command!\n");

        exit(EXIT_SUCCESS);
    }

    return 0;

}


int my_clearCmd(cmd_all * cmd_var)
{//清空所有保存命令的数组
    int i,j;
    background=0;
    cmd_var->cmdPtr=0;
    for(i=0;i<ALL_SIZE;i++)
    {
        for(j=0;j<CMD_LENG;j++)
        {
            //保存命令数组置NULL
            cmd_var->cmd_all[i].cmd[j]=NULL;
            //保存命令子数组置\0
            cmd_var->cmd_all[i].cmdStr[0]='\0';
        }
        cmd_var->cmd_all[i].nextSign=0;
    }
    
    return 0;
}
int main()
{
    char line[MAX_LINE];
    int pid;
    int key;
    //这里也可以直接用数组做 不用分配共享内存 在fork的时候内存会自动分配
    //共享内存
       key = ftok("shell1",0);//key_t ftok( const char \* fname, int id )，"."本处设置为当前目录。id为子序列号.
    shm_id = shmget(key,SHM_MEM_SIZE,IPC_CREAT|0666);
     //启动对该共享内存的访问，并把共享内存连接到当前进程的地址空间。
    shm_buff = (char *)shmat(shm_id,NULL,0);
   //给指令结构体分配内存
       cmd_var = (cmd_all *)shm_buff;
    my_init();
    while(1)
    {
        //打印当前命令
        printf("%s",currentdir);
        printf("$");
        my_readLine(line);
        my_analyCmd(line);
        //如果是内置命令，成功执行后清理进程
        if(0==my_builtinCmd())
        {
            my_clearCmd(cmd_var);
            continue;
        }
        //fork一个新进程执行program命令
        else
        {
            pid = fork();
            if(background==1)
            {
                if (pid==0)
                {//标准输出重定向到/dev/null
                    freopen("/dev/null","w",stdout);
                    my_execute();
                    
                }//父进程ignoreSIGCHLD
                signal(SIGCHLD,SIG_IGN);
                 
            }
            else{
             if(pid==0)
            {
                my_execute();
            }//父进程等待子进程执行完
                waitpid(pid,NULL,0);
            
           }
        }
        //保证所有命令到执行完再进行clear
       sleep(1);
        my_clearCmd(cmd_var);
    }
    return 0;

}
