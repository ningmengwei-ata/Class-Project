# 最大公约数(GCD)

## 待完成:

* 请在 DSPPCode.flink.gcd.impl 中创建GCDImpl, 继承 GCD, 实现抽象方法

## 题目描述:

* 给定两个正整数，求两者的最大公约数（GCD）。通常，两个正整数的最大公约数可用辗转相除法求解，其原理是：两个正整数的最大公约数等于其中较小的那个数和两数相除余数的最大公约数。例如：存在a和b两个正整数，并且a大于b，则a和b的最大公约数等于b与两数相除余数 `a mod b` 的最大公约数，记作`gcd(a, b) = gcd(b, a mod b)`。假定`r = a mod b`，若`r`依然为正整数，为了求解a和b的最大公约数，需进一步求解b和r的最大公约数，即`gcd(b, r) = gcd(r, b mod r)`。显然，计算最大公约数的过程是一个迭代计算过程，初始迭代时需对两个正整数做除法运算求得余数，然后每轮迭代都需对上一轮迭代的除数和余数做相同的除法运算。直到上一轮迭代的除数和余数可以整除，迭代计算方会终止。此时，上一轮迭代的除数即为最大公约数。**现要求求解两个整数的最大公约数，并且在求解过程中输出每轮迭代的除数和余数。**

  **注：请使用flink迭代算子实现。**

* 输入格式: 每行由一个字母标识符以及两个正整数`a`和`b`组成，三者之间使用空格分隔。字母标识符逐行递增，用于标识不同的最大公约数计算请求。例如：`A 5 2`表示这是第一个计算请求，要求求解5和2的最大公约数。

  ```
  A 5 2
  B 18 6
  ```
  
* 输出格式:  对于每个计算请求，要求依次输出每轮迭代的除数和余数。其输出格式为`(计算请求对应的字母标识符,除数,余数)`。例如：`(A,2,1)`表示第一个计算请求在第一轮迭代计算中的除数和余数分别为2和1。

  ```
  (A,2,1)
  (B,6,0)
  (A,1,0)
  ```
