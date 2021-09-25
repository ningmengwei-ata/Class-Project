# Consumer Statistics

## 待完成:

* 请在 DSPPCode.mapreduce.consumer_statistics.impl 中创建 ConsumerMapperImpl, 继承 ConsumerMapper, 实现抽象方法

* 请在 DSPPCode.mapreduce.consumer_statistics.impl 中创建 ConsumerReducerImpl, 继承 ConsumerReducer, 实现抽象方法

## 题目描述:

* 某超市有一份消费数据，该数据包括**消费者id、消费时间、消费金额以及消费者的会员属性**字段。每条数据表示某一顾客的一笔交易信息。其中，假设消费金额均为整数，最小为1；超市交易量很大，超过30亿。现需使用MapReduce对该数据进行处理以统计有多少会员和非会员在超市内进行了消费。此外，超市还要求分别计算出会员和非会员在超市内的消费金额总数。

* 输入格式: 

  ```
  3819	2021-03-27 21:30	357	vip
  3231	2021-03-27 21:30	77	non-vip
  ```

  输入包括四个字段：**消费者id、消费时间、消费金额以及消费者的会员属性**。以上述示例为例，`3819	2021-03-27 21:30	357	vip`表示id为3819的vip消费者在2021-03-27 21:30时刻消费了357元

* 输出格式: 

  ```
  non-vip	1	77
  vip	1	357
  ```

  输出包括三个字段：**会员属性、人数、消费金额总数**，各字段间用制表符分隔。以上述示例为例，`non-vip	1	77`表示超市内仅有1个非会员用户进行了消费，并且消费了77元。
