from builtins import range
import numpy as np
from random import shuffle
from past.builtins import xrange

def softmax_loss_naive(W, X, y, reg):
    """
    Softmax loss function, naive implementation (with loops)

    Inputs have dimension D, there are C classes, and we operate on minibatches
    of N examples.

    Inputs:
    - W: A numpy array of shape (D, C) containing weights.
    - X: A numpy array of shape (N, D) containing a minibatch of data.
    - y: A numpy array of shape (N,) containing training labels; y[i] = c means
      that X[i] has label c, where 0 <= c < C.
    - reg: (float) regularization strength

    Returns a tuple of:
    - loss as single float
    - gradient with respect to weights W; an array of same shape as W
    """
    # Initialize the loss and gradient to zero.
    loss = 0.0
    dW = np.zeros_like(W)

    #############################################################################
    # TODO: 使用显式循环计算softmax损失及其梯度。
    # 将损失和梯度分别保存在loss和dW中。
    # 如果你不小心，很容易遇到数值不稳定的情况。 
    # 不要忘了正则化！                                                           
    #############################################################################
    # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
    (N, D) = X.shape
    C = W.shape[1]
    #遍历每个样本
    for i in range(N):
      f_i = X[i].dot(W)
    #进行公式的指数修正
      f_i -= np.max(f_i)
      sum_j = np.sum(np.exp(f_i))
    #得到样本中每个类别的概率
      p = lambda k : np.exp(f_i[k]) / sum_j
      loss += - np.log(p(y[i]))
    #根据softmax求导公式
      for k in range(C):
        p_k = p(k)
        dW[:, k] += (p_k - (k == y[i])) * X[i]
  
    loss /= N
    loss += 0.5 * reg * np.sum(W * W)
    dW /= N
    dW += reg*W
    # pass

    # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

    return loss, dW


def softmax_loss_vectorized(W, X, y, reg):
    """
    Softmax loss function, vectorized version.

    Inputs and outputs are the same as softmax_loss_naive.
    """
    # Initialize the loss and gradient to zero.
    loss = 0.0
    dW = np.zeros_like(W)

    #############################################################################
    # TODO: 不使用显式循环计算softmax损失及其梯度。
    # 将损失和梯度分别保存在loss和dW中。
    # 如果你不小心，很容易遇到数值不稳定的情况。 
    # 不要忘了正则化！
    #############################################################################
    # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
    (N, D) = X.shape
    C = W.shape[1]
    f = X.dot(W)
    #在列方向进行指数修正
    f -= np.max(f,axis=1,keepdims=True)
    #求得softmax各个类的概率
    p = np.exp(f) / np.sum(np.exp(f),axis=1,keepdims=True)
    y_lable = np.zeros((N,C))
    #y_lable就是(N,C)维的矩阵，每一行中只有对应的那个正确类别 = 1，其他都是0
    y_lable[np.arange(N),y] = 1
    #cross entropy
    loss = -1 * np.sum(np.multiply(np.log(p),y_lable)) / N
    loss += 0.5 * reg * np.sum( W * W)
    #求导公式
    dW = X.T.dot(p-y_lable)
    dW /= N
    dW += reg*W
    # pass

    # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

    return loss, dW
