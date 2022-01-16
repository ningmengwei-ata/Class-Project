from builtins import range
from builtins import object
import numpy as np
from past.builtins import xrange


class KNearestNeighbor(object):
    """ a kNN classifier with L2 distance """

    def __init__(self):
        pass

    def train(self, X, y):
        """
        Train the classifier. For k-nearest neighbors this is just
        memorizing the training data.

        Inputs:
        - X: A numpy array of shape (num_train, D) containing the training data
          consisting of num_train samples each of dimension D.
        - y: A numpy array of shape (N,) containing the training labels, where
             y[i] is the label for X[i].
        """
        self.X_train = X
        self.y_train = y

    def predict(self, X, k=1, num_loops=0):
        """
        Predict labels for test data using this classifier.

        Inputs:
        - X: A numpy array of shape (num_test, D) containing test data consisting
             of num_test samples each of dimension D.
        - k: The number of nearest neighbors that vote for the predicted labels.
        - num_loops: Determines which implementation to use to compute distances
          between training points and testing points.

        Returns:
        - y: A numpy array of shape (num_test,) containing predicted labels for the
          test data, where y[i] is the predicted label for the test point X[i].
        """
        if num_loops == 0:
            dists = self.compute_distances_no_loops(X)
        elif num_loops == 1:
            dists = self.compute_distances_one_loop(X)
        elif num_loops == 2:
            dists = self.compute_distances_two_loops(X)
        else:
            raise ValueError('Invalid value %d for num_loops' % num_loops)

        return self.predict_labels(dists, k=k)

    def compute_distances_two_loops(self, X):
        """
        Compute the distance between each test point in X and each training point
        in self.X_train using a nested loop over both the training data and the
        test data.

        Inputs:
        - X: A numpy array of shape (num_test, D) containing test data.

        Returns:
        - dists: A numpy array of shape (num_test, num_train) where dists[i, j]
          is the Euclidean distance between the ith test point and the jth training
          point.
        """
        num_test = X.shape[0]
        num_train = self.X_train.shape[0]
        dists = np.zeros((num_test, num_train))
        for i in range(num_test):
            for j in range(num_train):
                #####################################################################
                # TODO: 
                #计算第i个测试点与第j个训练点之间的l2距离，并将结果存储在dists[i，j]中。
                #你不应使用循环和np.linalg.norm()函数。    
                #####################################################################
                # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

              dists[i][j]=np.sqrt(np.sum(np.square(self.X_train[j,:]-X[i,:])))

                # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
        return dists

    def compute_distances_one_loop(self, X):
        """
        Compute the distance between each test point in X and each training point
        in self.X_train using a single loop over the test data.

        Input / Output: Same as compute_distances_two_loops
        """
        num_test = X.shape[0]
        num_train = self.X_train.shape[0]
        dists = np.zeros((num_test, num_train))
        for i in range(num_test):
            #######################################################################
            # TODO:                                                               
            #计算第i个测试点与所有训练点之间的l2距离，并将结果存储在dists[i，：]中。
            #不要使用np.linalg.norm()。                                     
            #######################################################################
            # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
            # axis=1 列
          dists[i,:]=np.sqrt(np.sum(np.square(self.X_train-X[i,:]),axis=1))
            # 注意np.sum中要加上维度axis=1才能得出正确的结果
            # 关于axis的介绍
            # https://zhuanlan.zhihu.com/p/30960190
            # 以及np.sum的介绍
            # https://docs.scipy.org/doc/numpy/reference/generated/numpy.sum.html
            
            # self.X_train (5000,3072) X[i] (1,3072) (self.X_train - X[i]) (5000,3072)
            # pass

            # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
        return dists

    def compute_distances_no_loops(self, X):
        """
        Compute the distance between each test point in X and each training point
        in self.X_train using no explicit loops.

        Input / Output: Same as compute_distances_two_loops
        """
        num_test = X.shape[0]
        num_train = self.X_train.shape[0]
        dists = np.zeros((num_test, num_train))
        #########################################################################
        # TODO:                                                                 
        #在不使用任何显式循环的情况下，计算所有测试点和所有训练点之间的l2距离，
        #并将结果存储在dists中。                                                     
        #您应该仅使用基本的数组操作来实现此功能。
        #不可以使用scipy中的函数以及函数np.linalg.norm()。
        #
        #提示：尝试使用矩阵乘法和广播总和来计算l2距离。                           
        #########################################################################
        # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

        # (x-y)^2 = x^2 + y^2 - 2xy
        # reshape是为了让两个矩阵有个维度为1，这样子便可进行广播
        # pass
        # 按行相加，并且保持其二维特性
        a=np.sum(np.square(X),axis=1,keepdims= True)

        b=np.sum(np.square(self.X_train),axis=1)
        # np.dot 点积
        c=np.multiply(np.dot(X,self.X_train.T),-2)
        dists=np.add(a,b)
        dists=np.add(dists,c)
        dists=np.sqrt(dists)
        # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
        return dists

    def predict_labels(self, dists, k=1):
        """
        Given a matrix of distances between test points and training points,
        predict a label for each test point.

        Inputs:
        - dists: A numpy array of shape (num_test, num_train) where dists[i, j]
          gives the distance betwen the ith test point and the jth training point.

        Returns:
        - y: A numpy array of shape (num_test,) containing predicted labels for the
          test data, where y[i] is the predicted label for the test point X[i].
        """
        num_test = dists.shape[0]
        y_pred = np.zeros(num_test)
        for i in range(num_test):
            # A list of length k storing the labels of the k nearest neighbors to
            # the ith test point.
            closest_y = []
            #########################################################################
            # TODO:                                                                   
            #使用距离矩阵查找第i个测试点的k个最近邻居，
            #并使用self.y_train查找这些邻居的标签。
            #将这些标签存储在closest_y中。
            #
            #提示：查阅函数numpy.argsort。                            
            #########################################################################
            # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
            closest_y=self.y_train[np.argsort(dists[i,:])[:k]]
            # numpy.argsort 返回排序好的数列的索引
            # pass

            # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****
            #########################################################################
            # TODO:                                                                 
            #
            #现在，你已经找到了k个最近邻的标签，接着需要在closest_y中找到最可能的标签。                                                     #将此标签存储在y_pred [i]中。如果有两个标签可能性一样的话选择索引更小的那个。
            #########################################################################
            # *****START OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

            # y_pred[i] = np.bincount(closest_y).argmax()
            y_pred[i]=np.argmax(np.bincount(closest_y))
            # *****END OF YOUR CODE (DO NOT DELETE/MODIFY THIS LINE)*****

        return y_pred
