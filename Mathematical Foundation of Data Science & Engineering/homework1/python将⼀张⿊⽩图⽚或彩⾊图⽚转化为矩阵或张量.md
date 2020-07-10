```python
import matplotlib.pyplot as plt # plt 用于显示图片
import matplotlib.image as mpimg # mpimg 用于读取图片
import numpy as np
from PIL import Image
 
pil_im = Image.open('girl.jpg')
pil_im.show()
```


```python
pil_im = Image.open('girl.jpg').convert('L')
pil_im.show()
```


    ---------------------------------------------------------------------------

    NameError                                 Traceback (most recent call last)

    <ipython-input-48-33c5551b2caf> in <module>
          1 pil_im = Image.open('girl.jpg').convert('L')
          2 pil_im.show()
    ----> 3 imObj.save(pil_im, quality=95)
    

    NameError: name 'imObj' is not defined



```python
data= np.array(pil_im) 
#print(data)
matrix= np.mat(data)  # 数组转矩阵
print(type(matrix))
print(array_to_matrix, end='\n\n')
matrix_new=np.flip(matrix,1)#水平翻转
print(matrix_new)
image = Image.fromarray(matrix_new)
image.show()
```

    <class 'numpy.matrix'>
    [[169 169 169 ... 170 170 170]
     [169 169 169 ... 170 170 170]
     [170 170 170 ... 170 170 170]
     ...
     [188 188 188 ... 181 181 181]
     [188 188 188 ... 181 181 181]
     [188 188 188 ... 181 181 181]]
    
    [[170 170 170 ... 169 169 169]
     [170 170 170 ... 169 169 169]
     [170 170 170 ... 170 170 170]
     ...
     [181 181 181 ... 188 188 188]
     [181 181 181 ... 188 188 188]
     [181 181 181 ... 188 188 188]]



```python
plt.imshow(pil_im)
```




    <matplotlib.image.AxesImage at 0x104b5c9d0>




![png](output_3_1.png)



```python

```
