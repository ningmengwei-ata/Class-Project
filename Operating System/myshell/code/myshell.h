//
//  myshell.h
//  shell1
//
//

#ifndef myshell_h
#define myshell_h
#include <stdio.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/shm.h>
#include <sys/ipc.h>
#include <signal.h>

#define ALL_SIZE 10
#define CMD_LENG 8          //指令最大长度
#define PARA_MAX 64         //参数最大长度
#define SHM_MEM_SIZE 10240  //共享内存的空间大小


// 指令结构体
typedef struct CMD_STRUCT
{
    char *cmd[CMD_LENG]; //数组元素为字符指针每个指针指向命令的首地址。
    char cmdStr[CMD_LENG*PARA_MAX];//cmdStr存my_substring得到的子字符串
    char nextSign; // '|' or'>' or '<'
}cmdStruct;
typedef struct CMD_ALL
{
    cmdStruct cmd_all[ALL_SIZE];//定义数组包含ALL_SIZE个cmdstruct结构体
    int cmdPtr;//用以指示对应的是cmd_all的第几个命令
}cmd_all;

extern cmd_all *cmd_var;
int my_cd(void);
int my_exit(void);
int my_readLine(char *line);
int my_subString(char *ResultString , char *str , int start , int end);
int my_splitStr(char *resultArr[] , char *str , char *split);
int my_analyCmd(char *line);
int my_builtinCmd(void);
int my_execute(void);
int my_clearCmd(cmd_all * cmd_var );
int my_history(void);
#endif /* myshell_h */
