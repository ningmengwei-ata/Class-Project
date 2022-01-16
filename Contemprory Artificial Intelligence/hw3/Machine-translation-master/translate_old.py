
import math
import numpy as np
import os
import random
import tensorflow as tf
import tensorflow.contrib.seq2seq as seq2seq
from tensorflow.python.ops.rnn_cell import LSTMCell
from tensorflow.python.ops.rnn_cell import MultiRNNCell
from tensorflow.contrib.seq2seq.python.ops import attention_wrapper
from tensorflow.python.layers.core import Dense




def make_dictionary(filename_en, filename_de):
    src_dict = dict()
    with open(filename_de, encoding='utf-8') as f:
        for line in f:
            # save the first word,not the number
            # line=line.split(" ")
            # line=line[0]
            # print(line)
            src_dict[line[:-1]] = len(src_dict)

    src_rev_dict = dict(zip(src_dict.values(),src_dict.keys()))

    tgt_dict = dict()
    with open(filename_en, encoding='utf-8') as f:
        for line in f:
            # line=line.split(" ")
            # line=line[0]
            tgt_dict[line[:-1]] = len(tgt_dict)
    tgt_rev_dic = dict(zip(tgt_dict.values(),tgt_dict.keys()))
    return src_dict, src_rev_dict, tgt_dict,tgt_rev_dic

# preprocess
# make the english and german dictionary
# after running once, we don't need to run it again
def write_en():
    data=[]
    with open("vocab.en", encoding='utf-8') as f:
        for line in f:
        # 取出每一行的第一个，也就是单词，不需要词频
            line=line.split(" ")
            line=line[0]
            data.append(line)
    with open("preprocess.en", "w") as f:
        for a in data:
            f.writelines(a)
            f.write("\n")
        print("writing test en complete")

def write_de():
    data_de=[]
    with open("vocab.de", encoding='utf-8') as f:
        for line in f:
            line=line.split(" ")
            line=line[0]
            data_de.append(line)

    with open("preprocess.de", "w") as f:
        for a in data_de:
            f.writelines(a)
            f.write("\n")
        print("writing test de complete")

import gensim
from gensim.models import Word2Vec  
def embedding():
    model=Word2Vec("preprocess.en",vector_size=100,window=5,min_count=2,negative=3,sample=0.001,hs=1,workers=4)
    model.save("englishembedding.npy")  
    print("embedding model en saved")
    model = Word2Vec.load("englishembedding.npy") 
    model=Word2Vec("preprocess.de",vector_size=100,window=5,min_count=2,negative=3,sample=0.001,hs=1,workers=4)
    model.save("germanembedding.npy")  
    print("embedding model de saved")


import re

def tokenize(sentence,source,src_dict,tgt_dict):
    sentence = sentence.replace('-',' ')
    sentence = sentence.replace(',',' ,')
    sentence = sentence.replace('.',' .')
    sentence = sentence.replace('\n',' ') 
    # sentence = re.sub('-',' ',sentence)
    # sentence = re.sub('\n',' ',sentence) 
    # sentence = re.sub(',',' ,',sentence)
    # sentence = re.sub('.',' .',sentence)
    
    tokens = sentence.split(' ')
    for t, key in enumerate(tokens):
        if source:
            if key not in src_dict.keys():
                tokens[t] = '<unk>'
        else:
            if key not in tgt_dict.keys():
                tokens[t] = '<unk>'
    return tokens



def padding(src_sentence, tgt_sentence,src_dictionary,tgt_dictionary):

    training_inputs = []
    training_outputs = []
    train_input_size = []
    training_output_size = []

    for i, (src_sent, tgt_sent) in enumerate(zip(src_sentence,tgt_sentence)):
       
        
        src_sent_tokens = tokenize(src_sent,True,src_dictionary,tgt_dictionary)
        tgt_sent_tokens = tokenize(tgt_sent,False,src_dictionary,tgt_dictionary)          
        num_src_sent = []
        for tok in src_sent_tokens:
            num_src_sent.append(src_dictionary[tok])
        num_src_set = num_src_sent[::-1] 
        num_src_sent.insert(0,src_dictionary['<s>'])
        train_input_size.append(min(len(num_src_sent)+1,src_max_sentence_length))       
        if len(num_src_sent)<src_max_sentence_length:           
            num_src_sent.extend([src_dictionary['</s>'] for _ in range(src_max_sentence_length - len(num_src_sent))])
        
        elif len(num_src_sent)>src_max_sentence_length:
            num_src_sent = num_src_sent[:src_max_sentence_length]
        

        training_inputs.append(num_src_sent)

        num_tgt_sent = [tgt_dictionary['</s>']]
        for tok in tgt_sent_tokens:
            num_tgt_sent.append(tgt_dictionary[tok])
        
        training_output_size.append(min(len(num_tgt_sent)+1,tgt_max_sentence_length))
        
        if len(num_tgt_sent)<tgt_max_sentence_length:
            num_tgt_sent.extend([tgt_dictionary['</s>'] for _ in range(tgt_max_sentence_length - len(num_tgt_sent))])
        elif len(num_tgt_sent)>tgt_max_sentence_length:
            num_tgt_sent = num_tgt_sent[:tgt_max_sentence_length]
        
        training_outputs.append(num_tgt_sent)

    training_inputs = np.array(training_inputs, dtype=np.int32)
    training_outputs = np.array(training_outputs, dtype=np.int32)
    train_input_size = np.array(train_input_size, dtype=np.int32)
    training_output_size = np.array(training_output_size, dtype=np.int32)

    return training_inputs, training_outputs, train_input_size, training_output_size


class data_batch_generate(object):
    
    def __init__(self,batch_size,num_unroll,is_source,train_inputs,train_outputs ,src_dictionary):
        self._batch_size = batch_size
        self._num_unroll = num_unroll
        self._pointer = [0 for offset in range(self._batch_size)]  
        self._src_word_embeddings = np.load('german-embeddings.npy')        
        self._tgt_word_embeddings = np.load('eng-embeddings.npy')        
        self._sent_ids = None        
        self._is_source = is_source
        self.train_inputs  = train_inputs
        self.train_outputs = train_outputs
        self.src_dictionary = src_dictionary
                
    def next_batch(self, sent_ids, first_set):

        if self._is_source:
            max_sent_length = src_max_sentence_length
        else:
            max_sent_length = tgt_max_sentence_length
        batch_labels_ind = []
        batch_data = np.zeros((self._batch_size),dtype=np.float32)
        batch_labels = np.zeros((self._batch_size),dtype=np.float32)
        
        for b in range(self._batch_size):
            
            sent_id = sent_ids[b]            
            if self._is_source:
                sent_text = self.train_inputs[sent_id]                             
                batch_data[b] = sent_text[self._pointer[b]]
                batch_labels[b]=sent_text[self._pointer[b]+1]
            else:
                sent_text = self.train_outputs[sent_id]
                if sent_text[self._pointer[b]]!=self.src_dictionary['<s>']:
                    batch_data[b] = sent_text[self._pointer[b]]
                else:
                    batch_data[b] = sent_text[self._pointer[b]]
                batch_labels[b] = sent_text[self._pointer[b]+1]                
            self._pointer[b] = (self._pointer[b]+1)%(max_sent_length-1)
                                  
        return batch_data,batch_labels
        
    def unroll_batches(self,sent_ids,train_inp_lengths):
        
        if sent_ids is not None:
            
            self._sent_ids = sent_ids
            
            self._pointer = [0 for _ in range(self._batch_size)]
                
        unroll_data,unroll_labels = [],[]
        inp_lengths = None
        for j in range(self._num_unroll):
            # The first batch in any batch of captions is different
            if self._is_source:
                data, labels = self.next_batch(self._sent_ids, False)
            else:
                data, labels = self.next_batch(self._sent_ids, False)
                    
            unroll_data.append(data)
            unroll_labels.append(labels)
            inp_lengths = train_inp_lengths[sent_ids]
        return unroll_data, unroll_labels, self._sent_ids, inp_lengths
    
    def reset_indices(self):
        self._pointer = [0 for offset in range(self._batch_size)]



#define parameters

vocab_size= 50000
num_units = 128
input_size = 128
batch_size = 60
source_sequence_length=60
target_sequence_length=60
sentences_to_read = 50000
decoder_type ="attention"
source_sent = []
target_sent = []
test_source_sent = []
test_target_sent = []
train_inputs = []
train_outputs = []
train_inp_lengths = []
train_out_lengths = []
max_tgt_sent_lengths = 0
src_max_sentence_length = 61
tgt_max_sentence_length = 61

# making source and target dictionaries
src_dictionary, src_reverse_dictionary, tgt_dictionary,tgt_reverse_dictionary = make_dictionary("vocab.50K.en.txt","vocab.50K.de.txt")

# read the german and english database
with open("train.de", encoding='utf-8') as f:
    for l_i, line in enumerate(f):      
        if l_i<50:
            continue
        source_sent.append(line)
        if len(source_sent)>=sentences_to_read:
            break           
            
with open("train.en", encoding='utf-8') as f:
    for l_i, line in enumerate(f):
        if l_i<50:
            continue            
        target_sent.append(line)
        if len(target_sent)>=sentences_to_read:
            break
print("debug------get the first ten elments of the dict",list(src_dictionary.keys())[:10])

#making all the sentences of equal length
train_inputs, train_outputs, train_inp_lengths, train_out_lengths = padding(source_sent, target_sent,src_dictionary,tgt_dictionary)
tf.compat.v1.reset_default_graph()

enc_train_inputs = []
dec_train_inputs = []

# using pre-trained word embeddings 
encoder_emb_layer = tf.convert_to_tensor(np.load('de-embeddings.npy'))
decoder_emb_layer = tf.convert_to_tensor(np.load('en-embeddings.npy'))
# encoder_emb_layer = tf.convert_to_tensor(np.load('germanembedding.npy',allow_pickle=True))
# decoder_emb_layer = tf.convert_to_tensor(np.load('englishembedding.npy',allow_pickle=True))
# defining unrolled batch training inputs
for j in range(source_sequence_length):
    enc_train_inputs.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='enc_train_inputs_%d'%j))

dec_train_labels=[]
dec_label_masks = []
for j in range(target_sequence_length):
    dec_train_inputs.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='dec_train_inputs_%d'%j))
    dec_train_labels.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='dec-train_outputs_%d'%j))
    dec_label_masks.append(tf.compat.v1.placeholder(tf.float32, shape=[batch_size],name='dec-label_masks_%d'%j))
    
# mapping each token to its embedding
encoder_emb_inp = [tf.nn.embedding_lookup(encoder_emb_layer, src) for src in enc_train_inputs]
encoder_emb_inp = tf.stack(encoder_emb_inp)

decoder_emb_inp = [tf.nn.embedding_lookup(decoder_emb_layer, src) for src in dec_train_inputs]
decoder_emb_inp = tf.stack(decoder_emb_inp)

source_sequence_length = tf.placeholder(tf.int32, shape=[batch_size],name='train_input_lengths')
target_sequence_length = tf.placeholder(tf.int32, shape=[batch_size],name='train_output_lengths')

# Build RNN cell
encoder_cell = tf.nn.rnn_cell.BasicLSTMCell(num_units)
initial_state = encoder_cell.zero_state(batch_size, dtype=tf.float32)
# Run Dynamic RNN
#   encoder_outputs: [max_time, batch_size, num_units]
#   encoder_state: [batch_size, num_units]
encoder_outputs, encoder_state = tf.nn.dynamic_rnn(
    encoder_cell, encoder_emb_inp, initial_state=initial_state,
    sequence_length=source_sequence_length, 
    time_major=True, swap_memory=False)


#decoder 
# Build RNN cell
decoder_cell = tf.nn.rnn_cell.BasicLSTMCell(num_units)
projection_layer = Dense(units=vocab_size, use_bias=True)
# Helper
helper = tf.contrib.seq2seq.TrainingHelper(
    decoder_emb_inp, [tgt_max_sentence_length-1 for _ in range(batch_size)], time_major=True)

# attention
attention_mechanism = tf.contrib.seq2seq.LuongAttention(
    num_units = 128, 
    memory = encoder_outputs,
    memory_sequence_length = source_sequence_length)

decoder_cell = tf.contrib.seq2seq.AttentionWrapper(
    cell = tf.nn.rnn_cell.BasicLSTMCell(128),
    attention_mechanism = attention_mechanism,
    attention_layer_size = 128)
#decoder
training_decoder = tf.contrib.seq2seq.BasicDecoder(
    cell = decoder_cell,
    helper = helper,
    initial_state = decoder_cell.zero_state(batch_size,tf.float32).clone(cell_state=encoder_state),
    output_layer = projection_layer)
# Dynamic decoding    
outputs, _, _ = tf.contrib.seq2seq.dynamic_decode(
    training_decoder, output_time_major=True,
    swap_memory=False
)

logits = outputs.rnn_output
#loss
crossent = tf.nn.sparse_softmax_cross_entropy_with_logits(labels=dec_train_labels, logits=logits)
# print(type((batch_size*target_sequence_length)))
# print(type(tf.reduce_sum(crossent*tf.stack(dec_label_masks))))
loss = (tf.reduce_sum(crossent*tf.stack(dec_label_masks)) / (batch_size*target_sequence_length))
train_prediction = outputs.sample_id

#defining learning rate with exponential decay and adam optimizer
global_step = tf.Variable(0, trainable=False)
inc_gstep = tf.compat.v1.assign(global_step,global_step + 1)
learning_rate = tf.compat.v1.train.exponential_decay(
    0.01, global_step, decay_steps=10, decay_rate=0.9, staircase=True)

# choose adam optimizer
with tf.compat.v1.variable_scope('Adam'):
    adam_optimizer = tf.compat.v1.train.AdamOptimizer(learning_rate)
# Calculate and clip gradients
adam_gradients, v = zip(*adam_optimizer.compute_gradients(loss))
adam_gradients, _ = tf.clip_by_global_norm(adam_gradients, 25.0)
adam_optimize = adam_optimizer.apply_gradients(zip(adam_gradients, v))

#create session
sess = tf.compat.v1.InteractiveSession()

#initaialise all the variables
tf.compat.v1.global_variables_initializer().run()

src_word_embeddings = np.load('de-embeddings.npy')
tgt_word_embeddings = np.load('en-embeddings.npy')

# Defining data generators
enc_data_generator = data_batch_generate(batch_size=batch_size,num_unroll=source_sequence_length,is_source=True,train_inputs = train_inputs,train_outputs= train_outputs,src_dictionary = src_dictionary)
dec_data_generator = data_batch_generate(batch_size=batch_size,num_unroll=target_sequence_length,is_source=False,train_inputs = train_inputs, train_outputs = train_outputs,src_dictionary = src_dictionary)

num_steps = 50001
avg_loss = 0

avg_file = open("avg_file.txt","w") 
 
print('Training Started ')
for step in range(num_steps):

    print('.',end='')   
    if (step+1)%100==0:
        print("\n")
    sent_ids = np.random.randint(low=0,high=train_inputs.shape[0],size=(batch_size))
    
#data collection for encoder        
    eu_data, eu_labels, _, eu_lengths = enc_data_generator.unroll_batches(sent_ids=sent_ids,train_inp_lengths = train_inp_lengths)
    
    feed_dict = {}
    feed_dict[enc_train_inp_lengths] = eu_lengths
    for j,(dat,lbl) in enumerate(zip(eu_data,eu_labels)):            
        feed_dict[enc_train_inputs[j]] = dat                
    
#data collection for decoder
    
    du_data, du_labels, _, du_lengths = dec_data_generator.unroll_batches(sent_ids=sent_ids,train_inp_lengths = train_inp_lengths)
    
    feed_dict[target_sequence_length] = du_lengths
    for j,(dat,lbl) in enumerate(zip(du_data,du_labels)):            
        feed_dict[dec_train_inputs[j]] = dat
        feed_dict[dec_train_labels[j]] = lbl
        feed_dict[dec_label_masks[j]] = (np.array([j for _ in range(batch_size)])<du_lengths).astype(np.int32)
 
#defining model saver
    saver = tf.compat.v1.train.Saver()


    
    _,l,tr_pred = sess.run([adam_optimize,loss,train_prediction], feed_dict=feed_dict)
    
        
    tr_pred = tr_pred.flatten()
        
            
    if (step+1)%100==0:  
        print("--------------------------------------") 
    
        print('Step ',step+1)
        print_str = 'Actual: '
        for w in np.concatenate(du_labels,axis=0)[::batch_size].tolist():
            
            print_str += tgt_reverse_dictionary[w] + ' '                    
            if tgt_reverse_dictionary[w] == '</s>':
                break
                     
        print(print_str)
               
        print_str = 'Predicted: '
        for w in tr_pred[::batch_size].tolist():
            print_str += tgt_reverse_dictionary[w] + ' '

            if tgt_reverse_dictionary[w] == '</s>':
                break
        print(print_str)
        print('\n')  
        
        
        
 

        rand_idx = np.random.randint(low=1,high=batch_size)
        print_str = 'Actual: '
        for w in np.concatenate(du_labels,axis=0)[rand_idx::batch_size].tolist():
            print_str += tgt_reverse_dictionary[w] + ' '
            if tgt_reverse_dictionary[w] == '</s>':
                break
        print(print_str)            
        
        print_str = 'Predicted: '
        for w in tr_pred[rand_idx::batch_size].tolist():
            print_str += tgt_reverse_dictionary[w] + ' '
            if tgt_reverse_dictionary[w] == '</s>':
                break
        print(print_str)
        print()        
        
    avg_loss += l
    
    if (step+1)%500==0:
        print('Step ', str(step+1))
        print('\t Loss: ',avg_loss/500.0)
        
        save_path = saver.save(sess, "log/testmodel")
        print("Model saved in path: %s" % save_path)
        avg_file.write(str(avg_loss))

        
             
        avg_loss = 0.0
        sess.run(inc_gstep)

avg_file.close()