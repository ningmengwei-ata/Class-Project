##  Project 3

### Checkoff Item 1：

> Make note of one bottleneck. 

#### 在虚拟机上安装perf

安装好开始运行时出现如下报错

![image-20211213105640209](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213105640209.png)

这是因为出于安全考虑，内核在默认情况下禁止非特权用户监测系统性能。解决的办法有以下几种：

- 修改 kernel.perf_event_paranoid 内核参数
- 给 perf 加入 CAP_PERFMON 权限位
- 使用 root 用户进行性能监测

这里修改内核参数

修改后运行perf record出现如下warning

![image-20211213131357834](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213131357834.png)

运行如下命令

```
sudo sh -c " echo 0 > /proc/sys/kernel/kptr_restrict"
```

运行成功，结果如下

![image-20211213132441886](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213132441886.png)

#### 运行perf report

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213132940127.png" alt="image-20211213132940127" style="zoom:50%;" />

会有Cannot load tips.txt file, please install perf!的报错

解决方法在该路径下保存正确的tips.txt即可

![image-20211213151228102](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213151228102.png)

可以看到瓶颈在isort这里

更细化的看具体函数，命令如下

![image-20211213162745858](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213162745858.png)

![image-20211213162722581](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213162722581.png)

可以看到在整个isort运行中isort函数占了大头，为主要优化对象

#### 运行lscpu

![image-20211213133429846](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213133429846.png)

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213133458074.png" alt="image-20211213133458074" style="zoom:45%;" />

另外我还运行了perf top以及perf stat来查看相关结果

#### 运行perf top

![image-20211213134001997](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213134001997.png)

可以看到clear_page_orig以及finish_task_switch占用cpu资源的比例较大

#### 运行perf stat

![image-20211213140909712](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211213140909712.png)



### Checkoff Item 2：

> Run sum under cachegrind to identify cache performance. It may take a little while. In the output, look at the D1 and LLd misses. D1 represents the lowest-level cache (L1), and LL represents the last (highest) level data cache (on most machines, L3). Do these numbers correspond with what you would expect? Try playing around with the values N and U in sum.c. How can you bring down the number of cache misses?  

#### 在cache grind 下运行sum

![image-20211219135127228](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211219135127228.png)

D1misses(代表了L1 cache的miss) 100548142，比例为16.5%

LLD misses(大多数情况代表L3 cache的miss) 84954029，比例为13.9%

#### **运行lscpu**

可以看到l1d cache 32KiB L2cache 256Kib L3 cache 6MiB

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211219135154361.png" alt="image-20211219135154361" style="zoom: 43%;" />

![image-20211219135209527](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211219135209527.png)

要降低miss rate，需要减小随机数范围U的数值

由于L3 Cache大小是6M，令U=1,000,000时数组大小约为4M

运行结果如下

![image-20220102200455335](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102200455335.png)

可以看到三级缓存的miss rate变为0

反之比如尝试将U的值扩大20倍,看到miss rate没有很大的变化

![image-20211219141211191](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211219141211191.png)



### write up 1

> Compare the Cachegrind output on the DEBUG=1 code versus DEBUG=0 compiler optimized code. Explain the advantages and disadvantages of using instruction count as a substitute for time when you compare the performance of different versions of this program. 

#### 比较 Cachegrind 结果的不同on the DEBUG=1 code versus DEBUG=0 compiler optimized code

DEBUG=0

![image-20220102204109092](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102204109092.png)

![image-20220102204158870](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102204158870.png)

DEBUG=1

![image-20220102204647581](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102204647581.png)

![image-20220102204702502](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102204702502.png)

通过比较可以看到：

首先指令获取的缓存访问，可以看到DEBUG=1时，执行的指令数减少给出了获取的次数、I1 未命中的比例不变和 LL 指令数（LLi ) 未命中的比例降低。

对于数据的缓存，DEBUG=1时总数减少，读取和写入都有减少，而D1未命中比例和LLD未命中比例不变。

DEBUG=1时，分支数减少，而错误预测的比例不变。

#### 衡量性能时用指令数代替时间的利与弊：

利：得到指令数很简单

由于指令数取决于体系结构，而不是确切的实现，因此我们可以在不知道实现的所有细节的情况下测量指令数。

弊端：指令数在一些情况无法准确衡量计算性能。更改指令集以降低指令计数可能会导致一个时钟周期时间变慢或 CPI 更高的系统，从而抵消了指令计数的改进。

### write up 2

> Explain which functions you chose to inline and report the performance differences you observed between the inlined and uninlined sorting routines. 

内联函数选择，内联函数语句应不超过10行，只适合函数体内代码简单的函数数使用，不能包含复杂的结构控制语句例如while、switch。内联函数本身不能是直接递归函数。

我选择内联copy_i 函数。*mem_alloc*和*mem_free*

**内联是以代码膨胀（复制）为代价**，仅仅省去了函数调用的开销，从而提高函数的执行效率。如果执行函数体内代码的时间，相比于函数调用的开销较大，那么效率的收获会很少。另一方面，**每一处内联函数的调用都要复制代码，将使程序的总代码量增大，消耗更多的内存空间**。

在main.c将sort_i的注释符删除

![image-20211219194430367](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211219194430367.png)

性能测试如下

内联前

![image-20220102212310295](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102212310295.png)

![image-20220102204158870](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102204158870.png)

内联后

![image-20220102213602544](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102213602544.png)

可以看到内联后对性能影响不明显。分析原因可能是优化等级已经设为O3导致inline的优化效果不显著。

### write up 3

> Explain the possible performance downsides of inlining recursive functions. How could profiling data gathered using cachegrind help you measure these negative performance effects? 

#### 内联递归函数可能出现的性能缺点

因为函数调用是需要开销的（函数调用时的参数压栈、栈帧开辟与销毁、以及寄存器保存与恢复等等操作），因为内联函数是在编译阶段被编译器展开在调用处的，多次调用就需要多段重复的代码放在各个调用处，这样会增加代码量。在空间上，一般来说使用内联函数会导致生成的可执行文件变大。递归调用堆栈的展开并不像循环那么简单, 比如递归层数在编译时可能是未知的, 可能造成代码的无限inline循环。所以如果对递归函数进行强制内联，会得不偿失，运行性能变差。

#### cachegrind 收集的分析数据如何帮助衡量这些负面性能影响

Cachegrind模拟CPU中的一级缓存和二级缓存，能够精确地指出程序中cache的丢失和命中。

cachegrind收集到的包括高速缓存的命中率，高速缓存包括指令缓存和数据缓存，可以通过看缓存命中率的变化，看出内联函数使用不当带来的负面影响。此外也可以直接从指令数进行分析。

在现代机器上，L1 未命中通常会花费大约 10 个周期，LL 未命中可能会花费多达 200 个周期，而错误预测的分支会花费 10 到 30 个周期。详细的缓存和分支分析可以看到内联后的程序与机器交互中的变化。

### write up 4

> Give a reason why using pointers may improve performance. Report on any performance differences you observed in your implementation. 

修改为指针,主要修改的部分如下。修改了merge_p和copy_p。

定义两个指针指向left和right并进行依次遍历。

```c
  data_t* l_start=left;
  data_t* r_start=right;
 
  data_t* A0=A+p;
  for (; A0 <= A+r; A0++) {
    if (*l_start <= *r_start) {
      *A0=*l_start;
      l_start++;
    } else {
      *A0=*r_start;
      r_start++;
    }
  }
  mem_free(&left);
  mem_free(&right);
}

static void copy_p(data_t* source, data_t* dest, int n) {
  assert(dest);
  assert(source);

  data_t* s=source;
  data_t* d=dest;
  for (; s< source+n&& d< dest+n ; s++&&d++) {
    *d=*s;
  }
```

![image-20220102235512945](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220102235512945.png)

以上是sort_p的执行结果

对比执行sort_a

![image-20220103000729052](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103000729052.png)

可以看到运行时间变快了一点，这是因为每次通过下标访问数组，如a[i]需要进行乘法计算i*4（一个int的大小），而使用指针的情况下pointer++只需要执行一次加法计算

### write up 5

> Explain what sorting algorithm you used and how you chose the number of elements to be sorted in the base case. Report on the performance differences you observed.

base case为开始结束递归的最小的数组长度,默认的是要分到1后才会开始归并,这里尝试设置大一点看看运行效果

在sort_c中调用内置的isort插入排序进行测试

```c
extern void isort(data_t* start,data_t* end);
```

```c
if (p +16< r) {
    int q = (p + r) / 2;
    sort_c(A, p, q);
    sort_c(A, q + 1, r);
    merge_c(A, p, q, r);
  }else{
    isort(A+p,A+r);
  }
```

base case为16

![image-20220103111834431](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111834431.png)

![image-20220103110047564](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103110047564.png)

base case为32

![image-20220103111808985](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111808985.png)

![image-20220103110155240](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103110155240.png)

base case为64

![image-20220103111743550](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111743550.png)

![image-20220103111312362](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111312362.png)

base case为128

![image-20220103111718908](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111718908.png)

![image-20220103111436540](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111436540.png)

base case为256

![image-20220103111654075](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111654075.png)

![image-20220103111624199](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103111624199.png)

通过以上对比

| base case大小           | 16       | 32       | 64       | 128      | 256      |
| ----------------------- | -------- | -------- | -------- | -------- | -------- |
| random array 排序时间   | 0.095891 | 0.081380 | 0.087393 | 0.123463 | 0.166654 |
| inverted array 排序时间 | 0.184742 | 0.167607 | 0.191518 | 0.298657 | 0.436079 |
| D1 miss rate            | 2.0%     | 2.0%     | 1.8%     | 1.3%     | 0.9%     |
| Mispred rate            | 9.3%     | 7.8%     | 5.6%     | 3.5%     | 1.9%     |

可以看到随着base case的增大，miss rate和mispred rate都在减小。当base case为32和64的时候，random array以及inverted array排序时间都较小。

### write up 6

> Explain any difference in performance in your sort_m.c. Can a compiler automatically make this optimization for you and save you all the effort? Why or why not?

定义一个half,将数组下标从p到q都复制到里面。

修改copy_m如下

```python
static void merge_m(data_t* A, int p, int q, int r) {
  assert(A);
  assert(p <= q);
  assert((q + 1) <= r);
  int n1 = q - p + 1;
  //int n2 = r - q;

 // data_t* left = 0, * right = 0;
 // mem_alloc(&left, n1 + 1);
 // mem_alloc(&right, n2 + 1);
  data_t* half=0;
  mem_alloc(&half,n1+1);
  if (half == NULL) {
    mem_free(&half);
    return;
  }

  copy_m(&(A[p]), half, n1);
  //copy_c(&(A[q + 1]), right, n2);
  half[n1] = UINT_MAX;
  //right[n2] = UINT_MAX;

  data_t* l_start=half;
  data_t* r_start=A+q;
 
  data_t* A0=A+p;
  for (; A0 <= A+r; A0++) {
    if (*l_start <= *r_start) {
      *A0=*l_start;
      l_start++;
    } else {
      *A0=*r_start;
      r_start++;
    }
  }
  mem_free(&half);
  
}
```

![image-20220103123058276](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103123058276.png)

![image-20220103123033344](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103123033344.png)

同样base case取64，可以看到和writeup5相比，排序时间减小，D1miss rate从1.8%减小到了1.3%,分支mispred rate从5.6%减小到1.7%。

由于性能有较大提升，可以估计本身的O3等级优化并不会自动对这里进行优化。

### write up 7

> Report any differences in performance in your sort_f.c, and explain the differences using profiling data.

将原来的sort_f改为一个驱动函数drive

```c
static void drive(data_t* A, int p, int r, data_t* buffer) { 
  assert(A);
  if (p + 64 < r) { 
  int q = (p + r) / 2; 
  drive(A, p, q, buffer); 
  drive(A, q + 1, r, buffer); 
  merge_f(A, p, q, r, buffer); 
  } 
  else { 
  isort(A+p, A+r); 
  } 
}
```

在sort_f中申请缓冲区并调用drive函数

```c
void sort_f(data_t* A, int p, int r) {
  /*assert(A);
  if (p + 256 < r) {
    int q = (p + r) / 2;
    sort_f(A, p, q);
    sort_f(A, q + 1, r);
    merge_f(A, p, q, r);
  }else{
    isort(A+p,A+r);
  }*/
  assert(A); 
  data_t* buffer = 0; 
  mem_alloc(&buffer, r-p); 
  drive(A, p, r, buffer); 
  mem_free(&buffer);
}
```

，并重写一个递归实现的归

并排序函数my_sort_f，在sort_f中申请一个缓冲区用于后续merge操作，并最后释放。

merge_f传参中添加改缓冲区

```c
static void merge_f(data_t* A, int p, int q, int r,data_t* buffer)
```

![image-20220103140058378](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103140058378.png)

![image-20220103140116786](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20220103140116786.png)

对比writeup6,可以看到排序时间进一步减小，D1miss rate从1.3%减小到了1.2%,分支mispred rate从1.7%减小到1.4%。



### reference

https://www.kernel.org/doc/html/latest/admin-guide/perf-security.html

http://www.caotama.com/612336.html

https://stackoverflow.com/questions/21284906/perf-couldnt-record-kernel-reference-relocation-symbol

https://askubuntu.com/questions/1171494/how-to-get-perf-fully-working-with-all-features

https://zhuanlan.zhihu.com/p/74425386

内联测试：https://blog.csdn.net/weixin_39703605/article/details/108410698

https://valgrind.org/docs/manual/cg-manual.html