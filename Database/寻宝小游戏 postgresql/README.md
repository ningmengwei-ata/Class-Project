### 一、数据库设计

##### ER图

初始ER图

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201124183601515.png" alt="image-20201124183601515" style="zoom:33%;" />

在对题意的进一步分析中发现，用户和宝物的关系还包括用户将宝物存放在自己的盒子中，而且购买宝物和挂牌宝物的行为不只是用户和宝物之间的行为，实际应用中也与market实体类发生联系，因为需要进行挂牌和取消挂牌的操作，且市场交易在本次实验设计的应用中较频繁发生，故单独存挂牌的宝物，作为一个实体类。

完善ER图设计如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201206081212844.png" alt="image-20201206081212844" style="zoom:50%;" />

##### 关系模式

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201206081238016.png" alt="image-20201206081238016" style="zoom:43%;" />

这里的cases, on_market以及wear包含num属性值，其值有tid和uid共同决定。存num的好处有：

如果cases中有多个同种的宝物，可以很方便的记录数量。此外，在sql语句中count(*)的运行时间一般要比func(sum(num))慢，这样做可以对性能有一定的提升。

##### 关系模式的优化

**逻辑设计** 

范式化考量

1.考察是否满足二范式，即是否已经保证非码属性必须完全依赖于候选码，消除非主属性对主码的部分函数依赖。可以看到，username,money,ability,luck完全依赖于uid。name,luck,work_ab完全依赖于tid,price完全依赖于(uid,tid)。如上关系模式已满足该条件。

2.考察是否满足三范式，即是否保证任何非主属性不依赖于其它非主属性，是否在二范式上消除了传递依赖。传递依赖的定义为：设X,Y,Z是关系R中互不相同的属性集合，存在X→Y(Y !→X),Y→Z，则称Z传递函数依赖于X。如user中，uid决定ability,ability不能决定uid,但ability不能决定luck,故uid和luck之间不存在传递依赖。

3.考虑是否满足BC范式,即考察是否存在部分主键依赖于非主键部分的情况。如上设计的关系模式不存在非主键可决定主键的情况，故满足BC范式。

**物理设计**

1.代理键的使用

采用ID做主键，更具有规范性和严谨性

主键自动递增

```
class User(Base):
    __tablename__ = 'users'
    uid = Column(serial, primary_key=True, autoincrement=True)
```

2.为方便应用进行的冗余

由于包括回收宝物，查找宝物，购买宝物，挂牌宝物以及佩戴宝物的情形中需要频繁访问到宝物的名字和用户的名字，此外考虑应用场景中修改用户名/宝物名发生频率很低，加入冗余信息的代价基本没有，所以将宝物名和用户名冗余地存储在case中，更改case表格形式如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201125164011756.png" alt="image-20201125164011756" style="zoom:33%;" />

通过程序编写，我们发现cases表通常与on_market表以及wear表同时被访问，因为on_market和wear表中都只有uid和tid,没有用户名和商品名，函数访问时需要到user中查找uid对应的uname,到treasure中查找tid对应的tname一次，而在on_market以及wear中都添加uname和tname带来的冗余过多，插入修改等代价较高。这种情况下单独在case中增加unmae和tname的冗余时没有价值的，并没有提高查询效率和性能，所以我们删除在case表中添加的uname和tname冗余。最后的case表格形式如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201130104658872.png" alt="image-20201130104658872" style="zoom:33%;" />

此外我们考虑到用户访问市场较频繁。按照正常逻辑用户根据商品卖家、商品名和价格来决定购买的宝物而不是根据商家的ID以及宝物的ID，用户不一定能很清晰地记得宝物和宝物ID的对应关系，商家和商家名的对应关系。宝物挂牌，取消挂牌，购买宝物等都需要知道宝物的商品名。故添加在market表中添加冗余的商品名，并在其上加索引。此外uname和tname不能设有unique唯一约束，那样会影响一个用户向market中插入多个宝物或者一个宝物有多个用户买的情况的进行。最后的market表格形式如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201202212001177.png" alt="image-20201202212001177" style="zoom:33%;" />

##### 索引

由于为了方便用户的访问，考虑到用户不太能记得所有用户的id以及宝物的id,flask接口的url采用的是用户名和宝物名的形式。所以，对users表，在uname上添加索引。且这里的uname设置唯一约束。对treasure表，在tname上添加索引，且这里tname设置唯一约束。

![image-20201206083601615](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201206083601615.png)

### 二、相关函数的实现

**tomorrow**函数:起线程实现每一天的流逝

本次实验实现自动工作寻宝

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

![](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205143324215.png)

一天后

![image-20201205143553383](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205143553383.png)

两天后

![image-20201205143701792](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205143701792.png)



**login**函数：**需要访问数据库users表0或1次，若为新的用户名，则需插入到users表中**

判断是否为新用户，若是则像users表中插入一条新的记录，最后返回是否是新用户以及用户信息

该函数的url接口为/\<string:name\>

```python
IF_NEW = False
    if session.query(USER).filter(USER.c.uname==name).first()== None:   
      conn.execute(
        USER.insert(None).values(uname=name, money=0,luck=0,work=0))
      IF_NEW = True
```

**show**函数:不需要参数，**访问users表一次，cases表一次，on_market表一次，wear表一次**

显示用户所有信息的函数

show中会出现explore发现case中宝物看似没变，其实是在explore是寻到了已经在宝物盒中存在的宝物种类。宝物数量已经增加，由于宝物的种类没变而导致的。

该函数的url接口为/\<string:name\>/show

```python
return jsonify({'id':show[0],'name':show[1], "usr info":show,"case":cases,"on_market":on_markets,"wear_on":wear_ons})
```

**work_auto**函数: 不需要参数，**访问数据库2次，一次查询，一次更新**

**一次性从数据库中读取所有需要的数据，然后再分别取其中的不同属性给变量赋值，而不是用GetU函数每次只读一个属性，从而有效减少访问数据库的次数**

work_auto函数1)首先查看如果新用户当前工作能力为0，则将其设置为5（这样做的目的是因为，每次工作我们会给用户的money加上他当前的work对应的值，如果work为0就无法体现该用户已经工作过还是没有工作）

2)给用户的money加上当前的work

3)更新users中的money和work

该函数的url接口为/\<string:name\>/work

```python
work = GetU(name, 'work')
if work==0:
   work=5
money = GetU(name,'money')+work
USER.update_many({"_id":name},{"$set":{"money":money,"work":work,"FLAG_work":True}})
```

**explore_auto**函数: **访问users表两次，一次查询，一次更新。访问case表两次，一次查询，一次更新。总共访问数据库2次。**如果寻宝后存储箱满了，则需要回收。

该函数的url接口为/\<string:name\>/explore

explore函数1）如果当前luck值为0，由于不存在宝物的price为0，为用户分配宝物盒中price最低的宝物

如果当前luck不为0，先确定可以随机寻的宝物价值在(0.8\*luck,1.2\*luck）中

如果该价值区间中没有宝物则扩大范围随机寻的宝物价值在(0.6\*luck,1.4\*luck)中

以此类推。在确定好的有宝物的最小范围中随机生成一个宝物，加入当前用户的宝物盒

4）如果寻宝后存储箱满了，则需要回收。这里的存储箱满是只宝物数量达到5个，而不是看宝物的种类。

5）最后更新用户的case，在更新case中由于我们给宝物设置了数量，如果遇到已经在case中有的宝物种类，则将该数量+1，否则我们插入一条新的宝物的数据到cases中

```python

  luck = GetU(name, 'luck')
  x=session.query(TREASURE.c.tid).filter(and_(TREASURE.c.price >0.8*luck,TREASURE.c.price<1.2*luck)).all()
  area=[]
  if len(x)!=0:
    for i in range(len(x)):
      area.append(x[i][0])
  # print(area)
  #所有treasure
  #area.extend(list(TRSEASURE.find({})))
  if len(area)==0 and luck==0:
    tr_set=['13','15','16','17']
    u=random.randint(0,3)
    new_tr_name=tr_set[u]
  elif len(area)==0 and luck!=0:
  x=session.query(TREASURE.c.tid).filter(and_(TREASURE.c.price >0.6*luck,TREASURE.c.price<1.6*luck)).all()
    if len(x)!=0:
      for i in range(len(x)):
        area.append(x[i][0])
```

```python
 i = random.randint(0,len(area)-1)
 new_tr_name=area[i]
 if len(session.query(CASE).filter(and_(CASE.c.tid==new_tr_name , CASE.c.uid==wear_id)).all())!=0:
    x=conn.execute(select([CASE.c.num]).where(CASE.c.tid==new_tr_name and CASE.c.uid==wear_id)).fetchone()
    x=x[0]
    conn.execute(CASE.update(None).where(CASE.c.tid==new_tr_name and CASE.c.uid==wear_id).values(num=x+1))
  else:
    conn.execute(CASE.insert(None).values(tid=new_tr_name,uid=wear_id,num=1))
```

**recycle**函数：**函数用于在藏宝箱中宝物数量>5时系统回收价值最低的宝物**，在测试中不需要任何参数。

**为了减少访问数据库的次数，另写回收函数需要用户将当前的case传入，最后返回新的case。该函数执行过程中不需要另外访问数据库，因为调用它的函数已经访问数据库并初始化类，之后也会再次更新数据库。**

因为宝物的价值的大小与宝物的工作属性和幸运值正相关，所以直接回收宝物价值最低的宝物即可。如果宝物盒中的宝物数>5,则用for循环找出价值最低的宝物，从宝物箱中移出并更新usr数据库用户的宝物盒信息

该函数的url接口为/\<string:name\>/recycle

如果用户的宝物盒可以装下现有的宝物，则会返回如图所示的结果

![image-20201205152619370](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205152619370.png)

**wear**函数: 需要访问users,cases,wear表两次，一次查找，一次更新

通过对宝物的luck取bool值得到其tag,即是否为luck属性宝物。对宝物的work属性同理。对于已戴上宝物集合中的宝物，计算其中luck属性和work属性的宝物分别的数量.

1)如果对应属性的宝物已经装满，则返回没有地方装新的宝物。

2)在on_market 中检索该物品是否已经挂牌，如果该物品已经挂牌，则必须留在藏宝箱中，不能佩戴。

否则，戴上所选择的宝物，并从宝物盒中移除该宝物。用户的幸运值加上已戴上的宝物的幸运值。用户的工作能力加上佩戴的宝物的工作属性值。更新用户数据库中的luck、work值。更新cases表和wear表。如果遍历case中都没有想找的宝物则返回宝物不在宝物盒中。

另补充：对于cases表的更新，我们分两种情况考虑，如果这个宝物在cases中不只一个，则将宝物的num-1，如果这个宝物在cases中只有一个，则在cases表中删除该用户该宝物对应的相关记录。

该函数的url接口为/\<string:name\>/wear/\<string:tr_name\>

```python
      if (TAG and luck_num) or ((not TAG) and (working_num ==2)):
        return " No space to wear on!"
      wear_ons.append(i)
      cases.remove(i)
      luck =GetTr(i,'luck') + GetU(wear_id, 'luck')
      work = GetTr(i,'work_ab') + GetU(wear_id, 'work')
      conn.execute(USER.update(None).where(USER.c.uid == wear_id).values(luck=luck,work=work))
```

尝试戴上在宝物盒中的宝物t7，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205153506240.png" alt="image-20201205153506240" style="zoom:20%;" />

![](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205153442234.png)

尝试戴上不在盒子中的宝物，会返回

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084632488.png" alt="image-20201025084632488" style="zoom: 10%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025084225215.png" alt="image-20201025084225215" style="zoom:33%;" />

**unwear**函数:

与wear函数的实现类似。需要访问users,cases,wear表2次，一次查找，一次更新。

1）查看想要脱下的物品是否已经佩戴，如果没有佩戴则无法摘下。

2）如果宝物在已佩戴宝物集合中，取上所选择的宝物，并向宝物盒加入该宝物。用户的幸运值减去取下的宝物的幸运值。用户的工作能力减去取下的宝物的工作属性值。更新用户数据库中的luck、work值。更新cases表和wear表。

更新cases表分两种情况，如果宝物的种类已经在cases表中，则将其num加一

如果宝物种类不在cases表中，则向cases表插入一条新的数据记录当前宝物的tid数量以及用户的uid相关信息。

调用recycle函数，如果宝物盒中宝物数超过5，则需要回收。

该函数的url接口为/\<string:name\>/unwear/\<string:tr_name\>

随机测试未佩戴的宝物，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205154716810.png" alt="image-20201205154716810" style="zoom:20%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205154750654.png" alt="image-20201205154750654" style="zoom:33%;" />

测试取下已佩戴的宝物，结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205154638682.png" alt="image-20201205154638682" style="zoom:20%;" />

![image-20201205154652584](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205154652584.png)

**add_onmarket**函数:

**add_onmarket用于挂牌物品，需要两个参数，挂牌的物品名称和给出的价格**

需要访问users表2次，一次查询一次更新。访问treasure表一次，更新宝物的价格。访问market表一次，插入宝物的名称、价格、卖家信息。访问cases两次，一次查询出当前的case，一次更新。访问on_market两次，一次查询一次更新。

如果想挂牌的宝物在宝物箱中，在挂牌宝物的集合中加入宝物的名字，根据用户的输入更改自主确定宝物售卖的价格，在市场中加入宝物的卖家，名字和价格信息，返回宝物已挂牌，否则返回宝物不在存储箱中

如果查询发现同一用户挂牌同一宝物，返回You have successfully update the price of the treasure you add onmarket

如果发现同一用户以同一价格再次出售某宝物，返回You have already add this treasure to the market!There is no need to readd it again!

```python
 k=session.query(MARKET).filter(MARKET.c.uid == wear_id , MARKET.c.tid==treasure_id , MARKET.c.price==price).all()
    if len(k)!=0:
      return "You have already add this treasure to the market!There is no need to readd it again!"
    if len(session.query(MARKET).filter(MARKET.c.uid == wear_id , MARKET.c.tid==treasure_id ).all())!=0:
      conn.execute(TREASURE.update(None).where(TREASURE.c.tid == treasure_id).values(price=price))
      return 'You have successfully update the price of the treasure you add onmarket'
    conn.execute(ONMARKET.insert(None).values(tid=treasure_id,uid=wear_id,num=1))
    new_price=price
    conn.execute(TREASURE.update(None).where(TREASURE.c.tid == treasure_id).values(price=new_price))
   conn.execute(MARKET.insert(None).values(tid=treasure_id,uid=wear_id,price=new_price,tname=GetTr(treasure_id,'tname'),uname=GetU(wear_id,'uname')))
    return 'treasure is added to the market'
  else:
    return jsonify({"result": 'treasure is not in your case', "ok": 0})
```

该函数的url接口为/\<string:name\>/add_onmarket/\<string:tr_name\>/\<int:price\>

由于宝物盒中有多个congratulation宝物。第一次尝试将宝物盒中的t7挂牌，结果如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205222040647.png" alt="image-20201205222040647" style="zoom:20%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205215315367.png" alt="image-20201205215315367" style="zoom:29%;" />

尝试将不在宝物盒中的宝物挂牌，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205215411729.png" alt="image-20201205215411729" style="zoom:20%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205215352264.png" alt="image-20201205215352264" style="zoom:33%;" />

尝试再添加一个congratulation,但是修改对应的标价，返回结果如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205222000494.png" alt="image-20201205222000494" style="zoom:20%;" />

![image-20201205221722100](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205221722100.png)

尝试不修改定价，再加一次congratulation，返回结果如下：

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205222022380.png" alt="image-20201205222022380" style="zoom:20%;" />

![image-20201205221657255](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205221657255.png)

**un_onmarket**函数:

**un_onmarket撤回挂牌物品，需要一个参数，挂牌的物品名称，需要访问数据库users表2次，一次查询，一次更新，market表1次，更新操作，on_market表2次，一次查询一次更新，cases表2次，一次查询一次更新**

与add_onmarket函数类似。

1.查找用户准备撤回的物品是否在藏宝箱中，若不存在则是非法操作。

2.查找on_market是否有要撤回的宝物。若没有找到，则反馈给用户没有挂牌该物品。

3.若查找到，在on_market中删除要撤回的宝物

4.从market中删除撤回的宝物的相关信息

5.更新卖家的藏宝箱和saler信息

如果想收回的宝物在已挂牌宝物集合中，在挂牌宝物的集合中去除宝物的名字，调用unsale函数，更新用户的on_market集合，返回宝物已收回，否则返回宝物未挂牌

该函数的url接口为/\<string:name\>/un_onmarket/\<string:tr_name\>

删除之前已经添加到market的宝物congratulation

![image-20201205224539451](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205224539451.png)

尝试删除用户peggy未添加到market中的宝物rabbit

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205225407012.png" alt="image-20201205225407012" style="zoom:25%;" />

![image-20201205225525331](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205225525331.png)

**unsale**函数：

该函数的编写，为方便对un_onmarket和unsale的分别单独测试之用，其具体功能已在un_onmarket中描述

如果在market中找到需要unsale的记录，在market中删除该记录。否则，显示不能撤回你未挂牌的宝物

该函数的url接口为/\<string:name\>/unsale/\<string:tr_name\>

**get_market**函数:

访问市场出售信息，不需要参数，访问market文档集一次

直接返回market的所有信息，包括卖家、宝物名称和价格

```python
market = conn.execute(select([MARKET]))
for treasure in market:
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

需要访问数据库users表2次，2次查询，分别查询买家和卖家的相关信息，2次更新（买家和卖家），访问on_market表，对卖家的on_market信息一次查询一次更新。访问case表，对于卖家和买家都要进行一次查询一次更新。market表1次，删除操作，删除已售商品

1）用户不可以买自己出售的商品。

2）如果顾客的钱数小于商品的价格，返回“对不起，你没有足够的钱”

3）否则，删除market中的对应记录，将宝物加到顾客的宝物盒中，顾客的现有钱数为原有钱数减去宝物的价格。更新顾客的储物箱和拥有金币数的信息。将宝物从卖家的宝物盒和挂牌宝物集合中删除，卖家的现有钱数等于原有钱数加上卖出的宝物的价格。

采用不指定特定的卖家，系统自动挑选一位拥有该商品的卖家。

该函数的url接口为/\<string:name\>/buy/\<string:tr_name\>

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205232026888.png" alt="image-20201205232026888" style="zoom:25%;" />

顾客peggy购物前的情形

![image-20201205232120680](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205232120680.png)

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205232758714.png" alt="image-20201205232758714" style="zoom:25%;" />

![](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205232141312.png)

购买后卖家sel的情况

![image-20201205232155854](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205232155854.png)

购买后买家peggy的情况

![image-20201205233322514](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205233322514.png)

rabbit商品价格为5元，我们可以看到顾客peggy的money-5，卖家sel的money+5,顾客peggy的case中多出了买来的rabbit,卖家的case中删除了rabbit,on_market中的rabbit也被成功删除

当用户钱不够时，返回结果如下

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025101340965.png" alt="image-20201025101340965" style="zoom:25%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201025091834930.png" alt="image-20201025091834930" style="zoom: 33%;" />

辅助函数：

**GetU**函数

通过用户的用户名主键找到对应属性值

**GetTr**函数

与GetU函数类似，通过宝物的宝物名主键找到对应的属性值



### 三、debug主要问题记录

1.无法根据创建的primary_key来建表以及表名命名的问题

![image-20201124212534239](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201124212534239.png)

无法通过primary_key来建表，经仔细检查，发现问题在于定义中的格式问题，修改定义为如下格式即可解决问题

```
class Market(Base):
    __tablename__ = 'market'
    uid = Column(Integer, ForeignKey('user.uid'),primary_key=True)
```

此外应注意用户类的名字不能命名成user，这样会和postgres里默认设置发生冲突

会出现如下情况

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201124224639622.png" alt="image-20201124224639622" style="zoom:30%;" />

将用户表命名为users即可

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201124224618662.png" alt="image-20201124224618662" style="zoom:30%;" />

​			切忌与数据库本身带有特殊含义的字符同名如这里的case

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201125163140301.png" alt="image-20201125163140301" style="zoom:30%;" />

2.No value for argument 'dml' in method call

该错误由dml未定义引起

将原先的

```python
conn.execute(
        USER.insert().values(uname=name, money=0,luck=0,work=0))
```

改成

```python
conn.execute(
        USER.insert(None).values(uname=name, money=0,luck=0,work=0))
```

3.pytest的programming error

![image-20201201234257856](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201201234257856.png)

经过分析，发现是由于对于treasure的引用等式两边分别用了id和name，造成了programming error,将name的引用改成id保持统一即可。

4.pytest 中的key value violate

![image-20201202134445093](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201202134445093.png)

通过在juypter notebook 中调试打印相关变量的值发现，这里我们想测试所写的buy函数支不支持如果碰到了一个买家买自己出售的宝物的情况，能不能正确的返回您不能购买自己挂牌的宝物。而在判断条件中我错误的设置判断商品的名字是否在挂牌的宝物的ID集合中。将商品的名字改成商品的ID即可。

修改为如下形式：

```
if tr_mkt!=None:
    saler_x=tr_mkt['uname']
    if saler_x==name and treasure_id in on_markets:
        print(tr_name)
        print(saler_x)
```

5.nonetype问题

问题描述：TypeError: 'NoneType' object is not subscriptable

![image-20201205224839972](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205224839972.png)

这是由于treasure中没有所写的宝物名，操作失误导致的。

### 四、测试与优化

1.对于测试文件的优化 从coverage78%到98%

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201202175247013.png" alt="image-20201202175247013" style="zoom:25%;" />

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

2.对于recycle函数的测试

首先对recycle函数 保证通过

```python
@bp.route("/<string:name>/recycle", methods=['GET'])
```

设置测试样例的case中的宝物数为5和6，分别测试在不需要回收宝物时，宝物有无被错误回收以及当case中宝物数量超过限定值时是否宝物已被成功回收

![image-20201202202624745](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201202202624745.png)

3.对wear函数的测试

测试1)盒中有宝物且wear_on中宝物没有满，可以穿戴宝物的情况

2)如果如果已经有了一件幸运宝物，新来的宝物也是幸运宝物 那么不能戴上 会在网页返回no space to wear以及如果已经有了两件工作宝物，新来的宝物也是工作宝物，那么不能戴上，会在网页返回no space to wear

在此处的测试，我设置了一个随机穿戴的测试以及对于特殊情况（如上2所描述的测试）

3)如果盒中没有宝物或者没有想要穿戴的宝物是否正确返回"Your treasure does not exist!"

4)尝试穿已经挂在市场的宝物 能否正确返回"This treasure is on market,you can't wear it"

![image-20201202212355374](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201202212355374.png)

由于随机性，会出现在宝物盒中有两个相同的宝物的情况，允许这种情况存在，通过num标记宝物的个数

4.对于unwear的测试

与对wear的测试类似，当已佩戴饰品盒中没有宝物时，检查是否正确返回没有穿戴宝物。当以佩戴饰品中有宝物时，检查宝物能否不再佩戴并放回宝物盒。

5.对于un_onmarket的测试

考虑两种情况 1）查询的宝物被用户挂牌，检测是否正确返回treasure is deleted from the market

2）查询的宝物未被用户挂牌，检测是否正确返回treasure is not on market

6.对于add_onmarket的测试

1）测试对于不在宝物箱中的宝物，在尝试挂牌的时候能否正确返回treasure is not in your case

2)测试对于在宝物箱中的宝物,能否成功挂牌，并正确返回treasure is added to the market

7.对于unsale的测试

1）测试对于未挂牌的商品，尝试撤回是否正确返回Cannot unsale treasure you've never sold!

2）测试对于已挂牌的商品，能否成功撤回，测试宝物盒中的宝物数量是否正确

8.对于buy的测试

1）测试如果购买自己的商品，能否正确返回You can not buy what you've sold!

2）测试如果顾客的钱小于商品的价格，能否正确返回You donnot have enough money!

3）测试顾客拥有足够钱的情况下，能否购买成果，顾客和卖家的金币数以及宝物盒中的商品是否正确

4）测试顾客拥有足够钱的情况下，买一个自己宝物盒同种的宝物，该宝物数量能否正确+1

补充：关于自动寻宝和自动工作的测试在treasure-hunt-web中运行app.py在网页端测试即可

### 五、最终结果和前端效果展示

测试最终结果:所有测试全部通过，覆盖率达98%

func.py中包含所有实现具体功能的函数

![image-20201203162249096](/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201203162249096.png)

前端 登陆页面

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201023203309906.png" alt="image-20201023203309906" style="zoom: 20%;" />

登陆后的目录页

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205225121360.png" alt="image-20201205225121360" style="zoom:30%;" />

<img src="/Users/wangwenqing/Library/Application Support/typora-user-images/image-20201205230022467.png" alt="image-20201205230022467" style="zoom:20%;" />

