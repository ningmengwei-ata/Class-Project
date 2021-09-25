# Max Temperature

## 待完成:

* 请在 DSPPCode.mapreduce.max_temperature.impl 中创建 MaxTemperatureMapperImpl, 继承 MaxTemperatureMapper, 实现抽象方法

* 请在 DSPPCode.mapreduce.max_temperature.impl 中创建 MaxTemperatureReducerImpl, 继承 MaxTemperatureReducer, 实现抽象方法

## 题目描述:

* 给定一份年份与温度的统计表，现需计算统计表中每个年份的最大温度。

* 输入格式: 年份 温度

  ```
  1990 21
  1990 18
  1991 35
  1992 30
  1990 25
  1992 21
  1992 40
  ```

* 输出格式: 年份 最高温度

  ```
  1990 25
  1991 35
  1992 40
  ```
