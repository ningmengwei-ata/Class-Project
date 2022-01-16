### DaSE 计算机视觉 Assignment1 简答题

#### Problem1

**(a) 选取的 K 值太小，会有什么风险？**

预测结果对邻近的实例点非常敏感。如果邻近的实例点恰巧时候噪声，预测就会出错。k值的减小意味着整体模型变得复杂，容易发生过拟合。

**(b) 选取的 K 值太大，会有什么风险？**

与输入实例较远的训练实例也会对预测结果起作用，使预测发生错误。

**(c) 如图 1 所示，为什么有一些区域被标记为白色（没有类别）？**

这些白色区域没有获得任何一个区域的投票，是KNN无法决策的区域

**AS：票数相同**

**(d) 为了确定一个合适的 K 值，我们可以使用什么方法？**

通过交叉验证

思想：将样本的训练数据交叉的拆分出不同的训练集和验证集，使用交叉拆分出不同的训练集和验证集测分别试模型的精准度，然就求出的精准度的均值就是此次交叉验证的结果。将交叉验证作用到不同的超参数中，选取出精准度最高的超参数作为模型创建的超参数即可

实现思路：

- 将数据集平均分割成K个等份
- 使用1份数据作为测试数据，其余作为训练数据
- 计算验证准确率
- 使用不同的测试集，重复2、3步骤

#### **Problem 2**

**假设你拥有一个训练好的KNN模型，在训练集中有N个样本，训练和预测的时间复杂度分别是多少？**

训练时间复杂度为0.预测时间复杂度为O(n)

#### **Problem 3**

**假设你拥有一个训练好的 KNN 模型，现在在测试集上进行测试以便选取 K 值，测试之前，**

**你想估计一下这个模型用于预测所耗费的时间（假设每次计算距离所耗费的时间为 T，测试数**

**据中有 N 个观测值）**

**(a) 使用 1-NN 算法所耗费的时间是多少？**

1-NN算法所耗费的时间为NT

**(b)（选择）使用 1-NN，2-NN，3-NN 进行预测所耗费的时间之间的大致关系是什么？为什么？**

**A) 1-NN>2-NN>3-NN**

**B) 1-NN<2-NN<3-NN**

**C) 1-NN=2-NN=3-NN**

**D) 都不对**

C大致相当。kNN在训练数据集中动态确定和一个新输入样本相近的k个训练样本。不论是找最近的一个，还是最近的三个都需要计算和所有点之间的距离（这个是算法最耗时的部分），其余基于选出的点决定分类相对来讲，耗时较不显著。可粗略看作三者耗时相同。

**AS：大部分时间都在算距离上。不会因为用来分类的K的个数增加而增加。**

#### **Problem 4**

**假设一个 SVM 分类器已经能够在数据集上正确分类，那么微调该分类器，使得输出分数**

**发生小幅变化（比如 0.001），是否会改变损失函数的值？为什么？**

不会。当样本被正确分类时，y(wx+b)>0；y(wx+b)的绝对值越大表示决策边界对样本的区分度越好。当样本被正确分类且函数间隔大于1时，支持向量机的损失才是0，输出分数的小幅变化不会影响其结果。

#### **Problem 5**

**对于一个图片，它的 softmax loss 可能的最大值和最小值分别是多少？分别在什么情况下**

**能获得最大和最小值？**

softmax loss理论上最大值为正无穷，最小值为0。

在所有类别都分对是取到最小值，都分错时取到最大值。

#### **Problem 6**

**考虑一个 SVM 分类器，加大数据量是否一定导致 SVM 分类器的决策边界改变？为什么？**

不一定。如果这个数据点本身在边界之外“+”的那一侧，那么判决边界不受影响。
如果这个数据点在边界之内，或者在margin之外“-”的那一侧，那么这个点一定会成为新的支持向量。但是，判决边界并不一定发生变化，因为这个数据点可能能够被目标函数中的容错项处理。

**AS：决策边界 非凸问题**

#### Problem 7

**判断：在反向传播的过程中，梯度流经过诸如 Sigmoid 的等非线性层时，它的符号不会改变**

一般情况下不会改变。如果非线性层不适当（如sin、cos）符号会变。大部分我们常用的激活函数都具有单调性，使得在激活函数处的梯度方向不会经常改变，从而让训练更容易收敛。

**AS：sigmoid不改变**

#### Problem 8

![image-20211017171628458](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017171628458.png)

Step1:<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017171702001.png" alt="image-20211017171702001" style="zoom:60%;" />

1*（-1/1）=-1

Step2:<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017171834255.png" alt="image-20211017171834255" style="zoom:60%;" />

1*-1=-1

Step3:<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017171834255.png" alt="image-20211017171834255" style="zoom:60%;" />

-1*1=-1

Step4:<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017171834255.png" alt="image-20211017171834255" style="zoom:60%;" />

-1*1=-1

Step5:<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017172135827.png" alt="image-20211017172135827" style="zoom:60%;" />

-1*3=-3

-1*1=-1

-1*-2=2

-1*2=-2

补全后

![image-20211017172639543](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211017172639543.png)





<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211102145135956.png" alt="image-20211102145135956" style="zoom:50%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211102145153286.png" alt="image-20211102145153286" style="zoom:50%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20211102145221048.png" alt="image-20211102145221048" style="zoom:50%;" />

