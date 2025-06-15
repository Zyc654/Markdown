## 深度学习多层感知器

![image-20250612141724577](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612141724577.png)

![image-20250612142028229](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612142028229.png)

### MLP 多层感知器

![image-20250612142413615](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612142413615.png)

![image-20250612142524252](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612142524252.png)

![image-20250612142704879](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612142704879.png)

### MLP实现非线性二分类

![image-20250612143457028](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612143457028.png)

![image-20250612152429316](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612152429316.png)

### MLP用于多分类预测 

![image-20250612153652533](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612153652533.png)

![image-20250612161930579](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612161930579.png)

B -> A -> C -> E -> F -> D

### Keras 

![image-20250612162220524](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612162220524.png)

#### Keras or Tensorflow

![image-20250612162542473](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612162542473.png)

![image-20250612163134431](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612163134431.png)

> units 输出个数 ； input_dim 输入个数 ；sigmoid 是指逻辑回归  --- 中间层的初始化
>
> 第二维指定目标输出，此时由于隐藏（中间）层被指定了不需要再指定输入 3  个个数
>
> loss 是指损失函数，categorical_crossentropy 指逻辑回归二分类损失函数，optimizer 优化器指采用什么样的求解方法，sgd（随机梯度下降） / Adam（自适应估计）
>
> epochs 表示迭代次数，依次增大迭代次数来判断。
>
> https://keras-cn.readthedocs.io/en/lates/#30skeras
>
> pip install keras

### 实战准备

#### 非线性二分类

![image-20250612163758036](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612163758036.png)

- 如果神经元过少可能需要很长时间的训练且得不到好结果

![image-20250612164442293](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612164442293.png)

> Dense (输出个数，输入个数，激活函数模型) 搭配 add(Dense())
>
> Activation 激活函数
>
> Sequential() 顺序模型
>
> summary() 显示概括
>
> compile(优化器，损失函数)  配置核心参数
>
> predict_classes(test) 将概率转换为 0 / 1    --> numpy.ndarray 无法进行索引
>
> new = pd.Series([i[0] for i in predict])  转换为可以用于索引的 Series 类型

#### 实现图像多分类

![image-20250612164509145](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612164509145.png)

![image-20250612164624481](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612164624481.png)

![image-20250612164803196](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612164803196.png)

![image-20250612165007282](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612165007282.png)

> softmax 表示自动输出多分类的结果

### 实战环节

#### 实战一

> 基于 data.csv 数据，建立 mlp 模型，计算其在测试数据上的准确率，可视化模型预测结果
>
> 1. 进行数据分离：test_size = 0.33，random_state=10
> 2. 模型结构：一层隐藏层，有 20 个神经元

```py
#load the date
import pandas as pd
import numpy as np
data = pd.read_csv('data.csv')
data.head()
```

![image-20250612194916147](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612194916147.png)

```py
#define x & y
X = data.drop(['y'],axis=1)
y = data.loc[:,'y']
X.head()
```

![image-20250612194941077](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612194941077.png)

```py
# 可视化操作
%matplotlib inline
from matplotlib import pyplot as plt
fig1 = plt.figure(figsize=(5,5))
passed = plt.scatter(X.loc[:,'x1'][y==1],X.loc[:,'x2'][y==1])
failed = plt.scatter(X.loc[:,'x1'][y==0],X.loc[:,'x2'][y==0])
plt.legend((passed,failed),('passed',"failed"))
plt.xlabel('x1')
plt.ylabel('x2')
plt.title('raw data') # 原始数据
plt.show()
```

![image-20250612194957329](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612194957329.png)

```py
# 数据分离
from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.33,random_state=10)
print(X_train)
print(X_train.shape,X_test.shape,X.shape)
```

![image-20250612195010328](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612195010328.png)

```py
# set up the model
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense,Activation
mlp = Sequential()
mlp.add(Dense(units=20,input_dim=2,activation='sigmoid')) # sigmoid逻辑回归
mlp.add(Dense(units=1,activation='sigmoid'))
mlp.summary()
```

![image-20250612213908072](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612213908072.png)

```py
# compile the model
mlp.compile(optimizer='adam',loss='binary_crossentropy',metrics=['accuracy'])
```

```py
# train the model
mlp.fit(X_train,y_train,epochs=3000)
```

![image-20250612213932577](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612213932577.png)

```py
def predict_classes(model, x, threshold=0.5):
    # model.predict 返回形如 (N, 1) 的概率
    probs = model.predict(x).ravel()              # 展平成 (N,)
    return (probs >= threshold).astype(int)       # 大于阈值即预测为 1，否则为 0
# mlp.predict_classes 已被弃用，使用 阈值 替代 此时返回的就是数列

# make predicition and calculate the accuracy
y_train_predict = predict_classes(mlp,X_train) # 使用 classes 是为了输出 0 1分类 而不是概率
from sklearn.metrics import accuracy_score
accuracy_train = accuracy_score(y_train,y_train_predict)
print(accuracy_train)

# make predicition based on the test data
y_test_predict = predict_classes(mlp,X_test)
accuracy_test = accuracy_score(y_test,y_test_predict)
print(accuracy_test)
```

![image-20250612213959510](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612213959510.png)

```py
print(y_train_predict)
#y_train_predict_form = pd.Series(i[0] for i in y_train_predict) # [0] 表示初始值
y_train_predict_form = y_train_predict# 这里只是为了和 课程中变量一致 本身即是数组
print(y_train_predict_form.shape) 
```

![image-20250612214012601](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612214012601.png)

```py
# generate new data for plot
xx,yy = np.meshgrid(np.arange(0,1,0.01),np.arange(0,1,0.01))
print(xx,xx.shape)
x_range = np.c_[xx.ravel(),yy.ravel()] #将二维数组转换为数列
y_range_predict = predict_classes(mlp,x_range) # mlp模型预测
print(type(y_range_predict))
print(y_range_predict)
```

![image-20250612214029540](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612214029540.png)

```py
fig2 = plt.figure(figsize=(5,5))
passed_predict = plt.scatter(x_range[:,0][y_range_predict_form==1],x_range[:,1][y_range_predict_form==1])
failed_predict = plt.scatter(x_range[:,0][y_range_predict_form==0],x_range[:,1][y_range_predict_form==0])
passed = plt.scatter(X.loc[:,'x1'][y==1],X.loc[:,'x2'][y==1])
failed = plt.scatter(X.loc[:,'x1'][y==0],X.loc[:,'x2'][y==0])
plt.legend((passed,failed,passed_predict,failed_predict),('pass','failed','passed_predict','failed_predict'))
plt.xlabel('x1')
plt.ylabel('x2')
plt.title('raw data')
plt.show()
```

![image-20250612214105849](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612214105849.png)

> 好坏质检二分类 mlp 实战 summary：
>
> 1. 通过 mlp 模型，在不增加特征项的情况下，实现了非线性二分类任务
> 2. 掌握了 mlp 模型的建立、配置与训练方法，并实现了基于新数据的预测
> 3. 熟悉了 mlp 分类的预测数据格式，并实现格式转换
> 4. http://keras-cn.readthedocs.io/en/latest/#30skeras

#### 实战二 MLP实现图像多分类

 ![image-20250612214357921](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250612214357921.png)

> 由于下载的数据集大小是 784，首层，第一、二隐藏层是 392

```py
# load the minst data 
from keras.datasets import mnist
(X_train,y_train),(X_test,y_test) = mnist.load_data() # 官网中自带的数据包
print(type(X_train),X_train.shape)
```

```
<class 'numpy.ndarray'> (60000, 28, 28)
```

```py
#visualize the data
img1 = X_train[0]
%matplotlib inline
from matplotlib import pyplot as plt
fig1 = plt.figure(figsize=(3,3))
plt.imshow(img1) # 简而言之，就是把一个二维（或三维）数值“矩阵”当作一幅图像来可视化。具体来说：
plt.title(y_train[0]) # 第一幅图像
plt.show()
print(img1.shape,X_train.shape)
```

![image-20250613123006133](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613123006133.png)

##### 将输入数据转换为向量

```py
# fomat the input data
feature_size = img1.shape[0]*img1.shape[1]
X_train_format = X_train.reshape(X_train.shape[0],feature_size) # 样本数量 ， size
X_test_format = X_test.reshape(X_test.shape[0],feature_size) #reshape 方法允许你不复制数据的前提下，重新组织数组的维度。矩阵->向量
print(feature_size,X_train_format.shape) # 转换为 60000 个向量 将矩阵转换为向量
X_train_format[0]
```

![image-20250613145432559](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613145432559.png)

##### 格式化输入数据

```py
# normalize the input data  图片是 0 到 255 的
X_train_normal = X_train_format / 255
X_test_normal = X_test_format / 255
# print(X_train_format[0],'\n','\n','\n','\n') 
# print(X_train_normal[0])
X_train_normal.shape
```

![image-20250613145501187](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613145501187.png)

##### 格式化输出数据

```py
#format the output data (labels)
from keras.utils import to_categorical
y_train_format = to_categorical(y_train)
y_test_format = to_categorical(y_test) # 将数组转化为向量
"""
如果不显式指定 num_classes 参数，to_categorical 会取 y_train 中的最大值 M，然后把类别数设为 M+1；
也可以手动传入 to_categorical(y_train, num_classes=K)，强制输出宽度为 K 的一热向量。
"""
print(y_train.shape)  # 数组转换为向量
for i in range(10) :
    print(y_train[i])
for i in range(10) :
    print(y_train_format[i])
```

![image-20250613145514293](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613145514293.png)

##### 设置模型

```py
#set up the model
from keras.models import Sequential
from keras.layers import Dense,Activation
mlp = Sequential() # mlp 实例
mlp.add(Dense(units=392,activation='sigmoid',input_dim=feature_size))
mlp.add(Dense(units=392,activation='sigmoid')) #激活方式是sigmoid逻辑回归
mlp.add(Dense(units=10,activation='softmax')) # 输出的多分类
mlp.summary()
```

![image-20250613145618580](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613145618580.png)

##### 配置模型

```py
#comfigure the model 配置模型
mlp.compile(loss='categorical_crossentropy',optimizer='adam') #专门用于多分类的损失函数
#train the model
print(X_train_normal.shape,y_train_format.shape)
mlp.fit(X_train_normal,y_train_format,epochs=10) # 如果不指定batch_size（epochs后面指定）默认值是32，此时显示的数据范围是1875，分32个批次训练
```

![image-20250613145829360](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613145829360.png)

##### 预测模型

```py
# evaluate the model
def predict_classes(model, x, threshold=0.5):
    return np.argmax(model.predict(x),axis=1)
    # # model.predict 返回形如 (N, 1) 的概率
    # probs = model.predict(x).ravel()              # 展平成 (N,)
    # return (probs >= threshold).astype(int)       # 大于阈值即预测为 1，否则为 0
# mlp.predict_classes 已被弃用，使用 阈值 替代 此时返回的就是数列
y_train_predict = predict_classes(mlp,X_train_normal)
# y_train_predict = np.argmax(mlp.predict(X_train_normal),axis=1)
print(y_train_predict)
```

![image-20250613150110049](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613150110049.png)

##### 检验精确度

```py
from sklearn.metrics import accuracy_score
accuracy_train = accuracy_score(y_train,y_train_predict)
print(accuracy_train)
# print(y_train.shape,y_train_predict.shape)
```

![image-20250613150130599](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613150130599.png)

```py
y_test_predict = predict_classes(mlp,X_test_normal)
accuracy_test = accuracy_score(y_test,y_test_predict)
print(accuracy_test)
```

![image-20250613150145854](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613150145854.png)

##### 检验测试与原数据比对结果

```py
#基于图像数据的数字自动识别分类
img2 = X_test[10]
fig2 = plt.figure(figsize=(3,3))
plt.imshow(img2)
plt.title(y_test_predict[10])
plt.show()
```

![image-20250613150158381](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613150158381.png)

## 深度学习之卷积神经网络

### 卷积神经网络（一）

> 训练数据量非常庞大

![image-20250613151630128](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613151630128.png)

![image-20250613152754583](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613152754583.png)

> 减少训练参数数量：提取出图像中的关键信息（轮廓 ），在建立 mlp 模型进行训练

![image-20250613152959702](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613152959702.png)

#### 图像卷积计算 ！ ！ 

对**图像矩阵**与**滤波器**矩阵进行**对应相乘**在求和计算，转换得到新的矩阵。

作用：快速定位图像中某些边缘特质

英文：convolution   CNN            A与B的卷积通常表示为 A * B 或 convolution(A,B)

 ![image-20250613153436102](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613153436102.png)

![image-20250613162417766](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613162417766.png)

#### 竖向轮廓过滤器

> 包含竖向轮廓的区域非常亮（灰度值高）

![image-20250613162721099](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613162721099.png)

#### 横向轮廓过滤器

> 将图片与轮廓滤波器进行卷积运算，可快速定位固定轮廓特征的位置

![image-20250613163037587](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613163037587.png)

#### 卷积神经网络的核心

> 计算机根据样本图片，自动寻找合适的轮廓过滤器，对新图片进行轮廓匹配

![image-20250613163329716](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613163329716.png)

> RGB图像的卷积：对R\G\B三个通道**分别求卷积在相加**
>
> 过滤器数量不定，可以将过滤器得到的结果相加得到三位的结果   m 个过滤器  -> 4 * 4 * m

![image-20250613163435905](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613163435905.png)

#### 池化层实现维度缩减

> 池化：按照一个固定规则对图像矩阵进行处理，将其转换为更低维度的矩阵

##### 最大法池化（Max-pooling)

> 指定矩阵大小，从左到右从上到下的数据是当前矩阵中最大的数据，stride 表示的是窗口滑动步长（每次向右和向下移动的步长）
>
> **保留核心信息**的情况下，实现维度缩减（类似于PCA主成分分析）

![image-20250613164719116](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613164719116.png)

##### 平均法池化（Avg-pooling)

![image-20250613164833946](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613164833946.png)

把卷积、池化、mlp先后连接在一起，组成卷积神经网络

#### 激活函数 Relu

> Relu vs sigmoid
>
> Relu 激活函数求导来说比较方便，sigmoid 求导在 x 较大或较小的时候迭代比较麻烦
>
> 最后需要将池化矩阵转换为 数组  mlp 接受的是数组形式的参数

![image-20250613165254755](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613165254755.png)

#### 两大特点

1. 参数共享（parameter sharing）：同一个特征过滤器可用于整张图片
2. 稀疏连接（sparsity of connections）：生成的特征图片每个节点只与原图片中特定节点连接

> CNN 可以使得和附近的形成轮廓，mlp 是将所有的神经元都建立联系，CNN 节省了一些没必要的计算，很快提取出特征。

![image-20250613165554128](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613165554128.png)

### 卷积神经网络（二）

卷积运算导致的两个问题

1. 图像被压缩，造成信息丢失
2. 边缘信息使用少，容易被忽略

![image-20250613171003609](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613171003609.png)

#### 图像填充

> 添加的数据往往由过滤器的尺寸和滑动窗口（stride）确定   same 表示填充

![image-20250613203250893](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613203250893.png)

![image-20250613203420721](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613203420721.png)

#### CNN 模型

1. 参考经典的CNN结构搭建新模型          LeNet-5  AlexNet   VGG    -----> 卷积加池化
2. 使用经典的CNN模型结构对图像预处理，在建立MLP模型

> FC 表示全连接层

![image-20250613205401332](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613205401332.png)

##### LeNet-5

> 第一层 5 * 5 的过滤器，32 -> 28 使用六个过滤器，所以第一层之后尺寸是 28 * 28 * 6，第二层滑动窗口是 2，所以 （28-2）/2+1=14

![image-20250613204155681](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613204155681.png)

##### AlexNet

> 可以用于 RGB 图像，可以使用多分类，same 表示填充数据 卷积加池化+mlp（卷积尺寸和步长变化很多）
>
> 输入数据：227 * 227 * 3 RGB 图，3 个通道，训练参数：约60000000个
>
> 特点
>
> 1. 适用于识别较为复杂的彩色图，可识别1000中类别
> 2. 结构比LeNet更为复杂，使用 Relu 作为激活函数
>
> ------>    深度学习技术在计算机视觉应用中可以得到很不错的结果

![image-20250613204504007](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613204504007.png)

##### VGG-16

> 一定情况下参照了AlexNet，尽量多卷积操作，将重要的轮廓提取出来 
>
> flatten 表示**全连接层**，最后进行多分类的预测
>
> 输入图像：227*227*3 RGB 3个通道
>
> 训练参数：约 138000000个
>
> 特点：
>
> 1. 所有卷积层 filter 宽和高都是3，步长是 1，padding 都使用 same convolution（填充，保证卷积之后尺寸一致）
> 2. 所有的池化层 filter 宽和高都是2，步长都是 2
> 3. 相比 alaxnet，有更多的 filter 用于提取轮廓信息，具有更高精准性。

![image-20250613204953861](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613204953861.png)

> 结合经典模型 + 自己进行 mlp 全连接，实现猫狗分类

![image-20250613205527922](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613205527922.png)

### 实战准备

#### 实战一

![image-20250613205639175](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613205639175.png)

> relu 激活函数 f(x) = max(x,0)

![image-20250613205736844](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613205736844.png)

![image-20250613210030940](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613210030940.png)

![image-20250613210218617](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613210218617.png)

![image-20250613210742366](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613210742366.png)

#### 实战二

![image-20250613210829417](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613210829417.png)

> preprocess_input 将输入数据转换为 VGG16 所需要的数据

![image-20250613210940880](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613210940880.png)

![image-20250613211146708](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613211146708.png)

![image-20250613211208305](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613211208305.png)

![image-20250613211251448](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613211251448.png)

### 实战

#### 实战一

> 基于 dataset\training_Set 数据，根据提供的结构，建立 CNN 模型，识别图片中的猫/狗，计算预测准确率
>
> 1. 识别图片中的猫/狗，计算dataset\test_set测试数据预测准确率
> 2. 从网站下载猫/狗图片，对其进行预测

##### 加载数据

```py
# load the data 自己建立CNN模型的时候一般需要进行图像增强 1000 -> 10000 张 ImageDataGenerator keras中的函数
from tensorflow.keras.preprocessing.image import ImageDataGenerator
train_datagen = ImageDataGenerator(rescale=1./255) # 归一化，将所有像素转换为 0 - 255 
training_set = train_datagen.flow_from_directory('E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/dataset/training_set',
                                               target_size=(50,50),batch_size=32,class_mode='binary')
# batch_size 表示每次选 32 张图进行梯度下降，class_mode 表示二分类
#training_test 是由图片和标签组成的，使用 ImageDataGenerator 和 flow_from_directory 方法时，标签是根据目录结构自动生成的。flow_from_directory 方法能够根据目录结构自动生成标签，方便了数据的组织和读取。

output : Found 8000 images belonging to 2 classes.
```

##### 建立CNN模型

> Sequential 是 Keras 提供的一种 按层顺序堆叠神经网络模型结构，适用于从前到后逐层构建的线性网络，即：每一层只有一个输入和输出，并且下一层的输入就是上一层的输出，一种顺序网络，不含分支或多输入输出结构
>
> 不适用于有分支结构（ResNet，Inveption）有多个输入或多个输出的模型，需要在网路中共享权重或跳层连接

```py
# 建立 CNN 模型
from tensorflow.keras.models import Sequential
from keras.layers import Conv2D, MaxPool2D, Flatten, Dense
model = Sequential() #实例化模型
# 第一层卷积层
model.add(Conv2D(32, (3, 3), input_shape=(50, 50, 3), activation='relu'))
# Conv2D(32, (3, 3)) 表示使用 32 个 3x3 的卷积核
# input_shape=(50, 50, 3) 表示输入图像的尺寸为 50x50，3 个颜色通道
# activation='relu' 表示使用 ReLU 激活函数

# 第一层池化层
model.add(MaxPool2D(pool_size=(2, 2)))
# MaxPool2D(pool_size=(2, 2)) 表示使用 2x2 的最大池化窗口
# 第二层卷积层
model.add(Conv2D(32, (3, 3), activation='relu'))
# 第二层卷积层，无需指定 input_shape，因为它是第二层，输入来自上一层
# 第二层池化层
model.add(MaxPool2D(pool_size=(2, 2)))
# Flatten 层
model.add(Flatten())
# 将多维的特征图展平为一维向量，以便连接到全连接层
# 全连接层
model.add(Dense(units=128, activation='relu'))
# units=128 表示该层有 128 个神经元
# 输出层
model.add(Dense(units=1, activation='sigmoid'))
# units=1 表示输出一个值，activation='sigmoid' 适用于二分类问题
# 在二分类任务中，如果模型的输出层只有一个神经元，并且使用了 sigmoid 激活函数，输出值表示的是样本属于类别 1 的概率
```

![image-20250613212908866](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250613212908866.png)

##### 配置模型

```py
#comfigure the model
model.compile(optimizer='adam',loss='binary_crossentropy',metrics=['accuracy']) #优化器，loss损失函数，显示精确度
model.summary()
```

![image-20250614091553233](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614091553233.png)

##### 训练模型

```py
#train the model
model.fit( # 旧版本使用 fit_generator，新版本 keras建议直接使用fit搭配生成器使用
    training_set,
    epochs=25,
    steps_per_epoch=len(training_set)  # 每个 epoch 的步数
)
```

![image-20250614091625674](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614091625674.png)

##### 查看信息

```py
for images, labels in training_set:
    print(images.shape, labels.shape)
    print(images.dtype, labels.dtype)
    break
    
output:
  (32, 50, 50, 3) (32,)  # (32,) 表示此时是数组， 一维的，一行数据；(32,1) 表示此时是二维的，32行一列
float32 float32
```

> **`images.shape`** ：
>
> **32** ：表示批次大小（batch size），即每个批次有 32 张图像。
>
> **50, 50** ：表示每张图像的高度和宽度，这里是 50x50 像素。
>
> **3** ：表示图像的通道数，这里是 3，代表 RGB 三通道彩色图像。如果是灰度图像，则通道数为 1。
>
> **`labels.shape`** ：
>
> **32** ：表示批次大小，与 `images.shape` 中的第一个维度对应，每个图像对应一个标签。在这里，标签是一个一维数组，形状为 `(32,)`，表示每个样本有一个标签值。在二分类问题中，标签通常是 0 或 1。

##### 查看精确度

```py
#accuracy on training data
accuracy_train = model.evaluate(training_set) #这个方法将模型的预测结果与训练数据的真实标签进行比较，从而计算出模型在训练集上的准确率。
print(accuracy_train)

#accuracy on the test data
test_set = train_datagen.flow_from_directory('E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/dataset/test_set',
                                              target_size=(50,50),batch_size=32,class_mode='binary')
#这里主动进行了缩放，target_size=(50,50) 原始图片是 (280,300) 的
accuracy_test = model.evaluate(test_set)
print(accuracy_test) # 一般来说 CNN 需要大量的数据进行训练，比如缩放。
```

![image-20250614091803603](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614091803603.png)

##### 定义 predict_class 函数

```py
import numpy as np

def predict_class(model, data, batch_size=None):
    """
    Predict class labels for binary classification using the model.
    Handles both single and multiple input samples.
    
    Args:
        model: The trained model used for prediction.
        data: The input data for prediction. Can be a single sample or multiple samples.
        batch_size: Batch size for prediction. If None, use the model's default batch size.
    
    Returns:
        predicted_class: Predicted class labels (0 or 1). 
        Returns a scalar for single input, array for multiple inputs.
    """
    # 确保输入是二维数组（即使只有一个样本）
    if len(data.shape) == 1:
        data = np.expand_dims(data, axis=0)
    
    predictions = model.predict(data, batch_size=batch_size)
    
    # 二分类处理：概率 > 0.5 则为类别1，否则为类别0
    predicted_class = (predictions > 0.5).astype(int).flatten()
    
    # 如果原始输入是单个样本，返回标量值；否则返回数组
    if len(predicted_class) == 1:
        return predicted_class[0]
    else:
        return predicted_class
```

##### 查看种类划分

```py
training_set.class_indices

output:{'cats': 0, 'dogs': 1}
```

##### 预测下载的图像

```py
#load single image
from keras.preprocessing.image import load_img,img_to_array
pic_dog = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/dog.jpg"
pic_dog = load_img(pic_dog,target_size=(50,50))  # 返回的是 RGB 图像，每次load的时候都进行了缩放
print(pic_dog.mode)
pic_dog = img_to_array(pic_dog)
print(pic_dog.shape)
pic_dog = pic_dog / 255
pic_dog = pic_dog.reshape(1,50,50,3) # 第几张图片，3个维度，RGB
result = predict_class(model,pic_dog)
print(result)

output:RGB
(50, 50, 3)
1/1 ━━━━━━━━━━━━━━━━━━━━ 0s 45ms/step
1
```

```py
#load single image
# from keras.preprocessing.image import load_img,img_to_array
pic_cat = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/cat1.jpg"
pic_cat = load_img(pic_cat,target_size=(50,50))
pic_cat = img_to_array(pic_cat)
pic_cat = pic_cat / 255 # 归一化
pic_cat = pic_cat.reshape(1,50,50,3) # 第几张图片，3个维度，RGB
result = predict_class(model,pic_cat)
print(result)

output:0
```

> reshape 的 原因
>
> 深度学习模型，尤其是 CNN，训练和预测时需要知道输入数据的各个维度信息(batch_size,height,width,channels)，如果只有一张图像，加载之后通常是(height,width,channels)，所以需要reshape，表示批次大小为 1   cahnnels =3 表示 RGB 图像，=1 表示灰度图像
>
> load_img 是一个PIL图像对象，通常用于显示或保存图像，保留了原始格式，原始信息和属性
>
> img_to_array 将 PIL 转换为 numpy 三维数组，(h,w,channels)，包含了图像的像素值，每个像素值是一个浮点数或整数，表示对应位置的色彩强度，适用于深度学习模型输入，因为模型需要数值化的数据进行计算

##### 批量预测

```py
# make prediction on multiple images
import matplotlib as mlp #用于绘画和显示图像
font2 = {'family' : 'SimHei',  # 黑体
'weight' : 'normal',
'size'   : 20,
}
mlp.rcParams['font.family'] = 'SimHei'
mlp.rcParams['axes.unicode_minus'] = False  #关闭 Matplotlib 的 Unicode 减号显示，避免负号显示错误。
from matplotlib import pyplot as plt  #用于导入 Matplotlib 可视化库中一个最常用的子模块 pyplot
from matplotlib.image import imread
from keras.preprocessing.image import load_img #用于图像预处理，包括加载图像和将图像转换为数组。
from keras.preprocessing.image import img_to_array 
from keras.models import load_model #用于加载训练好的模型。
a = [i for i in range(1,10)]
fig = plt.figure(figsize=(10,10))
for i in a:
    img_name = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/" + str(i)+'.jpg'
    img_ori = load_img(img_name, target_size=(50, 50)) # RGB 图像
    img = img_to_array(img_ori) #转换为 数组，用于 mlp 或 plt 输入
    img = img.astype('float32')/255 #归一化
    img = img.reshape(1,50,50,3)
    result = predict_class(model,img)
    img_ori = load_img(img_name, target_size=(250, 250))
    plt.subplot(3,3,i)
    plt.imshow(img_ori)
    plt.title('预测为：狗狗' if result == 1 else '预测为：猫咪')
plt.show()
```

![image-20250614095018046](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614095018046.png)

> CNN 实现猫狗识别实战 summary
>
> 1. 通过搭建 CNN 模型，实现了对复杂图像的自动识别分类
> 2. 掌握了图像数据的批量加载与图像增强方法
> 3. 更熟练的掌握了keras的sequence结构，并嵌入卷积、池化层
> 4. 实现了对网络图片的分类识别
> 5. 图像预处理参考资料：https://keras.io/preprocessing/image/

#### 实战二

> 使用 VGG16的结构提取图像特征，在根据特征建立mlp模型，实现猫狗图像识别。训练/测试模型：dataset\data.vgg
>
> 1. 对数据进行分离，计算测试数据预测准确率
> 2. 从网站下载猫/狗图片，对其进行预测
>
>    mlp模型一个隐藏层，10个神经元

```py
#load the data 
from keras.preprocessing.image import load_img,img_to_array
img_path = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/1.jpg"
img = load_img(img_path,target_size=(224,224)) # 用到 vgg 上面，大小为 (224,224)
img = img_to_array(img)
print(type(img),img.shape)

output: <class 'numpy.ndarray'> (224, 224, 3)
```

##### 加载VGG16核心

```py
from keras.applications.vgg16 import VGG16
from keras.applications.vgg16 import preprocess_input
import numpy as np
model_vgg = VGG16(weights='imagenet',include_top=False) #加载核心结构 不需要全连接层
# weights='imagenet'：加载在 ImageNet 上预训练的参数
# include_top=False：不加载顶层的 fc（全连接层），只保留卷积层部分，用于特征提取。
x = np.expand_dims(img,axis=0) # 增加维度，axis 表示列
x = preprocess_input(x)
print(x.shape)

ourput:(1, 224, 224, 3)
```

##### 特征提取

```py
# 特征提取
features = model_vgg.predict(x) # 因为去除了全连接层，所以此时的预测结果是全连接层的前一层结果，手动模拟全连接层
print(features.shape)

output:(1, 7, 7, 512)
```

##### flatten 摊开

```py
#摊开 flatten
features = features.reshape(1,7*7*512)
features.shape

output: (1, 25088)
```

##### 可视化

```py
#可视化数据
%matplotlib inline
from matplotlib import pyplot as plt
fig = plt.figure(figsize=(5,5))
img = load_img(img_path,target_size=(224,224))
plt.imshow(img)
plt.show()
```

![image-20250614133205697](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614133205697.png)

##### 加载和处理数据

```py
#load image and preprocess it with vgg16 structure
#--by flare
from keras.preprocessing.image import img_to_array,load_img
from keras.applications.vgg16 import VGG16
from keras.applications.vgg16 import preprocess_input
import numpy as np

model_vgg = VGG16(weights='imagenet', include_top=False)
#define a method to load and preprocess the image
def modelProcess(img_path,model):
    img = load_img(img_path, target_size=(224, 224))
    img = img_to_array(img)
    x = np.expand_dims(img,axis=0)
    x = preprocess_input(x)
    x_vgg = model.predict(x)
    x_vgg = x_vgg.reshape(1,25088)
    return x_vgg
#list file names of the training datasets
import os
# folder = "dataset/data_vgg/cats"
folder = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/dataset/data_vgg/cats" 
dirs = os.listdir(folder)
#generate path for the images
img_path = []
for i in dirs:                             
    if os.path.splitext(i)[1] == ".jpg":   
        img_path.append(i)
img_path = [folder+"//"+i for i in img_path]

#preprocess multiple images
features1 = np.zeros([len(img_path),25088])
for i in range(len(img_path)):
    feature_i = modelProcess(img_path[i],model_vgg)
    print('preprocessed:',img_path[i])
    features1[i] = feature_i
    
folder = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/dataset/data_vgg/dogs" 

dirs = os.listdir(folder)
img_path = []
for i in dirs:                             
    if os.path.splitext(i)[1] == ".jpg":   
        img_path.append(i)
img_path = [folder+"//"+i for i in img_path]
features2 = np.zeros([len(img_path),25088])
for i in range(len(img_path)):
    feature_i = modelProcess(img_path[i],model_vgg)
    print('preprocessed:',img_path[i])
    features2[i] = feature_i
    
#label the results
print(features1.shape,features2.shape)
y1 = np.zeros(300)
y2 = np.ones(300)

#generate the training data
X = np.concatenate((features1,features2),axis=0)
y = np.concatenate((y1,y2),axis=0)
y = y.reshape(-1,1)
print(X.shape,y.shape)
```

![image-20250614133241546](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614133241546.png)

##### 数据分离

```py
# 数据分离
from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.3,random_state=50)
print(X_train.shape,X_test.shape,X.shape)

output:(420, 25088) (180, 25088) (600, 25088)
```

##### 设置 mlp

```py
# set up mlp model 25088个输入，中间有一个隐藏层10个神经元
from keras.models import Sequential #顺序模型
from keras.layers import Dense 
model = Sequential()
model.add(Dense(units=10,activation='relu',input_dim=25088))
model.add(Dense(units=1,activation='sigmoid'))
model.summary()
```

![image-20250614133328750](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614133328750.png)

##### 配置模型

```py
#配置模型
model.compile(optimizer='adam',loss='binary_crossentropy',metrics=['accuracy']) #二分类的
#训练模型
model.fit(X_train,y_train,epochs=100,batch_size=32)
```

##### predict_class

```py
import numpy as np
def predict_class(model, data, batch_size=None, threshold=0.5):
    """
    通用分类预测函数，支持二分类和多分类问题
    Args:
        model: 训练好的Keras模型
        data: 输入数据，可以是单个样本或多个样本
        batch_size: 预测时的批次大小
        threshold: 二分类阈值（仅用于二分类模型）
    Returns:
        预测结果：单个样本返回类别索引（整数），多个样本返回类别索引数组
    """
    # 确保输入是二维数组（即使只有一个样本）
    if len(data.shape) == 1:
        data = np.expand_dims(data, axis=0)
        single_sample = True
    else:
        single_sample = False
    # 获取模型预测结果
    predictions = model.predict(data, batch_size=batch_size)
    # 获取模型输出形状信息
    output_shape = model.output_shape  #output_shape 解释见 pro
    n_classes = output_shape[1] if len(output_shape) > 1 else 1
    # 根据模型类型处理预测结果
    if n_classes == 1:
        # 二分类模型（sigmoid输出）
        predicted_classes = (predictions > threshold).astype(int).flatten()
    elif n_classes > 1:
        # 多分类模型（softmax输出）
        predicted_classes = np.argmax(predictions, axis=1)
    else:
        raise ValueError("无效的模型输出形状")
    # 根据输入类型返回相应格式的结果
    if single_sample:
        return predicted_classes[0]
    return predicted_classes
```

##### 精确度

```py
from sklearn.metrics import accuracy_score
y_train_predict = predict_class(model,X_train)
accuracy_train = accuracy_score(y_train,y_train_predict)
print(accuracy_train)

y_test_predict = predict_class(model,X_test)
accuracy_test = accuracy_score(y_test,y_test_predict)
print(accuracy_test)

# 评估模型在测试集上的性能
test_loss, test_accuracy = model.evaluate(X_test, y_test, batch_size=32) #此时计算的是test训练之后和y_test的精确度
print(f"Test Loss: {test_loss}, Test Accuracy: {test_accuracy}")

output:
14/14 ━━━━━━━━━━━━━━━━━━━━ 0s 3ms/step 
0.9928571428571429
6/6 ━━━━━━━━━━━━━━━━━━━━ 0s 5ms/step 
0.9722222222222222
```

##### 验证

```py
# import matplotlib as mlp
# font2 = {'family' : 'SimHei',
# 'weight' : 'normal',
# 'size'   : 20,
# }
# mlp.rcParams['font.family'] = 'SimHei'
# mlp.rcParams['axes.unicode_minus'] = False
img_now = "E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week8/"
a = [i for i in range(1,10)]
fig = plt.figure(figsize=(5,5))
for i in a:
    img_name =  img_now + str(i) + '.jpg'
    img = load_img(img_name,target_size=(224,224))
    img = img_to_array(img)
    x = np.expand_dims(img,axis=0) #增加一维，因为模型预测需要一个批次尺寸（即使只有一张图片，也需要模拟一个批次）。
    x = preprocess_input(x) #使用 preprocess_input 对图像进行预处理，这是 VGG16 模型所需的标准化步骤。
    x_vgg = model_vgg.predict(x) #使用 VGG16 模型的 predict 方法提取图像特征，
    x_vgg = x_vgg.reshape(1,7*7*512)
    result = predict_class(model,x_vgg)
    img_ori = load_img(img_name,target_size=(250,250))
    plt.subplot(3,3,i)
    plt.imshow(img_ori)
    plt.title(u'狗 ' if result == 1 else u'猫 ') # 输出中文会出问题，此时指定 u 或者开头注释的部分
plt.show()
```

![image-20250614133609732](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614133609732.png)

基于VGG16、结合 mlp 实现猫狗识别图像实战 summary

1、基于经典的 VGG16结构，实现了图像识别模型的快速搭建与训练，并完成猫狗识别任务

2、掌握了拆分已经训练好的模型结构的方法，实现对其灵活应用

3、更熟练的运用 mlp 模型，并将其与其他模型相结合，实现更复杂的任务

4、通过VGG16+MLP模型，实现了在小数据集的情况下的模型快速训练并获得较高的准确率



## 深度学习之循环神经网络

主要用于关注数据里的前后序列关系

### 序列模型

> 基于文本内容及其前后信息进行预测
>
> 基于目标不同时刻状态进行预测
>
> 基于数据历史信息进行预测
>
> 输入或输出中包含有**序列数据**的模型   ------>  **突出数据的前后序列关系**
>
> 两大特点：
>
> 1. 输入（输出）元素之间是具有**顺序关系**。不同的顺序，得到的结果应该是不同的，比如“不吃饭”和“吃饭不”这两个短语的意思是不同的
> 2. 输入输出**不定长**。比如文章生成、聊天机器人

![image-20250614194316834](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614194316834.png)

![image-20250614194409703](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614194409703.png)

![image-20250614204124209](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614204124209.png)

### 循环神经网络 RNN

> 前部序列的信息经处理后，作为输入信息传递到后部序列

![image-20250614205212085](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614205212085.png)



> $g_a\ 是一样的，保证激活函数一致，W\ 矩阵也是一样的$

![image-20250614205544336](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614205544336.png)



> 每个单词作为输入 $X_i$ ，输出的 label 代表是不是人名 （1表示是人名）

![image-20250614205928594](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614205928594.png)



> 词汇数值化：建立一个词汇 - 数值一 一对应的字典，然后把输入词汇转换为数值矩阵
>
> 一部每页只有一个单词的字典，页码就是单词对应的数值，给定的是一个数组，对应 xth 为 1 表示这个词汇的数值就是 x （词汇找到位置转为 one-hot 数组）

![image-20250614210330841](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614210330841.png)

![image-20250614210406496](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614210406496.png)



> 最小化损失函数   ----- RNN 模型核心处理过程

![image-20250614210529857](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614210529857.png)



> 字典  -- 字符代替词汇    会丢失相关联系 对模型要求更高

![image-20250614210655352](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614210655352.png)



> 思考问题

![image-20250614210728492](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250614210728492.png)



### 不同类型的 RNN 模型

1、多输入对多输出、维度相同 RNN 结构   应用：特定信息识别

![image-20250615180051983](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615180051983.png)



2、多输入单输出 + 情感识别          单输入多输出  + 序列数据生成器

![image-20250615180210764](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615180210764.png)



3、多输入多输出但是维度不同  +  语言翻译

![image-20250615180314729](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615180314729.png)



#### 普通 RNN 结构缺陷

1. 前部序列信息在传递到后部的同时，**信息权重下降**，导致**重要信息丢失**，求解过程中梯度消失    -----> 需要提高前部**特定信息**的决策权重 （新结构）

![image-20250615180542905](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615180542905.png)

![image-20250615180624521](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615180624521.png)



#### 长短期记忆网络（LSTM）



![image-20250615183654559](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615183654559.png)

> 可以调整激活函数使得信息丢失尽可能减少
>
> 忘记门 + 更新门 + 输出门

![image-20250615183833943](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615183833943.png)

1. 在网络结构很深（很多层）的情况下，也能保留重要信息
2. 解决了普通 RNN 求解过程中的梯度消失问题

#### 双向循环神经网络（BRNN）

![image-20250615184139234](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615184139234.png)

> 比 RNN 多了一个复制的过程 反向传输

#### 深层循环神经网络（DRNN）

解决更复杂的序列任务，可以把单层 RNN 叠起来或者在输出前和普通 mlp 结构结合使用

![image-20250615184326254](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615184326254.png)

### 实战准备

#### 实战一 ：RNN 实现股价预测

![image-20250615184824366](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615184824366.png)



> slide 表示用多少个点预测下一个点，x 是输入的 8 个数 ，y 是下一个数，使用连续的 8 个数预测第 9 个数
>
> linear 表示线性回归，不是分类模型 而是回归预测 所以采用 mean_squared_error（最小绝对值误差） 作为损失函数

![image-20250615191421119](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615191421119.png)

##### ipput_shape

input_shape = (sample,time_steps,features)

sample：样本数量（模型根据输入数据自动计算），一般会省略

time_steps：序列的长度，即用多少个连续样本预测一个输出

features：样本的特征维数（[0,0,1]对应为3，one_hot 格式，自然语言处理常用）

![image-20250615191808286](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615191808286.png)

#### 实战二：LSTM自动生成文本

> 数据预处理：文字 --> 数值 --> one_hot

![image-20250615192006915](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615192006915.png)

##### 文本加载 + 字典建立

![image-20250615192202593](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615192202593.png)

##### 批量处理

![image-20250615192410987](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615192410987.png)

### 实战一

> 任务：基于zgpa_train.csv数据，建立 RNN 模型，预测股价
>
> 1.完成数据预处理，将序列数据转换为可用于 RNN 输入的数据
>
> 2.对数据的zgpa_test.csv 进行预测，可视化结果
>
> 3.存储预测结果，并观察局部预测结果
>
> 备注：模型结构，单层 RNN，输出有 5 个神经元：每次使用前 8 个数据预测第 9 个数据

#### 加载数据

```py
import pandas as pd
import numpy as np
data = pd.read_csv("E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料\Artificial_Intelligence-master/week9/zgpa_train.csv")
data.head()
```

![image-20250615232906991](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615232906991.png)

#### 收盘价

```py
price = data.loc[:,'close'] # 收盘价
price.head()
```

![image-20250615232932786](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615232932786.png)

#### 归一化处理

```py
#归一化处理
price_norm = price / max(price)
print(price_norm)
print(type(price_norm))
```

#### 可视化数据

```py
#可视化数据
%matplotlib inline 
from matplotlib import pyplot as plt 
fig1 = plt.figure(figsize = (8,5))
plt.title('close price')
plt.xlabel('time')
plt.ylabel('price')
plt.subplot(121)
plt.plot(price)
plt.subplot(122)
plt.plot(price_norm)
plt.show()
```

![image-20250615233001018](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233001018.png)

####  提取 X 和 y

```py
# define X and y
#定义提取 X 和 y 的方法
def extract_data(data,time_step):
    X = [] # 存储特征序列
    y = [] # 存储标签
    #0,1,2,3,...9:10个样本：time_step=8，0,1,2...7  ; 1,2,...8 ; 2,3,4,...9 这种形式为一个样本（包含8个数据）
    for i in range(len(data) - time_step): #最后一个样本无输出，不算完整样本
        X.append([a for a in data[i:i+time_step]]) #py数组后边界是不包含的
        y.append(data[i + time_step]) # 下表访问
    X = np.array(X)
    X = X.reshape(X.shape[0],X.shape[1],1) #数值文件不是文本文件，不是one_shot 格式的，此时的维度就是1
    return X, y
  
#define X & y
time_step = 8
X,y = extract_data(price_norm,time_step)
print(X[0,:,:],'\n',X.shape)
# (723,8,1) 表示723 个样本，每个样本8个数据点，每个数据点是一个单独的数值
print(y)

print(X.shape,len(y))  # y 是 723 个样本，每个样本1个数据点 list 格式  out:(723, 8, 1) 723
```

![image-20250615233058090](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233058090.png)

#### 建立模型

```py
#建立模型
from keras.models import Sequential
from keras.layers import Dense,SimpleRNN
model = Sequential()
#add RNN layer
model.add(SimpleRNN(units = 5,activation = 'relu',input_shape=(time_step,1)))
# add output layer
model.add(Dense(units=1,activation='linear')) #不是分类了，不需要使用sigmoid，softmax，使用线性即可，回归预测
#configure the model
model.compile(optimizer='adam',loss = 'mean_squared_error') 
model.summary()
```

![image-20250615233139520](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233139520.png)

#### 训练模型

```py
#trian the model
y = np.array(y)
# y 需要转换为numpy array格式以确保兼容性和性能
# 早期版本可能会自动转换，但显式转换更安全
model.fit(X,y,batch_size=30,epochs=200)  # 不能加metrics参数因为这是回归问题而不是分类问题，accuracy只适用于分类。对于回归问题，我们使用MSE作为评估指标，这已经在compile()中通过loss参数设置了。
#深度学习mlp初始状态不好的话可能会出现局部最优解，可以再次compile一次model，重新训练
```

#### 可视化预测效果

```py
#可视化预测效果 make prediction based on the training data  
y_train_predict = model.predict(X) * max(price)
y_train = y * max(price) #list 无法相乘，需要转换为numpy array
# print(y_train_predict,y_train)
fig2 = plt.figure(figsize = (5,5))
plt.title('prediction')
plt.plot(y_train_predict,label = 'predict_price')
plt.plot(y_train,label = 'real_price')
plt.legend()
plt.show()
```

![image-20250615233225797](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233225797.png)

#### 测试

```py
# 对测试数据进行预测
data_set = pd.read_csv("E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料\Artificial_Intelligence-master/week9/zgpa_test.csv")
data_set.head()
price_test = data_set.loc[:,'close']
price_test.head()
#extract X_test and y_test
price_test_norm = price_test / max(price) # 归一化
X_test_norm,y_test_norm = extract_data(price_test_norm,time_step)
print(X_test_norm.shape,len(y_test_norm))

# make prediction based on the test data 
y_test_predict = model.predict(X_test_norm) * max(price)
y_test_norm = np.array(y_test_norm)
y_test = y_test_norm * max(price)
print(y_test_predict,y_test)
```

#### 可视化

```py
fig3 = plt.figure(figsize = (5,3))
plt.title('prediction_test')
plt.plot(y_test_predict,label = 'predict_price')
plt.plot(y_test,label = 'real_price')
plt.title('predicition price')
plt.xlabel('time')
plt.ylabel('price')
plt.legend()
plt.show()
```

![image-20250615233316995](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233316995.png)

#### 存储预测结果，局部数据

```py
# 存储预测结果，对局部数据进行观察
result_y_test = y_test.reshape(-1,1) #先将测试数据存储下来，若干行，一列,前面已经转换为 array了
result_y_test_predict =  y_test_predict.reshape(-1,1)
print(result_y_test.shape,result_y_test_predict.shape)
result = np.concatenate((result_y_test,result_y_test_predict),axis = 1) # 合并两个array，axis = 1 表示按列合并
print(result.shape)
result = pd.DataFrame(result,columns = ['real_price_test','predict_price_test']) # 转换为DataFrame,columns 表示列名,DataFrame 是二维的，可以存储表格数据
result.head() #将numpy 转化为图标形式
result.to_csv('E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料\Artificial_Intelligence-master/week9/zgpa_test_result.csv')
# 这个时候预测结果是存在一定误差的，需要对误差进行分析，后滞性，局部预测比真实下降反应慢
```

![image-20250615233338150](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233338150.png)

> RNN 预测股价实战 summary
> 1. 通过搭建RNN模型，实现了基于历史数据对次日股价的预测
> 2. 熟悉了 RNN 模型的数据格式结构
> 3. 掌握了数字序列的数据预处理方法
> 4. 实现了预测数据存储，通过可视化局部细节了解了 RNN 用于股价预测的局限性：信息延迟
> 5. RNN模型参考资料：https://keras.io/layers/recurrent/
> 6. 模型结构：单层 RNN，输出有 5 个神经元：每次使用前 8 个数据预测第 9 个数据
> 7. 模型训练：使用adam优化器，损失函数为均方误差
> 8. 模型评估：使用MSE作为评估指标

### 实战二

> 任务：基于flare文本数据，建立LSTM模型， 预测序列文字
>
> 1. 完成数据预处理，将文字序列数据转化为可用于LSTM输入的数据
> 2. 查看文字数据预处理后的数据结构，并进行数据分离操作
> 3. 针对字符串输入 ("flare is a teacher in ai industry. He obtained his phd in Australia.") 预测其对应的后续字符
>
> 备注：模型结构：单层 LSTM，输出有20个神经元：每次使用前 20 个字符预测第 21 个字符

#### 读取数据

```py
# 文字预处理 文字 --> 数字（字典） --> one_hot    需要保证每一个字符都存在于字典里面
#加载数据
data = open("E:/(www.dmzshequ.com)Python3入门人工智能 掌握机器学习+深度学习 提升实战能力/课程资料/Artificial_Intelligence-master/week9/flare").read()
#移除换行符
data = data.replace('\n',' ').replace('\r',' ')
print(data)
```

![image-20250615233437291](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233437291.png)

#### 字符去重，做字典

```py
# 字符去重处理
letters = list(set(data))
print(letters)
num_letters = len(letters)
print(num_letters)

out:
['p', 'r', 'y', 'o', 't', 'c', 'A', 'l', ' ', 'e', 'a', 'n', '.', 's', 'm', 'H', 'd', 'b', 'h', 'u', 'S', 'f', 'i']
23
```

#### 字典 映射

```py
# 建立字典
#int to char
int_to_char = {a:b for a,b in enumerate(letters)} #enumerate 将数字和字符关系一一对应
print(int_to_char)
#char_to_int
char_to_int = {b:a for a,b in enumerate(letters)}
print(char_to_int) #每个字符在哪一页上
# for a,b in enumerate(letters):
#     print(a,b)

out :
  {0: 'p', 1: 'r', 2: 'y', 3: 'o', 4: 't', 5: 'c', 6: 'A', 7: 'l', 8: ' ', 9: 'e', 10: 'a', 11: 'n', 12: '.', 13: 's', 14: 'm', 15: 'H', 16: 'd', 17: 'b', 18: 'h', 19: 'u', 20: 'S', 21: 'f', 22: 'i'}
{'p': 0, 'r': 1, 'y': 2, 'o': 3, 't': 4, 'c': 5, 'A': 6, 'l': 7, ' ': 8, 'e': 9, 'a': 10, 'n': 11, '.': 12, 's': 13, 'm': 14, 'H': 15, 'd': 16, 'b': 17, 'h': 18, 'u': 19, 'S': 20, 'f': 21, 'i': 22}
```

#### 处理函数

```py
time_step = 20
#批量字符数据预处理
import numpy as np
from keras.utils import to_categorical
#滑动窗口提取数据
def extract_data(data,slide):
    x = []
    y = []
    for i in range(len(data) - slide):
        x.append([a for a in data[i:i+slide]])
        y.append(data[i+slide])
    return x,y

def char_to_int_Data(x,y,char_to_int):
    x_to_int = []
    y_to_int = []
    for i in range(len(x)):
        x_to_int.append([char_to_int[char] for char in x[i]]) # 将每个字符通过char_to_int字典映射为对应的数字索引
                                                              # 例如: 如果x[i]是"hello", char_to_int={'h':0,'e':1,'l':2,'o':3}
                                                              # 则转换后得到[0,1,2,2,3]
        y_to_int.append([char_to_int[char] for char in y[i]]) # 将字符转化为数字
    return x_to_int,y_to_int

#实现输入字符文章的批量转换，输入整个字符，滑动窗口的大小，转换字典
def data_preprocessing(data,slide,num_letters,char_to_int):
    char_Data = extract_data(data,slide)
    int_Data = char_to_int_Data(char_Data[0],char_Data[1],char_to_int)
    Input = int_Data[0]
    Output = list(np.array(int_Data[1]).flatten()) #flatten 将二维数组展平为一维数组
    Input_RESHAPED = np.array(Input).reshape(len(Input),slide)# 将输入数据展平为一维数组
    # 创建一个三维数组用于存储one-hot编码后的数据
    # Input_RESHAPED.shape[0]表示样本数量
    # Input_RESHAPED.shape[1]表示每个样本的时间步长(滑动窗口大小)
    # num_letters表示字符集大小,即one-hot编码的维度
    # np.random.randint(0,10,...) 表示生成0到9之间的随机整数
    # 但这里其实不需要随机数,因为后面会用to_categorical填充one-hot编码
    # 所以直接用zeros初始化即可
    new = np.random.randint(0,10,size=[Input_RESHAPED.shape[0],Input_RESHAPED.shape[1],num_letters])
    #randint 随机生成一个整数,
    for i in range(Input_RESHAPED.shape[0]):
        for j in range(Input_RESHAPED.shape[1]):
                new[i,j,:] = to_categorical(Input_RESHAPED[i,j],num_classes=num_letters)#将每个字符转化为one-hot编码
                #to_categorical 将整数转换为one-hot编码
    return new,Output
```

#### 提取数据

```py
#提取 x 和 y 从数据中
X,y = data_preprocessing(data,time_step,num_letters,char_to_int) #X 是输入数据one-hot编码，y 是输出数据,是对应数值
#num_letters 是字符集大小,即one-hot编码的维度,即是“多少页” time_step 是滑动窗口的大小 char_to_int 是字符到数字的映射字典 data 是原始文本数据
```

#### 维度判断

```py
# 判断维度是否相同
print(type(X),type(y))
# print(X)
print(X.shape)
print(len(y))

out:
<class 'numpy.ndarray'> <class 'list'>
(56620, 20, 23)
56620 
```

#### 数据分离

```py
#数据分离
from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.1,random_state=10)
print(X_train.shape,X_test.shape,X.shape)
# 将 y_train 转化为内 one_hot
# to_categorical函数参数:
# y_train: 输入的整数标签数组，可以是numpy array或list类型，但建议使用numpy array以提高性能
# num_classes: 类别总数,这里是字符集大小num_letters
y_train_category = to_categorical(y_train,num_classes=num_letters)
print(y_train_category)
```

#### 建立模型

```py
#建立模型
from keras.models import Sequential
from keras.layers import LSTM,Dense

model = Sequential()
model.add(LSTM(units=20,activation = 'relu',input_shape=(X_train.shape[1],X_train.shape[2])))
model.add(Dense(units=num_letters,activation = 'softmax')) #输出层，输出20个神经元，每个神经元对应一个字符
model.compile(optimizer = 'adam',loss = 'categorical_crossentropy',metrics = ['accuracy'])
model.summary()

#train the model
model.fit(X_train,y_train_category,batch_size=100,epochs=5) #LSTM需要的参数格式是one-hot编码
```



predict_class 函数与之前的一致，省略



#### 预测 + 转换

```py
#预测
y_train_predict = predict_class(model,X_train)
print(y_train_predict)

out : [19 18  9 ... 19  7 16]

#transform the int to letters
y_train_predict_char = [int_to_char[i] for i in y_train_predict]
print(y_train_predict_char)
```

![image-20250615233941860](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615233941860.png)

#### 准确度

```py
from sklearn.metrics import accuracy_score
accuracy = accuracy_score(y_train,y_train_predict)
print(accuracy)

out : 1.0

y_test_predict = predict_class(model,X_test)
accuracy_test = accuracy_score(y_test,y_test_predict)
print(accuracy_test)
print(y_test_predict)
print(y_test)
y_test_predict_char = [int_to_char[i] for i in y_test_predict]
```

![image-20250615234038539](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615234038539.png)

#### 预测新数据

```py
new_letter = "flare is a teacher in ai industry. He obtained his phd in Australia."
X_new,y_new = data_preprocessing(new_letter,time_step,num_letters,char_to_int)
y_new_predict = predict_class(model,X_new)
print(y_new_predict)

out:
  [11  8 10 22  8 22 11 16 19 13  4  1  2 12  8 15  9  8  3 17  4 10 22 11
  9 16  8 18 22 13  8  0 18 16  8 22 11  8  6 19 13  4  1 10  7 22 10 12]
  
y_new_predict_char = [int_to_char[i] for i in y_new_predict]
print(y_new_predict_char) #每次使用20个字符预测下一个字符

out : 
  ['n', ' ', 'a', 'i', ' ', 'i', 'n', 'd', 'u', 's', 't', 'r', 'y', '.', ' ', 'H', 'e', ' ', 'o', 'b', 't', 'a', 'i', 'n', 'e', 'd', ' ', 'h', 'i', 's', ' ', 'p', 'h', 'd', ' ', 'i', 'n', ' ', 'A', 'u', 's', 't', 'r', 'a', 'l', 'i', 'a', '.']
  
for i in range(0,X_new.shape[0]-20):
print(new_letter[i:i+20],'--predict next letter is---',y_new_predict_char[i])
```

![image-20250615234147781](./AI%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.assets/image-20250615234147781.png)

