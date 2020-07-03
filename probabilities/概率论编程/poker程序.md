poker程序

```python
import time
from tqdm import tqdm  
import numpy as np
import random
from random import shuffle
def validate(a):
    count=1
    for j in range(0,len(a)-1):
        if a[j+1]==a[j]+1:
            count+=1
            if count==5:
                return 1
        else:
            count=1
    return 0
def judge(c):
    ht ,ch ,fq ,bt = [], [] ,[], []
    for j in range(0,18):
        if((c[j]//100)==5):
            continue
        if((c[j]//100)==1):
            ht.append(c[j])
        elif((c[j]//100)==2):
            ch.append(c[j])
        elif((c[j]//100)==3):
            bt.append(c[j])
        elif((c[j]//100)==4):
            fq.append(c[j])
    ht.sort()
    ch.sort()
    bt.sort()
    fq.sort()
    #print(fq)
    if validate(ht) or validate(ch) or validate(bt) or validate(fq):
        return 1
    else:
        return 0
if __name__=="__main__":
    card=np.array([102,103,104,105,106,107,108,109,110,111,112,113,114
                   ,202,203,204,205,206,207,208,209,210,211,212,213,214,
                  302,303,304,305,306,307,308,309,310,311,312,313,314,
                  402,403,404,405,406,407,408,409,410,411,412,413,414,500,501])
    #500 501代表大小王 其余按 '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K','A'的顺序
#print(card[18:36])
    success=0
    test_times=10000000
    for i in tqdm(range(test_times)): 
        shuffle(card)
        if judge(card[0:18])==1 or judge(card[18:36])==1 or judge(card[36:54])==1:
             success += 1         
    print(success)
    score=success/test_times
    print(score)
```

