# Average Score

## 待完成:

* 请在 DSPPCode.mapreduce.average_score.impl 中创建 ScoreMapperImpl, 继承 ScoreMapper, 实现抽象方法

* 请在 DSPPCode.mapreduce.average_score.impl 中创建 ScoreReducerImpl, 继承 ScoreReducer, 实现抽象方法

## 题目描述:

* 某班级有若干名同学，共修读三门必修课，现需计算这三门课程的班级平均成绩（向下取整）

* 输入格式: 学号,数学分析分数,概率论分数,实变函数分数

  ```
  10160001,98,80,75
  10160002,53,94,77
  10160003,61,86,91
  ```

* 输出格式: 课程名,平均分

  ```
  Function of Real Variable	81
  Mathematical analysis	70
  Probability Theory	86
  ```

  (课程名通过 Util.getCourseName 获得)
