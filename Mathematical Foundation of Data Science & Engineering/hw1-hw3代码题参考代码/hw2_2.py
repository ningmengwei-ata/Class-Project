import cv2 #引入opencv模块
import numpy as np #引入numpy模块
img = cv2.imread('test.jpg') #载入图片
#显示原图片5秒
cv2.imshow('img', img)
cv2.waitKey(5000)
cv2.destroyAllWindows()
height, width, channel = img.shape #读取图片尺寸
new_height, new_width = 2*height, 2*width #新设图片尺寸
dst = np.zeros((new_height, new_width, 3), dtype=np.uint8) #新设图片
s_1,s_2 = height//2, width//2 #原图像旋转中心坐标
d_1,d_2 = height, width #旋转后图片的旋转中心坐标

delta = 30
cosd = (np.cos(delta / 180*np.pi))
sind = (np.sin(delta / 180*np.pi))
#前向映射
for x_1 in range(0, height):
    for x_2 in range(0, width):
        y_1 = int(cosd * (x_1 - s_1) + sind * (x_2 - s_2) + d_1)
        y_2 = int(-sind * (x_1 - s_1) + cosd * (x_2 - s_2) + d_2)
        dst[y_1, y_2] = img[x_1, x_2] #在对应位置填入像素
#显示旋转后的图片5秒
cv2.imshow('dst', dst)
cv2.waitKey(5000)
cv2.destroyAllWindows()
#重设图片
dst = np.zeros((new_height, new_width, 3), dtype=np.uint8)
#后向映射
for y_1 in range(0, new_height):
    for y_2 in range(0, new_width):
        x_1 = int(cosd * (y_1 - d_1) - sind * (y_2 - d_2)) + s_1
        x_2 = int(sind * (y_1 - d_1) + cosd * (y_2 - d_2)) + s_2
        if height > x_1 >= 0 and width > x_2 >= 0:
            dst[y_1, y_2] = img[x_1, x_2] #在对应位置填入像素
#显示旋转后的图片5秒
cv2.imshow('dst', dst)
cv2.waitKey(5000)
cv2.destroyAllWindows()
