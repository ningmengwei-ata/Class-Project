#%%
from PIL import Image
import matplotlib.pyplot as plt 
import numpy as np

# %%

img=np.array(Image.open('test.jpg'))

theta=30/180*np.pi 

cos_theta=np.cos(theta)
sin_theta=np.sin(theta)

center_i=len(img)/2
center_j=len(img[0])/2

#%%

imgr=np.zeros_like(img)
for i in range(len(img)):
    for j in range(len(img[0])):
        yi=int(cos_theta*(i-center_i)-sin_theta*(j-center_j)+center_i)
        yj=int(sin_theta*(i-center_i)+cos_theta*(j-center_j)+center_j)
        if yi<0 or yj<0 or yi>=len(img) or yj>=len(img[0]):
            continue
        for k in range(3):
            imgr[yi][yj][k]=img[i][j][k]
            

fig1=plt.figure()
ax=plt.subplot(111)
ax.imshow(imgr)
ax.axis('off')
ax.xaxis.set_tick_params([]) 
plt.show()




# %%



imgR=np.zeros_like(img)
for i in range(len(img)):
    for j in range(len(img[0])):
        xi=int(cos_theta*(i-center_i)+sin_theta*(j-center_j)+center_i)
        xj=int(-sin_theta*(i-center_i)+cos_theta*(j-center_j)+center_j)
        if xi<0 or xj<0 or xi>=len(img) or xj>=len(img[0]):
            continue
        for k in range(3):
            imgR[i][j][k]=img[xi][xj][k]
            

fig2=plt.figure()
ax=plt.subplot(111)
ax.imshow(imgR)
ax.axis('off')
ax.xaxis.set_tick_params([]) 
plt.show()



# %%

fig1.savefig('fig1.jpg')
fig2.savefig('fig2.jpg')

# %%
