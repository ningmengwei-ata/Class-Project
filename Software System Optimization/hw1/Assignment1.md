## Assignment1

### Write-up 2

Answer the questions in the comments in pointer.c. For example, why are 

some of the statements valid and some are not? 

**1.What is the type of argv?**

char *argv[]是一个指向字符串的指针数组，他的元素个数是argc，存放的是指向每一个参数的指针

**2.printf("char d = %c\n", d); // What does this print?**

char d = 6

**3.pcp = argv;  // Why is this assignment valid?**

pcp是一个指向指向char类型指针的指针，argv也是一个指向指向char类型指针的指针，所以语句是合法的

**4.What is the type of pcc2?**

一个指向字符常量的指针

**5.For each of the following, why is the assignment:**

*pcc = ’7’; // invalid?  由于pcc是指向字符常量的指针，它指向的字符常量是不可改的，所以这条语句不合法

pcc = *pcp; // valid?  *pcp是一个字符型的指针，将pcc的指针指向pcp所指向的字符，合法

pcc = argv[0]; // valid? argv[0]是一个字符串，将pcc指针更改为指向该字符串，合法

**6.For each of the following, why is the assignment:**

cp = *pcp; // invalid? cp是指向字符串的常指针，不能修改该指针的指向。而 *pcp是指向char类型的指针

cp = *argv; // invalid? *argv是指向char类型的指针，不能改变常指针的指向

*cp = ’!’; // valid?cp是指向字符串的常指针,可以改变它指向的值，即改为这里的'!'

**7.For each of the following, why is the assignment:**

cpc = *pcp; // invalid? cpc是一个指向字符串常量的常指针，不可以更改指针的指向

cpc = argv[0]; // invalid? cpc是一个指向字符串常量的常指针，不可以更改指向的值

*cpc = ’@’; // invalid? 不可以更改指向的字符串常量的值

### Write-up 3

For each of the types in the sizes.c exercise above, print the size of a pointer to 

that type. Recall that obtaining the address of an array or struct requires the & operator. 

Provide the output of your program (which should include the sizes of both the actual type 

and a pointer to it) in the writeup. 

运行结果如图

![image-20210922223029576](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210922223029576.png)

### **Write-up 4**

File swap.c contains the code to swap two integers. Rewrite the swap() function 

using pointers and make appropriate changes in main() function so that the values are 

swapped with a call to swap(). Compile the code with make swap and run the program with 

./swap. Provide your edited code in the writeup. Verify that the results of both sizes.c and 

swap.c are correct by using the python script verifier.py. 

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210922222327779.png" alt="image-20210922222327779" style="zoom: 35%;" />

verify运行报错的解决方案

运行报错如图所示

![image-20210922234107954](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210922234107954.png)

在makefile中删除对应的库即可解决

### Write-up 5

Now, what do you see when you type make clean; make? 

mac本地跑报错如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210922230220677.png" alt="image-20210922230220677" style="zoom:50%;" />

解决方案参考链接：https://stackoverflow.com/questions/30011041/ios-with-swift-main-referenced-from-implicit-entry-start-for-main-executab

在makefile中将这里原先的-O1改为 CFLAGS_RELEASE := -O3 -DNDEBUG

并将这里CFLAGS_ASAN := -O1 -g -fsanitize=address 也改为-O3

运行结果

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923140850296.png" alt="image-20210923140850296" style="zoom:43%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923140913977.png" alt="image-20210923140913977" style="zoom:43%;" />

此外将CFLAGS_DEBUG := -g -DDEBUG -O0错误地改为了-O3会导致错误

### **Write-up 6**

What output do you see from AddressSanitizer regarding the memory bug? Paste it into your writeup here. 

![image-20210923141228994](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923141228994.png)

![image-20210923141325872](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923141325872.png)

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923141343470.png" alt="image-20210923141343470" style="zoom:65%;" />



### Write-up 7

After you fix your program, run ./matrix_multiply -p. Paste the program output showing that the matrix multiplication is working correctly. 

在testbed可以看到AB矩阵的维数不对应，无法相乘

这里将A矩阵的维数改为如图

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923144735923.png" alt="image-20210923144735923" style="zoom:50%;" />

运行成功

![image-20210923144641031](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923144641031.png)

这里打印出来4814031是由于没有对C矩阵初始化导致的

增加对C矩阵初始化的代码如下

![image-20210923162628059](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923162628059.png)

### Write-up 8

Paste the output from Valgrind showing that there is no error in your program.

在matrix multiply之后增加free matrix操作

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923154633283.png" alt="image-20210923154633283" style="zoom:50%;" />

![image-20210923154506297](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923154506297.png)

![image-20210923154610282](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210923154610282.png)

