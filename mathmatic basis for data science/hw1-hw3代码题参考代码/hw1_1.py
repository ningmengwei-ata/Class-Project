

import numpy as np 
import matplotlib.pyplot as plt 
from PIL import Image
# %matplotlib inline

img=np.array(Image.open("1.png"))

fig=plt.figure(dpi=150)
plt.axis("off")
plt.imshow(img)

img=img[:,::-1,:]

fig=plt.figure(dpi=150)
plt.axis("off")
plt.imshow(img)

