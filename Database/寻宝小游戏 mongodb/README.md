##### 

### 一、数据库设计

本次实验的数据库包含四个文档集

分别为usr、treasure、market和atomic

**usr**文档集中包括:

**uid** : string类型，为玩家的用户名，不可重复

**money**:Int类型，为玩家拥有的金币数量

**luck**：Int类型，为玩家当前的幸运值，由饰品的属性决定

**work**:Int类型，为玩家当前的工作能力，由工具的属性决定

**case**:list类型，为玩家当前的宝物盒

**wear_on**:list类型,为玩家当前佩戴的宝物，玩家最多只能佩戴一个luck属性宝物，两个work属性宝物

**on_market**:list类型,为玩家当前挂牌的宝物集合，其中的部分宝物同时存放在case和on_market中，直到宝物被卖掉

以下两个属性在手动寻宝和工作中使用，在自动寻宝和工作中则不需要

**FLAG_explore**:bool类型，默认值为False，用来表示游戏中的一天里该用户是否已经寻过宝

**FLAG_work**:bool类型，默认值为False，用来标识游戏中的一天里该用户是否已经工作过

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201022192842581.png" alt="image-20201022192842581" style="zoom:30%;" />

**treasure**文档集包括：

**tr_name**:string类型，不可重复

**luck**:int类型，宝物的luck属性值

**work_ab**：int类型，宝物的工作属性值

在该游戏中我们定义的宝物,若luck值非0，work_ab为0.若luck值为0,work_ab不为0

**price**:int类型，宝物的价格，该值正比于luck非0宝物的luck值，正比于work_ab非0宝物的work_ab值，所以我们在回收宝物时直接通过price值是否为最低来选择被回收的宝物

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201022201012945.png" alt="image-20201022201012945" style="zoom:30%;" />

**market**文档集包括:

**_id**：ObjectId类型，为文档的主码，创建的时候就已经建立索引

**tr_onsale**:string类型，挂牌物品的名称

**saler**:string类型，卖该宝物的玩家

**price**:int类型，卖家给出的价格

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201022201935831.png" alt="image-20201022201935831" style="zoom:30%;" />

**atomic** 文档集：

 该文档集是为了保证交易的原子性。因为本次实验涉及到买卖操作，在购买的前后记录下卖家和买家的个人信息，方便如果发生故障之后进行恢复。记录所有交易需要修改的量，方便进行Undo/Redo操作。

_id：ObjectId类型，为文档的主码，创建的时候就已经建立索引

**saler_name**：String类型，卖家的名字

**customer_name**：String类型，买家的名字

**product**：String类型，商品名称

**price**：int类型，买家给出的商品价格

**saler_money_before**：int类型，交易前卖家拥有的金币数量

**customer_money_before**：int类型，交易前买家拥有的金币数量

**saler_money_after**：int类型，交易后卖家的金币数量

**customer_money_after**：int类型，交易后买家的金币数量

**saler_case_before**:list类型，交易前卖家的宝物盒

**customer_case_before**:list类型，交易前买家的宝物盒

**saler_case_after**:list类型，交易后卖家的宝物盒

**customer_case_after**:list类型，交易后买家的宝物盒

**saler_on_market_before**:list类型，交易前卖家挂牌的宝物集合

**saler_on_market_after**:list类型，交易后卖家挂牌的宝物集合

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025120346783.png" alt="image-20201025120346783" style="zoom:33%;" />

### 二、相关函数的实现

**tomorrow**函数:起线程实现每一天的流逝

本次实验实现自动工作寻宝和手动工作寻宝两种版本，两种项目对应的tomorrow代码略有不同

手动工作寻宝

在app.py中的tomorrow函数中，对于每一个用户名将对应的是否工作和寻宝的标志位按照固定的时间间隔重置为false

```python
while 1:
        print("One day passed!\t")
        for name in users:
            # 20s as a day
            #手动工作&寻宝版
            USER.update_one({'_id':name},{'$set':{'FLAG_work':False}})
            USER.update_one({'_id':name},{'$set':{'FLAG_explore':False}})
        time.sleep(60.0)
```

在页面中调用work和explore，结果如下图所示

当天第一次工作和寻宝会显示相关信息

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201021163203078.png" alt="image-20201021163203078" style="zoom:33%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201021162644926.png" alt="image-20201021162644926" style="zoom:33%;" />

当天已工作 寻宝会显示已经工作过 已经寻宝过

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201021162746838.png" alt="image-20201021162746838" style="zoom:50%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201021162721919.png" alt="image-20201021162721919" style="zoom:40%;" />

自动工作寻宝

在app.py中编写自动工作和寻宝的函数work_auto和explore_auto并在定义的每一天演变函数tomorrow中调用这两个函数即可实现

```python
for name in users:
            #自动工作&寻宝版
            work_auto(name)
            explore_auto(name)
```

根据一天一天的时间的流逝，自动寻宝和工作，如图所示

初始

![image-20201025083958040](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025083958040.png)一天后

![image-20201025084021563](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084021563.png)

两天后

![image-20201025092434617](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025092434617.png)

当你在一天内尝试再次工作时，会返回

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025092501545.png" alt="image-20201025092501545" style="zoom:35%;" />

当你在一天内尝试再次寻宝时，会返回

<img src="/Users/wangwenqing/Desktop/截屏2020-10-24 下午8.59.34.png" alt="截屏2020-10-24 下午8.59.34" style="zoom:33%;"/>

**login**函数：**需要访问数据库usr文档集0或1次，若为新的用户名，则需插入到usr文档集中**

判断是否为新用户，若是则像usr 文档集中插入一条新的记录，最后返回是否是新用户以及用户信息

该函数的url接口为/\<string:name\>

```python
IF_NEW = False
    if USER.find_one({'_id': name})== None:   
      USER.insert_one({'_id':name, 'money':0,'luck':0,'work':0,"FLAG_explore" : False, "FLAG_work" : False,"case" : [ ], "wear_on" : [ ] })
      IF_NEW = True
```

**show**函数:不需要参数，**访问usr文档集一次**

显示用户所有信息的函数

该函数的url接口为/\<string:name\>/show

```python
return jsonify({'name':show['_id'], 'money':show['money'],"usr info":USER.find_one({"_id":name})})
```

**work**函数: 不需要参数，**访问数据库2次，一次查询，一次更新**

**一次性从数据库中读取所有需要的数据，然后再分别取其中的不同属性给变量赋值，而不是用GetU函数每次只读一个属性，从而有效减少访问数据库的次数**

work_auto函数与work函数的核心思想相似，除了去除了jsonify返回到链接的步骤，这里不再赘述

work函数1）首先调取FLAG_work，如果FLAG_work为true则返回今天已工作，无需再工作

2）如果FLAG_work为False,获取当前用户的工作能力，如果能力为0则将工作能力设置为5，并更新用户的金币数。金币数为已有金币数+当前的工作能力。

3）最后，更新usr数据库的金币数，工作能力和FLAG_work

该函数的url接口为/\<string:name\>/work

```python
work = GetU(name, 'work')
if work==0:
   work=5
money = GetU(name,'money')+work
USER.update_many({"_id":name},{"$set":{"money":money,"work":work,"FLAG_work":True}})
```

**explore**函数: **访问usr文档集两次，一次查询，一次更新case。总共访问数据库2次。**如果寻宝后存储箱满了，则需要回收。

explore_auto函数与explore函数的核心思想相似，除了去除了jsonify返回到链接的步骤，这里不再赘述

该函数的url接口为/\<string:name\>/explore

explore函数1）首先调取FLAG_explore，如果FLAG_explore为true则返回今天已寻过宝，无需再寻宝

2）如果FLAG_explore为False,依据用户的luck值为用户随机寻宝

3）如果当前luck值为0，由于不存在宝物的price为0，为用户分配宝物盒中price最低的宝物

如果当前luck不为0，先确定可以随机寻的宝物价值在(0.8\*luck,1.2\*luck）中

如果该价值区间中没有宝物则扩大范围随机寻的宝物价值在(0.6\*luck,1.4\*luck)中

以此类推。在确定好的有宝物的最小范围中随机生成一个宝物，加入当前用户的宝物盒

4）如果寻宝后存储箱满了，则需要回收。

5）最后更新用户的case和FLAG_explore

```python
FLAG = GetU(name,'FLAG_explore')
  if FLAG:
    return "You have already obtained a treasure today! Don't be greedy"
  luck = GetU(name, 'luck')
  area = list(TRSEASURE.find({'price': {'$gt':0.8*luck, '$lt':1.2*luck}}))
  if len(area)==0 and luck==0:
    new_tr_name='small'
  elif len(area)==0 and luck!=0:
    area.extend(list(TRSEASURE.find({'price': {'$gt':0.6*luck, '$lt':1.4*luck}})))
    if len(area)==0:
        area.extend(list(TRSEASURE.find({'price': {'$gt':0.4*luck, '$lt':1.6*luck}})))
```

```python
 i = random.randint(0,len(area)-1)
 new_tr_name=area[i]['_id']
 case=GetU(name,'case')
 case.append(new_tr_name)
 USER.update_many({'_id':name},{'$set':{'case':case,'FLAG_explore':True}})
 return jsonify({'usr info': USER.find_one({"_id":name}),'get new treasure':area[i],'tr_num':len(GetU(name,'case'))})
```

**recycle**函数：**函数用于在藏宝箱中宝物数量>5时系统回收价值最低的宝物**，在测试中不需要任何参数。

**为了减少访问数据库的次数，另写回收函数需要用户将当前的case传入，最后返回新的case。该函数执行过程中不需要另外访问数据库，因为调用它的函数已经访问数据库并初始化类，之后也会再次更新数据库。**

因为宝物的价值的大小与宝物的工作属性和幸运值正相关，所以直接回收宝物价值最低的宝物即可。如果宝物盒中的宝物数>5,则用for循环找出价值最低的宝物，从宝物箱中移出并更新usr数据库用户的宝物盒信息

该函数的url接口为/\<string:name\>/recycle

**wear**函数: 需要访问usr文档集两次，一次查找，一次更新

通过对宝物的luck取bool值得到其tag,即是否为luck属性宝物。对宝物的work属性同理。对于已戴上宝物集合中的宝物，计算其中luck属性和work属性的宝物分别的数量.

1)如果对应属性的宝物已经装满，则返回没有地方装新的宝物。

2)在on_market 中检索该物品是否已经挂牌，如果该物品已经挂牌，则必须留在藏宝箱中，不能佩戴。

否则，戴上所选择的宝物，并从宝物盒中移除该宝物。用户的幸运值加上已戴上的宝物的幸运值。用户的工作能力加上佩戴的宝物的工作属性值。更新用户数据库中的wear_on、case、luck、work值。如果遍历case中都没有想找的宝物则返回宝物不在宝物盒中。

该函数的url接口为/\<string:name\>/wear/\<string:tr_name\>

```python
      if (TAG and luck_num) or ((not TAG) and (working_num ==2)):
        return " No space to wear on!"
      wear_on.append(i)
      case.remove(i)
      luck =GetTr(i,'luck') + GetU(name, 'luck')
      work = GetTr(i,'work_ab') + GetU(name, 'work')
      USER.update_many({'_id':name},{'$set':{'wear_on':wear_on, 'case':case, 'luck':luck, 'work':work}})
      return jsonify({'usr': name,'luck':luck,'work':work,'treasure wore on':GetU(name,'wear_on'),'treasures in case':GetU(name,'case')})
  return "Your treasure does not exist!"
```

尝试戴上在宝物盒中的宝物small，返回结果如下

![image-20201025084305039](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084305039.png)

尝试戴上不在盒子中的宝物，会返回

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084632488.png" alt="image-20201025084632488" style="zoom: 10%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084225215.png" alt="image-20201025084225215" style="zoom:33%;" />

**unwear**函数:

与wear函数的实现类似。需要访问usr文档集2次，一次查找，一次更新。

1）查看想要脱下的物品是否已经佩戴，如果没有佩戴则无法摘下。

2）如果宝物在已佩戴宝物集合中，取上所选择的宝物，并向宝物盒加入该宝物。用户的幸运值减去取下的宝物的幸运值。用户的工作能力减去取下的宝物的工作属性值。更新用户数据库中的wear_on、case、luck、work值。

调用recycle函数，如果宝物盒中宝物数超过5，则需要回收。

该函数的url接口为/\<string:name\>/unwear/\<string:tr_name\>

![image-20201025084506067](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084506067.png)

随机测试两个未佩戴的宝物，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084717659.png" alt="image-20201025084717659" style="zoom:10%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084853526.png" alt="image-20201025084853526" style="zoom:20%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084207314.png" alt="image-20201025084207314" style="zoom:33%;" />

**find_onmarket**函数:

访问usr文档集一次，一次查询操作，共访问数据库一次

查看某用户on_market中有无某宝物,有则result为宝物已被挂在市场，否则返回该宝物不在市场中

该函数的url接口为/\<string:name\>/find_onmarket/\<string:tr_name\>

当用户还没将宝物盒中的宝物挂牌时，我们看到的结果如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084403116.png" alt="image-20201025084403116" style="zoom: 25%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084339889.png" alt="image-20201025084339889" style="zoom: 33%;" />

**add_onmarket**函数:

**add_onmarket用于挂牌物品，需要两个参数，挂牌的物品名称和给出的价格**

需要访问usr文档集2次，一次查询一次更新。访问treasure文档集一次，更新宝物的价格。访问market文档集一次，插入宝物的名称、价格、卖家信息。访问4次数据库

如果想挂牌的宝物在宝物箱中，在挂牌宝物的集合中加入宝物的名字，根据用户的输入更改自主确定宝物售卖的价格，在市场中加入宝物的卖家，名字和价格信息，返回宝物已挂牌，否则返回宝物不在存储箱中

```python
  if tr_name in case:
    onmarket.append(tr_name)
    USER.update_one({'_id':name},{'$set':{'on_market':onmarket}})          
    new_price=price
    TRSEASURE.update_one({'_id':tr_name},{'$set':{'price':new_price}})
    MARKET.insert_one({'saler':name,'tr_onsale':tr_name,'price':new_price})
    return jsonify({"result": 'treasure is added to the market', "ok": 1})
  else:
    return jsonify({"result": 'treasure is not in your case', "ok": 0})
```

该函数的url接口为/\<string:name\>/add_onmarket/\<string:tr_name\>/\<int:price\>

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025090935076.png" alt="image-20201025090935076" style="zoom:25%;" />

尝试将宝物盒中的small挂牌，结果如下

![image-20201025090854977](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025090854977.png)

尝试将不在宝物盒中的宝物挂牌，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025091031619.png" alt="image-20201025091031619" style="zoom:50%;" />

**un_onmarket**函数:

**un_onmarket撤回挂牌物品，需要一个参数，挂牌的物品名称，需要访问数据库user文档集2次，一次查询，一次更新，market文档集1次，更新操作，总共访问三次数据库。**

与add_onmarket函数类似。

1.查找用户准备撤回的物品是否在藏宝箱中，若不存在则是非法操作。

2.查找on_market是否有要撤回的宝物。若没有找到，则反馈给用户没有挂牌该物品。

3.若查找到，在on_market中移除要撤回的宝物

4.从market中删除撤回的宝物的相关信息

5.更新卖家的藏宝箱和saler信息

如果想收回的宝物在已挂牌宝物集合中，在挂牌宝物的集合中去除宝物的名字，调用unsale函数，更新用户的on_market集合，返回宝物已收回，否则返回宝物未挂牌

该函数的url接口为/\<string:name\>/un_onmarket/\<string:tr_name\>

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025091656869.png" alt="image-20201025091656869" style="zoom: 33%;" />

**unsale**函数：

该函数的编写，为方便对un_onmarket和unsale的分别单独测试之用，其具体功能已在un_onmarket中描述

如果在market中找到需要unsale的记录，在market中删除该记录，向宝物盒增加该宝物，并返回用户当前宝物盒的信息。否则，显示不能撤回你未挂牌的宝物

该函数的url接口为/\<string:name\>/unsale/\<string:tr_name\>

**get_market**函数:

访问市场出售信息，不需要参数，访问market文档集一次

直接返回market的所有信息，包括卖家、宝物名称和价格

```python
for treasure in MARKET.find():
    res += str(process_dict(treasure))
    res += '<br><br>'
  return "<h1>MARKET</h1><br><br>" + res
```

该函数的url接口为/get_market/\<string:name>

页面返回结果如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025101246743.png" alt="image-20201025101246743" style="zoom:33%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025100703155.png" alt="image-20201025100703155" style="zoom: 25%;" />

**buy**函数：

**buy函数用于购买挂牌物品，需要一个参数，为想要购买的宝物名称**

需要访问数据库user文档集2次，2次查询，分别查询买家和卖家的相关信息，2次更新（买家和卖家），market文档集1次，删除操作，删除已售商品，atomic文档集1次插入操作，总共访问六次数据库。

1）用户不可以买自己出售的商品。

2）如果顾客的钱数小于商品的价格，返回“对不起，你没有足够的钱”

3）否则，删除market中的对应记录，将宝物加到顾客的宝物盒中，顾客的现有钱数为原有钱数减去宝物的价格。更新顾客的储物箱和拥有金币数的信息。将宝物从卖家的宝物盒和挂牌宝物集合中删除，卖家的现有钱数等于原有钱数加上卖出的宝物的价格。

采用不指定特定的卖家，系统自动挑选一位拥有该商品的卖家。

该函数的url接口为/\<string:name\>/buy/\<string:tr_name\>

运行buy测试前，需对数据库里的market进行deleteMany并初始化

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025091759020.png" alt="image-20201025091759020" style="zoom:25%;" />

![image-20201025102734469](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025102734469.png)

![image-20201025102746235](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025102746235.png)

![image-20201025102758740](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025102758740.png)

t6商品价格为20元，我们可以看到顾客doge的money-20，卖家peggy的money+20,顾客doge的case中多出了买来的t6,卖家的case中删除了t6,on_market中的t6也被成功删除

当用户钱不够时，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025101340965.png" alt="image-20201025101340965" style="zoom:25%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025091834930.png" alt="image-20201025091834930" style="zoom: 33%;" />

辅助函数：

**GetU**函数

通过用户的用户名主键找到对应属性值

```python
return USER.find_one({'_id':usr_name})[attri]
```

**GetTr**函数

与GetU函数类似，通过宝物的宝物名主键找到对应的属性值



### 三、debug主要问题记录

1.对于Internal Server Error的处理

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013134707210.png" alt="image-20201013134707210" style="zoom:17%;" />

利用jupyter notebook 来测试错误，举例如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013134617009.png" alt="image-20201013134617009" style="zoom: 33%;" />

发现由于dict不能直接相加 在将GetUser返回值改成User.find_one('uid':user_name)即可

2.键值冲突(mongodb插入记录是提示 E11000 duplicate key error index )

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013170004485.png" alt="image-20201013170004485" style="zoom:25%;" />

尝试更新数据库之前，进行delete_many操作,并没有效果

采用如下方法成功解决

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013172811981.png" alt="image-20201013172811981" style="zoom: 19%;" />

3.pytest测试中TypeError: 'NoneType' object is not subscriptable

出错原因

google搜索错误信息

在使用 `pymongo` 访问 users collection 里的数据时，

```py
user = mongo.db.users.find_one({'username': username})
```

由于该条数据不存在，所以 user 的值变成了 `None`。
但是我又像下面这样去获取 user 的属性，

```py
if password == user['password']:
```

所以 `None` 里没有这个属性，肯定就报错了。

情况一：通过pytest打印出错信息

在该行后

```
json = response.get_json()
```

多加一行

```
print(json)
```

打印结果如图所示 发现返回的json为None导致错误

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013194312677.png" alt="image-20201013194312677" style="zoom:25%;" />

解决方法如下：

通过运行app.py Running on http://127.0.0.1:5000/try/blue/work

发现只显示了一行You have already worked today! 这并不是json格式的，所以json返回为none

修改为当前的钱数，并修改测试代码即可

情况二：

```python
case = GetUser('peggy','case')
print(case)
case_num = len(case)
print(case_num)
index = random.randint(0,case_num-1)
print(index)
tr = case[index]
print(tr)
tr_name = GetTr(tr,'_id')
print(tr_name)
```

通过打印结果发现用户case的格式定义不统一，对新插入case的数据需从新定义其格式

![image-20201016202945858](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201016202945858.png)

4.Coverage report  no data to report

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201013213130131.png" alt="image-20201013213130131" style="zoom:33%;" />

配置文件设置有误

将source改为当前文件需要测试的文件夹implement即可

5. AttributeError: 'dict' object has no attribute 'in_transaction'

   问题描述

   <img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201018105345243.png" alt="image-20201018105345243" style="zoom:23%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201018103345634.png" alt="image-20201018103345634" style="zoom:23%;" />

查明为Insert_one格式出错，导致无法向market中加入指定记录

改为如下写法，即可正确运行

```python
MARKET.insert_one({'saler':name,'tr_onsale':tr_name,'price':price})
```

6.前端template not found

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201019000051188.png" alt="image-20201019000051188" style="zoom:16%;" />

错误原因为html存放的文件夹命名错误，改为templates即可

6.自动寻宝的问题

打开了debug模式，在自动寻宝和自动工作中，每次回自动跳动两次。

如图设置30sec寻一次宝，每30s会出现连续两次打印one day passed的情况

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201024180428162.png" alt="image-20201024180428162" style="zoom:30%;" />

写了很多种时间自动流逝，定时执行某函数的饿代码在juypter notebook下测试正常的代码放到vscode就测试失败，把debug模式关闭即可正常运行。

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201024180525720.png" alt="image-20201024180525720" style="zoom:30%;" />

### 四、测试与优化

1.对于数据库格式的优化

mongo engine 和mongoDB直连

Monogo engine简洁且灵活，mongoDB直连较为固定。两种方式都做了尝试。本次项目采用mongo engine

mongoDB直连如图所示,每次需要增加一个属性值的时候都需要将所有已有属性重复更改，且无法灵活的设置主键。mongoengine设置见一中的数据库设计的图片，这里不重复插入。

![image-20201025121121205](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025121121205.png)

2.对于测试文件的优化 从coverage 60%到100%

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201014135355316.png" alt="image-20201014135355316" style="zoom:25%;" />

在setup.cfg文件中增加show_missing=True或使用coverage report -m

以方便查看测试没有cover到的源代码行

测试设置

1.对于login的测试

除了测试已有的用户名是否已经在插入到数据库中外，增加测试一个随机生成的用户名字符串能否成功被检测为新来的用户并插入数据库中。生成随机用户名的示例如下

```python
def generate_random_str(randomlength=16):
    """
    生成一个指定长度的随机字符串
    """
    random_str = ''
    base_str = 'abcdefghijklmn'
    length = len(base_str) - 1
    for i in range(randomlength):
        random_str += base_str[random.randint(0, length)]
    return random_str
```

2.对于work函数的测试

对于当天已工作和未工作的情况都要进行测试。

插入一行

```python
USER.insert_one({ "_id" : "test", "money" : 0, "luck" : 0, "work" : 0, "FLAG_explore" : True, "FLAG_work" : True, "case" : [ ], "wear_on" : [ ] })
name_5='test'
```

保证数据库里有记录pink且它的flag_work为false

通过test来测试flag_work为true的用户的运行情况

![image-20201014211422811](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201014211422811.png)

对这两个函数进行修改，调整后coverage report 覆盖率增加到75%

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201014213837436.png" alt="image-20201014213837436" style="zoom:25%;" />

通过每次重新运行db.py对数据库的项进行初始化后，覆盖率进一步提高

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201015211317564.png" alt="image-20201015211317564" style="zoom:25%;" />

3.对于explore函数的测试

与work函数的测试类似，对于当天已寻过宝和未寻过宝的情况都要进行测试。

```python
response1 = client.get("/try/%s/explore" % (name_5))
        json1 = response1.get_json()
        assert json1['tr_num']==len(GetU(name_5,'case'))
```

比对本地GetU函数得到的用户的宝物数量与json返回的数量是否一致

4.对于recycle函数的测试

首先对recycle函数 保证通过

```python
@bp.route("/<string:name>/recycle", methods=['GET'])
```

```python
return jsonify({'usr info': USER.find_one({"_id":name}),'case':GetU(name,'case')})
```

设置测试样例的case中的宝物数为5和6，分别测试在不需要回收宝物时，宝物有无被错误回收以及当case中宝物数量超过限定值时是否宝物已被成功回收

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201015233122038.png" alt="image-20201015233122038" style="zoom:25%;" />

5.对wear函数的测试

测试1)盒中有宝物且wear_on中宝物没有满，可以穿戴宝物的情况

2)如果如果已经有了一件幸运宝物，新来的宝物也是幸运宝物 那么不能戴上 会在网页返回no space to wear以及如果已经有了两件工作宝物，新来的宝物也是工作宝物 那么不能戴上 会在网页返回no space to wear

在此处的测试，我设置了一个随机穿戴的测试以及对于特殊情况（如上2所描述的测试）

3)如果盒中没有宝物是否正确返回"Your treasure does not exist!"

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201017134357657.png" alt="image-20201017134357657" style="zoom:25%;" />

由于随机性，会出现在宝物盒中有两个相同的宝物的情况，允许这种情况存在

6.对于unwear的测试

与对wear的测试类似，当已佩戴饰品盒中没有宝物时，检查是否正确返回没有穿戴宝物。当以佩戴饰品中有宝物时，检查宝物能否不再佩戴并放回宝物盒。

7.对于find_onmarket的测试

考虑两种情况 1）查询的宝物被用户挂牌，检测是否正确返回This treasure is hanging on the market

2）查询的宝物未被用户挂牌，检测是否正确返回This treasure is not hanging on the market

8.对于un_onmarket的测试

考虑两种情况 1）查询的宝物被用户挂牌，检测是否正确返回treasure is deleted from the market

2）查询的宝物未被用户挂牌，检测是否正确返回treasure is not on market

9.对于add_onmarket的测试

1）测试对于不在宝物箱中的宝物，在尝试挂牌的时候能否正确返回treasure is not in your case

2)测试对于在宝物箱中的宝物,能否成功挂牌，并正确返回treasure is added to the market

10.对于unsale的测试

1）测试对于未挂牌的商品，尝试撤回是否正确返回Cannot unsale treasure you've never sold!

2）测试对于已挂牌的商品，能否成功撤回，测试宝物盒中的宝物数量是否正确

11.对于buy的测试

1）测试如果购买自己的商品，能否正确返回You can not buy what you've sold!

2）测试如果顾客的钱小于商品的价格，能否正确返回You donnot have enough money!

3）测试顾客拥有足够钱的情况下，能否购买成果，顾客和卖家的金币数以及宝物盒中的商品是否正确

### 五、最终结果和前端效果展示

测试最终结果:

func.py中包含所有实现具体功能的函数

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201022105059065.png" alt="image-20201022105059065" style="zoom:33%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201024102753507.png" alt="image-20201024102753507" style="zoom:33%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201023201613415.png" alt="image-20201023201613415" style="zoom:33%;" />

前端 登陆页面

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201023203309906.png" alt="image-20201023203309906" style="zoom: 20%;" />

登陆后的目录页

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025092753496.png" alt="image-20201025092753496" style="zoom: 33%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025092717965.png" alt="image-20201025092717965" style="zoom: 39%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025120520832.png" alt="image-20201025120520832" style="zoom: 25%;" />

