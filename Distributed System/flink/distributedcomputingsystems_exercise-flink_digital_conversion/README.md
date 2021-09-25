# Digital Conversion

## 待完成:

* 请在 DSPPCode.flink.digital_conversion.impl 中创建DigitalPartitionerImpl, 继承 DigitalPartitioner, 实现抽象方法
* 请在 DSPPCode.flink.digital_conversion.impl 中创建DigitalConversionImpl, 继承 DigitalConversion, 实现抽象方法

## 题目描述:

* 有一个程序会持续地产生一些数字，这些数字都在[0, 9]区间中。小明希望将程序中产生的整数转换为英文单词并进行输出。此外，小明发现这些数字均匀地分布在[0, 4]和[5, 9]的区间内。因此，在对这些数据进行处理的时候，小明还希望将这些数字均匀地划分到两个任务中。

* 输入格式: 输入的每行为一个数字

  ```
  0
  1
  2
  3
  4
  5
  6
  7
  8
  9
  ```

* 输出格式:  每行输出数字对应的英文单词，单词的首字母要求大写。

  任务1的输出

  ```
  One
  Three
  Zero
  Two
  Four
  ```

  任务2的输出

  ```
  Five
  Seven
  Nine
  Six
  Eight
  ```

  
