import logging
import pandas as pd
import numpy as np
# from torchtext import data
from torchtext.legacy import data
from torchtext.vocab import Vectors

import torch_model as tm
from pandas.core.frame import DataFrame
from sklearn.metrics import accuracy_score
import json
logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')
logging.root.setLevel(level=logging.INFO)

# data_str = open('20news.json').read()
# df = pd.read_json(data_str,orient="columns")
# df.head()
with open("stopword.txt", "r") as f:  # 打开文件
    stopword= f.read()  # 读取文件
   


data_list=[]

with open('20news.json','r',encoding='utf-8') as f:
        try:
            while True:
                line = f.readline()
                if line:
                    r = json.loads(line)
                    # print(r['text'])
                    data_list.append(r)
                else:
                    break
        except:
            f.close()

# print(data_list)

#将json文件转成dataframe格式并写入csv
data_after=[]
datas=DataFrame(data_list)
# print(datas.head())
#停用词处理
for row in datas['text']:
    after_text=[]
    
    for i in row:
        if i not in stopword:
            after_text.append(i)
    data_after.append(after_text)
    
datas_after=DataFrame(data_after)
print(datas_after.head())            
# for row in datas['text']:
#     new = re.sub("[\s+\.\!\/_,$%^*()+\"\:\-\'\\\]+|[+——！，。？、~@#￥%……&*（）]+", " ", raw)

print("datas",datas.head())
# datas.to_csv("raw.csv")

# #数据切分处理

# data_cut1=datas[datas['label'].isin(['0','1','2','3','4'])]
# data_cut1.to_csv("raw0.csv")
# data_cut1=datas[datas['label'].isin(['5','6','7','8','9'])]
# data_cut1.to_csv("raw1.csv")
# data_cut1=datas[datas['label'].isin(['10','11','12','13','14'])]
# data_cut1.to_csv("raw2.csv")
# data_cut1=datas[datas['label'].isin(['15','16','17','18','19'])]
# data_cut1.to_csv("raw3.csv")
# categories = ['Cases', 'Commentary', 'Legislation (Common Law)',
#               'Journals', 'Forms & Precedents', 'General Search']
categories = ['0','1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19']
cat_dict = dict(zip(categories, range(len(categories))))

print(cat_dict)

max_length = 20

train_data_path = './datas/train.csv'
val_data_path = './datas/val.csv'
test_data_path = './datas/test.csv'

label0=[0,1,2,3,4]
label1=[5,6,7,8,9]
label2=[10,11,12,13,14]
label3=[15,16,17,18,19]
# word2vec
def train_model():
    pass


# preprocess data
def get_train_val_test():
    # source_df = pd.read_csv('./data/uk_lib_intent_202106_processed.csv')

    source_df = pd.read_csv('raw0.csv')
    
    train_df = pd.DataFrame()
    val_df = pd.DataFrame()
    test_df = pd.DataFrame()
    for k in label0:
        print(source_df['label'])
        print(k)
        k_df = source_df[source_df['label'] == k]
        print(k_df)
        #inplace=True 不创建新的对象，直接对原始对象进行修改
        k_df.drop_duplicates(inplace=True)
        # if k_df.shape[0] > 10000:
        #     k_df = k_df.sample(10000, random_state=1)
        
        query_list = k_df['text'].values
        val_idx = int(0.8 * len(query_list))
        test_idx = int(0.9 * len(query_list))
        train_q, val_q, test_q = query_list[:val_idx], query_list[val_idx: test_idx], query_list[test_idx:]
        train_df = train_df.append(pd.DataFrame([[q, k] for q in train_q]))
        val_df = val_df.append(pd.DataFrame([[q, k] for q in val_q]))
        test_df = test_df.append(pd.DataFrame([[q, k] for q in test_q]))
    print(train_df.head())
    train_df.to_csv(train_data_path, index=False)
    val_df.to_csv(val_data_path, index=False)
    test_df.to_csv(test_data_path, index=False)
    print('generate train, val, test dataset')


# cut text
def tokenize(text):
    return [word for word in text.strip().split()]


# cnn classifier model
def train_cnn_model():
    config = tm.Config()

    text_field = data.Field(lower=True, tokenize=tokenize)
    label_field = data.Field(sequential=False)
    fields = [('text', text_field), ('label', label_field)]
    train_dataset, val_dataset = data.TabularDataset.splits(path='./', format='csv', skip_header=True,
                                                            train=train_data_path, test=val_data_path, fields=fields
                                                            )
    vectors=Vectors(name="./model/word2vec")
    text_field.build_vocab(train_dataset, val_dataset, min_freq=1, vectors=vectors)

    label_field.build_vocab(train_dataset, val_dataset)
    # test_dataset = data.TabularDataset(
    #     path=test_data_path,
    #     format='csv',
    #     fields=fields,
    #     skip_header=True
    # )
    # text_field.build_vocab(train_dataset, val_dataset, test_dataset, min_freq=1, vectors='fasttext.en.300d')
    # label_field.build_vocab(test_dataset)

    train_iter, val_iter = data.Iterator.splits((train_dataset, val_dataset),
                                                batch_sizes=(config.batch_size, config.batch_size),
                                                sort_key=lambda x: len(x.text))

    embed_num = len(text_field.vocab)
    class_num = len(label_field.vocab) - 1
    kernel_sizes = [int(k) for k in config.kernel_sizes.split(',')]

    cnn = tm.TextCnn(embed_num, config.embed_dim, class_num, config.kernel_num, kernel_sizes, config.dropout)
    if config.cuda:
        cnn = cnn.cuda()
    tm.train(train_iter, val_iter, cnn, config)


# test model
def test_intent():
    config = tm.Config()

    text_field = data.Field(lower=True, tokenize=tokenize)
    label_field = data.Field(sequential=False)
    fields = [('text', text_field), ('label', label_field)]

    train_dataset, val_dataset = data.TabularDataset.splits(path='./', format='csv', skip_header=True,
                                                            train=train_data_path, test=val_data_path, fields=fields
                                                          )
    vectors = Vectors(name="./model/word2vec")
    text_field.build_vocab(train_dataset, val_dataset, min_freq=1, vectors=vectors)

    label_field.build_vocab(train_dataset, val_dataset)

    test_dataset = data.TabularDataset(
        path=test_data_path,
        format='csv',
        fields=fields,
        skip_header=True
    )
    test_iter = data.Iterator(test_dataset, batch_size=config.batch_size, sort_key=lambda x: len(x.text))

    print('Loading model from {}...'.format(config.snapshot))
    embed_num = len(text_field.vocab)
    class_num = len(label_field.vocab) - 1
    kernel_sizes = [int(k) for k in config.kernel_sizes.split(',')]

    config.snapshot = './model/snapshot/best_steps_200.pt'

    cnn = tm.TextCnn(embed_num, config.embed_dim, class_num, config.kernel_num, kernel_sizes, config.dropout)
    cnn.load_state_dict(tm.torch.load(config.snapshot))

    summary_predict(cnn, text_field, label_field)


# summary predict result
def summary_predict(cnn, text_field, label_field):
    test_df = pd.read_csv(test_data_path)
    for k in label0:
        print(k)
        k_df = test_df[test_df['label'] == k]
        print("***********")
        print(k_df)
        k_count = k_df.shape[0]
        err_count = 0
        query_list = k_df['text'].values
        for q in query_list:
            label = tm.predict(q, cnn, text_field, label_field)
            if int(label) != k:
                # print(q, label)
                err_count += 1
        print(err_count)
        print('acc: %.4f' % ((k_count - err_count) / k_count))
        # return


if __name__ == '__main__':
    #get_train_val_test()
    # train_cnn_model()
    test_intent()
    pass
