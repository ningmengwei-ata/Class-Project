
#include <stdio.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <time.h>
#include <string.h>
#define Concurrency 20
#define writetime 500
#define readtime 400
#define Blocksize 65536
#define filesize (300 * 1024 * 1024)
#define maxline (2 * 1024 * 1024)
#define readbuff (10 * 1024 * 1024)
char * filepathDisk="/usr/read.txt";
char * filepathRam="/root/myram/read.txt";
char * filepathDN[17]={"/usr/read1.txt","/usr/read2.txt","/usr/read3.txt","/usr/read4.txt","/usr/read5.txt","/usr/read6.txt","/usr/read7.txt","/usr/read8.txt","/usr/read9.txt","/usr/read10.txt","/usr/read11.txt","/usr/read12.txt","/usr/read13.txt","/usr/read14.txt","/usr/read15.txt","/usr/read16.txt","/usr/read17.txt"};
char * filepathRN[17]={"/root/myram/read1.txt","/root/myram/read2.txt","/root/myram/read3.txt","/root/myram/read4.txt","/root/myram/read5.txt","/root/myram/read6.txt","/root/myram/read7.txt","/root/myram/read8.txt","/root/myram/read9.txt","/root/myram/read10.txt","/root/myram/read11.txt","/root/myram/read12.txt","/root/myram/read13.txt","/root/myram/read14.txt","/root/myram/read15.txt","/root/myram/read16.txt","/root/myram/read17.txt"};
char examtext[maxline] = "abcdefghijklmnopqrstuvwxyz";
struct timeval starttime, endtime;
char x[5];
char filepath[100];
int fp;
char string[40];
int length, res;
/*写文件:打开文件，判断返回值，如果正常打开文件就判断是否随机写，进行写操作*/
void write_file(int block, bool isrand, char *filepath)
{
    fp=open(filepath, O_CREAT | O_RDWR | O_SYNC, 0755);
 
      int i = 0;
      for (i=0; i < writetime; i++)
      {
          if((res=write(fp, examtext, block))!=block) {
              printf("Error writing to the file.\n");
              
          }
          // printf("Wrote %d bytes to the file.\n", res);
          if (isrand) //如果是随机写
              //块大小的整数倍的随机数
              lseek(fp, rand() % filesize, SEEK_SET);
      }
    //重设文件指针
    //默认文件指针自动移动，当读到文件末尾时，用lseek返回文件头
      lseek(fp, 0, SEEK_SET);
  

}

/*读文件:打开文件，判断返回值，如果正常打开就判断是否随机读，进行读操作*/
void read_file(int block, bool isrand, char *filepath)
{
 char examtext[maxline];
 int i = 0;
 for (; i < readtime; i++)
 {
     //read(fp, examtext, block);
    // printf("%s",examtext);
    
     if (isrand) //如果是随机读
         lseek(fp, rand() % filesize, SEEK_SET);
 }
 //lseek重新定位文件指针
    lseek(fp, 0, SEEK_SET);
}

//计算时间差，在读或写操作前后分别取系统时间，然后计算差值即为时间差。
//long get_time_left(long starttime, long endtime)
//{
// //to do....
//  return spendtime;
//}

/*主函数：首先创建和命名文件，通过循环执行read_file和write_file函数测试读写差异。
测试blocksize和concurrency对测试读写速度的影响，最后输出结果。*/
int main()
{
    //在默认情况下随机种子来自系统时钟。如果想在一个程序中生成随机数序列，需要至多在生成随机数之前设置一次随机种子。
    srand((unsigned)time(NULL));
    int ramfp, diskfp, block, i;
    int Fork;
    for (i = 0; i < maxline;)
    {
        strncat(examtext, "abcdefghij", 10);
        i += 10;
    }
    //O_RDWR 以可读写方式打开文件
    //O_SYNC 以同步的方式打开文件.
    //O_CREAT 若欲打开的文件不存在则自动建立该文件.
//    ramfp = open("/root/myram/read1.txt", O_CREAT | O_RDWR | O_SYNC, 0755);
//       diskfp = open("/usr/read1.txt", O_CREAT | O_RDWR | O_SYNC, 0755);
    
    
  
         
/*等待子进程完成后，获取计算时间，计算读写操作所花时间，延时，吞吐量等*/
     for (block = 64; block <= 4096;)
       {
           for (Fork = 6; Fork <= 16;){
           //clock_gettime(CLOCK_REALTIME, &starttime);
             gettimeofday(&starttime, NULL);
         //  printf("starttime.tv_sec:%d\n",starttime.tv_sec);
           for (i = 0; i <=Fork; i++){
                   if (fork() == 0)
                   {
                       //随机写
                       //write_file(block, true, filepathDN[i]);
                        // write_file(block, true, filepathRN[i]);
                     // write_file(block, true, ramfp);
                     // write_file(block, true, diskfp);
                         //顺序写
                      // write_file(block, false, filepathDN[i]);
                       write_file(block, false, filepathRN[i]);
                       //write_file(block, false, diskfp);
                     // write_file(block, false, ramfp);
                         
                         //随机读
                       
                       //  read_file(block, true, diskfp);
                      //read_file(block, true, ramfp);
                     //read_file(block,true,filepathDN[i]);
                       //read_file(block,true,filepathRN[i]);
                         //顺序读
                      // read_file(block,false,filepathDN[i]);
                      // read_file(block,false,filepathRN[i]);
                      // read_file(block, false, ramfp);
                      //read_file(block,false,diskfp);
                       
                       exit(1);
                   }
       }
         
          //等待所有子进程结束
               while (wait(NULL) != -1);
           gettimeofday(&endtime, NULL);
          // printf("endtime.tv_sec:%d\n",endtime.tv_sec);
              // clock_gettime(CLOCK_REALTIME, &endtime);
          //计算时间不要用微妙
          long alltime=(endtime.tv_sec-starttime.tv_sec)*1000+(endtime.tv_usec-starttime.tv_usec)/1000;
             double eachtime = (alltime ) / (double)writetime / Fork;
          double timeuse = eachtime / 1000;
             //转到KB级
                         double block_kB = (double)block / 1024.0;
           //MB/s
                         double readspeed = block_kB / timeuse/1024.0;
           double writespeed = block_kB / timeuse/1024.0;
                       //  printf("%d,%f,%ld,%f\n", block,block_kB,alltime,readspeed);
           printf("%d,%f,%ld,%f\n", block,block_kB,alltime,writespeed);
                Fork += 1;
           }
           block *= 2;
       }
  return 0;
}



