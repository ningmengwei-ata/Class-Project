## Assignment2

#### 1.安装 OpenTuner 并记录执行过程

```
conda create --name=opentuner python=3.8
```

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925121828036.png" alt="image-20210925121828036" style="zoom: 33%;" />

```
conda activate opentuner
pip install -r requirements.txt -r optional-requirements.txt
```

![image-20210925122430825](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925122430825.png)

![image-20210925122441831](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925122441831.png)

```python
python setup.py develop
```

运行中出现如下报错

![image-20210925161156608](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925161156608.png)

进入该网址 http://johnmacfarlane.net/pandoc/installing.html下载安装pandoc即可解决

最后结果如下

![image-20210925130000211](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925130000211.png)

测试installation

![image-20210925162727278](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925162727278.png)

#### 2.阅读论文《OpenTuner: An Extensible Framework for Program Autotuning》，理解其主要思想。

为了更好或者更便携的性能，在许多领域都演示了自动调谐。 但是，autotuners本身通常不是很便携，因为使用域通知的搜索空间表示是很关键的。

OpenTuner是构建领域特定多目标程序autotuners的新框架。 OpenTuner支持完全可以定制的配置表示形式，可以扩展的技术表示，以便与调优程序通信。 OpenTuner中的关键功能是同时使用不同搜索技术的集合，实现良好性能的技术将接收更大的测试预算和技术，这将导致性能低下。

#### 3.练习 Optimizing Block Matrix Multiplication 教程并记录执行过程和结果，了解开源项目

开发和维护流程。

![image-20210925161029452](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925161029452.png)

解决方法参考https://github.com/jansel/opentuner/issues/105

将import adddeps注释掉

```python
gcc_cmd += '-DBLOCK_SIZE='+ str(cfg['blockSize'])
```

在此处添加str

运行结果

![image-20210925165031109](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925165031109.png)

mmm_final_config.json可以看到

![image-20210925165140104](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20210925165140104.png)