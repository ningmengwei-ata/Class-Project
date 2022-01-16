## Assignment5

#### 1.安装 OpenCilk 并记录执行过程，了解并尝试 pre-build binaries 和 build from source code两种安装方式。

##### Pre-build binaries

下载对应的压缩包

![image-20211112151222254](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211112151222254.png)

运行对应的.sh文件

**build from source code**

出现报错：

1.fatal: could not create work tree dir 'infrastructure': No space left on device

虚拟机内存授权故障，重新设置properties,如还不行则需要重装系统

2.gnutls recv error

![image-20211108102924466](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211108102924466.png)

实验步骤

1. clone opencilk infrastructure repository

   ```
   git clone -b opencilk/v1.0 https://github.com/OpenCilk/infrastructure
   ```

   ![image-20211112144146559](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211112144146559.png)

2. Run the following script to get the OpenCilk source code

   ```
   infrastructure/tools/get $(pwd)/opencilk
   ```

   ![image-20211112150643186](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211112150643186.png)

   下载失败 查看网络 vpn等 重新下载

3. run the script to build opencilk

   ```
   infrastructure/tools/build $(pwd)/opencilk $(pwd)/build
   ```

![image-20211113155649500](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113155649500.png)

#### 2.阅读教程“Cilk Tutorial”，理解 cilk_spawn，cilk_sync，cilk_for 和 locks 等基本实现。

**Click spawn:**当 cilk_spawn 在函数调用之前创建并行工作，从而导致函数被spawn，最后的输出结果（如有）是乱序的。当一个函数产生另一个函数时，原始函数被称为父函数，而另一个函数被称为子函数。click_spawn不能作为另一个函数的参数。spawning 的语义与 C/C++ 函数或方法调用的不同之处在紧随spawn后的代码中，允许与child并行执行。

**Click sync:**cilk_sync 保证所有先前生成的任务在程序继续之前相互等待完成。

**Click for:**cilk_for 构造允许循环迭代并行运行的循环。cilk_for 将一个循环分成包含一个或多个循环迭代的块。 一旦循环中断，每个块都会在特定的执行线程上执行。

click for 有一些限制：1）不能更改循环体中的循环控制变量。2）不能在 C++ 的循环之外声明循环控制变量。

cilk_for 语句将循环分成多个较小的块，这些块在特定的执行线程上运行。 每个块中的最大迭代次数定义为粒度。 作为一个块运行的实际迭代次数通常小于粒度。

**Locks:**锁是防止多个线程同时更改变量的同步机制，可以有效消除数据竞争

有一些情况锁无法消除数据竞争。首先，可能会发生死锁，即所有线程都在互相等待。  其次，由于线程必须相互等待，代码的锁定部分被序列化，从而导致性能问题。 在 Cilk 中，通过减速器解决与锁相关的大多数问题

#### 3.阅读教程“Research and Teaching with OpenCilk”，了解 OpenCilk 的研发历程，尝试教程P31~P36 的 6 个 Demo，记录执行过程和结果。

##### Demo1 Compile and Run

报错 libtinfo.so.5: cannot open shared object file

![image-20211113130339304](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113130339304.png)

解决方法：sudo apt-get install libncurses5

**运行过程和输出**

![image-20211113130519957](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113130519957.png)

##### Demo2 Using Cilksan

**运行过程和输出**

⚠️ 命令最好直接在虚拟机打一遍 复制黏贴会报错

![image-20211113131222681](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113131222681.png)

手打一遍即恢复正常

![image-20211113131127351](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113131127351.png)

![image-20211113131252123](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113131252123.png)

**增加 –fcilktool=cilkscale 编译符来用Cilkscale 计算 work and span** 

![image-20211113131610977](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113131610977.png)

##### Demo3: Analyze a Region

报错：use of undeclared identifier 'wsp_t'

![image-20211113135600514](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113135600514.png)

补充头文件 \#include <cilk/cilkscale.h>

**运行过程和输出**

![image-20211113140130918](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113140130918.png)

可以注意到多了sample_qsort这一行输出

##### Demo 4: Download cilkscaler visualizer

![image-20211113140429949](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113140429949.png)

##### Demo 5:Cilkscaler visualizer

![image-20211113144204523](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113144204523.png)

安装matplotlib

![image-20211113145055213](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113145055213.png)

得到的performance图片

![image-20211113145831194](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211113145831194.png)