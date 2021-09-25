# Single Table Association

## 待完成:

* 请在 DSPPCode.spark.single_table_association.impl 中创建 SingleTableAssociationImpl, 继承SingleTableAssociation, 实现抽象方法

## 题目描述:

* 张三拿到了一份子女和父母对应的关系表，他发现关系表当中的一些人还存在着祖父母的亲属关系。关系表很大，手工分析可能需要几个小时，所以张三希望你能帮他编写程序自动地从中分析出哪些人存在着祖父母的亲属关系。

* 输入格式: 

  ```
  child parent
  Jack Philip
  Jack Jesse
  Philip Terry
  Philip Alma
  ```

  输入保存在文本中，文本的第一行为表头，其余行为孩子和父母的对应关系。以上述示例为例，`Jack Philip`表示Philip是Jack的父母

* 输出格式: 

  ```
  (Jack,Terry)
  (Jack,Alma)
  ```

   输出保存在文本中，每行为一个元组：**(孙子女姓名,祖父母姓名)**。以上述示例为例，`(Jack,Terry)`表示Terry是Jack的祖父母。
