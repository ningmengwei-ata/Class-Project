from gensim.models import word2vec
import pandas as pd
from bs4 import BeautifulSoup
import re
import nltk
import time

start = time.time()
data=pd.read_csv("raw.csv")
X = data['text']


def news_to_sentences(news):
    news_text = BeautifulSoup(news).get_text()
#     print(news_text)

#     tokenizer = nltk.data.load('tokenizers/punkt/english.pickle')
#     raw_sentences = tokenizer.tokenize(news_text)

    raw_sentences = news_text.split(" ")
    sentences = []
    for sent in raw_sentences:
        sentences.append(re.sub('[^a-zA-Z]', ' ', sent.lower().strip()).split())
    return sentences


# 句子词语列表化
sentences = []
for x in X:
    # print(x)
    sentences.extend(news_to_sentences(x))
# print(len(sentences))
# 设置词语向量维度
num_featrues = 300

# 设置并行化训练使用CPU计算核心数量
num_workers = 2
# 设置词语上下午窗口大小
context = 5
downsampling = 1e-3
print(sentences)
model = word2vec.Word2Vec(sentences, vector_size=300,workers=num_workers, window=context)

model.init_sims(replace=True)

# 输入一个路径，保存训练好的模型，其中./data/model目录事先要存在
model.wv.save_word2vec_format("./model/word2vec")
model.save("./model/word2vec_gensim")