# Student Info

## 待完成:

* 请在 DSPPCode.spark.station_statistics.impl 中创建 StationStatisticsImpl, 继承 StationStatistics, 实现抽象方法

## 题目描述:

* 假定一群人早晨徒步从A地区去往B地区踏青。其中，一部分人早晨出发得早，傍晚就徒步从B地区返回A地区，并于当天18:00:00之前抵达A地区；
  而另一部分人由于早晨出发得晚，当天未从B地区返回A地区，直接在B地区留宿一晚。
  在来回途中，每个人都会经过多个基站。
  现统计了当天每个人往返途中进入每个基站的时刻和从每个基站出来的时刻，要求计算出每个人在每个基站停留的总时间（以秒为单位）。

* 输入格式:

  统计数据保存在一个文本文件中，文件的每行由姓名、基站名、进入基站的时刻或从基站出来的时刻信息组成。信息之间用空格分隔。

  ```
  Mark station1 12:00:01
  Mark station1 12:01:01
  Bill station1 11:04:05
  Bill station1 11:06:08
  Bill station2 14:04:04
  Bill station2 14:07:12
  ```

* 输出格式:

  ```
  (Bill,station1,123)
  (Bill,station2,188)
  (Mark,station1,60)
  ```