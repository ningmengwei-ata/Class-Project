#%%

num_of_doc=10
tf_words=[]
num_of_words=[]
with open('data.txt', 'r',encoding='UTF-8') as f:
    for i in range(num_of_doc):
        words = f.readline().split()
        num_of_words.append(len(words))
        tf_words.append({})
        for word in words:
            if word in tf_words[i]:
                tf_words[i][word] += 1/num_of_words[i]
            else:
                tf_words[i][word] = 1/num_of_words[i]


idf_words={}
for i in range(num_of_doc):
    for item in tf_words[i].items():
        word=item[0]
        if word in idf_words:
            idf_words[word]+=1/num_of_doc
        else:
            idf_words[word]=1/num_of_doc

tf_idf_words=[]
for i in range(num_of_doc):
    tf_idf_words.append({})
    for item in tf_words[i].items():
        word=item[0]
        tf_idf_words[i][word]=idf_words[word]*tf_words[i][word]


import pandas as pd

tf=pd.DataFrame(tf_words).fillna(0)
idf=pd.DataFrame([idf_words]).fillna(0)
tf_idf=pd.DataFrame(tf_idf_words).fillna(0)


tf.to_csv('tf.csv')
idf.to_csv('idf.csv')
tf_idf.to_csv('tf_idf.csv')

# %%
