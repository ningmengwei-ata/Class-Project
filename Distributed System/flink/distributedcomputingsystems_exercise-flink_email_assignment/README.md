# Word Count

## 待完成:

* 请在DSPPCode.flink.email_assignment.impl中创建EmailAssignmentImpl, 继承EmailAssignment, 实现抽象方法.

## 题目描述:

* 邮件地址的格式是`用户名@邮箱后缀`。ECNU的工作邮箱可以申请多个用户名作为别名。
  由于同一部门的用户拥有相同的邮箱后缀（如`dase.ecnu.edu.cn`），别名以部门为单位进行管理。
  同一部门内的别名不能发生重复（如不能同时存在两个用户同时拥有`a_name@dase.ecnu.edu.cn`的邮箱地址）。
  不同部门间的别名可以发生重复（如可以同时存在两个用户分别拥有`a_name@dase.ecnu.edu.cn`和`a_name@cs.ecnu.edu.cn`的邮箱地址）。
  此外，别名长度的取值范围是[5,11]，且字符只能为英文字母、数字或下划线（_）。
  现要求根据符合时间顺序的别名申请、注销的请求序列，计算各请求的完成状态（成功或者失败）。

* 输入:
  答题时无需编写代码解析文本格式的输入。需要实现的抽象方法的输入为`DataStream<Request>`类型。其中每个字段的含义如下:
    * type: 申请（APPLY）或注销（REVOKE）。
    * id: 发出请求用户的工号，每个工号只对应一个邮箱账户。
    * depart: 由于邮件系统设计的原因，部门通过两位代码表示，分别为`firstLevelCode`（一级部门代码，如`1`表示信息学部）、`secondLevelCode`（一级部门内的二级部门代码，如`3`表示数据学院）。
    * alias: 请求的别名。
  ```
    Request{type=APPLY, id=12345, depart=Department{firstLevelCode=1, secondLevelCode=1}, alias="*invalid"}
    Request{type=APPLY, id=22222, depart=Department{firstLevelCode=1, secondLevelCode=3}, alias="a_name"}
    Request{type=APPLY, id=25222, depart=Department{firstLevelCode=1, secondLevelCode=3}, alias="a_name"}
    Request{type=REVOKE, id=22222, depart=Department{firstLevelCode=1, secondLevelCode=3}, alias="a_name"}
    Request{type=APPLY, id=25222, depart=Department{firstLevelCode=1, secondLevelCode=3}, alias="b_name"}
  ```

* 输出:
  输出每个请求的完成状态，`SUCCESS`表示请求成功执行，`FAILURE`表示请求执行失败。
  ```
    FAILURE
    SUCCESS
    FAILURE
    SUCCESS
    SUCCESS
  ```