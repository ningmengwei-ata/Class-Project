####modules reqjred
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




"""make_dictionary(filename_en, filename_de)
src_dict maps each word in the german dictionary to an integer. src_rev_dict is the inverse mapping in which each integer is mapped to a word.
tgt_dict maps each word in the english dictionary to an integer. tgt_rev_dict is the inverse mapping in which each integer is mapped to a word.
returns(src_dict, src_rev_dict, tgt_dict,tgt_rev_dic)
"""


def make_dictionary(filename_en, filename_de):
    src_dict = dict()
    with open(filename_de, encoding='utf-8') as f:
        for line in f:
            src_dict[line[:-1]] = len(src_dict)

    src_rev_dict = dict(zip(src_dict.values(),src_dict.keys()))

    tgt_dict = dict()
    with open(filename_en, encoding='utf-8') as f:
        for line in f:
            tgt_dict[line[:-1]] = len(tgt_dict)
    tgt_rev_dic = dict(zip(tgt_dict.values(),tgt_dict.keys()))
    return src_dict, src_rev_dict, tgt_dict,tgt_rev_dic

"""
sentence_split(sentence,is_source,src_dict,tgt_dict):
splits a sentence into its constituent literals(words, symbols), last new line character is omitted.
return: literals
 
"""


def sentence_split(sentence,is_source,src_dict,tgt_dict):
    sentence = sentence.replace('-',' ')
    sentence = sentence.replace(',',' ,')
    sentence = sentence.replace('.',' .')
    sentence = sentence.replace('\n',' ') 
    
    literals = sentence.split(' ')
    for t, lit in enumerate(literals):
        if is_source:
            if lit not in src_dict.keys():
                literals[t] = '<unk>'
        else:
            if lit not in tgt_dict.keys():
                literals[t] = '<unk>'
    return literals

"""
padding(src_sentence, tgt_sentence,src_dictionary,tgt_dictionary):
makes all the sentences of same length by padding with </s> until the sentence reaches max length, if that is excedded, it is truncated
return(train_inputs, train_outputs, train_inp_lengths, train_out_lengths)
"""

def padding(src_sentence, tgt_sentence,src_dictionary,tgt_dictionary):

    training_inputs = []
    training_outputs = []
    train_input_size = []
    training_output_size = []

    for i, (src_sent, tgt_sent) in enumerate(zip(src_sentence,tgt_sentence)):
        
        src_sent_tokens = sentence_split(src_sent,True,src_dictionary,tgt_dictionary)
        tgt_sent_tokens = sentence_split(tgt_sent,False,src_dictionary,tgt_dictionary)          
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

"""
class data_batch_generate(object):
to generate batch of data in the format [source_sequence_length, batch_size, embedding_size] using unroll function. The data supplied to the unroll function 
is by next_batch function.

"""

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


#making all the sentences of equal length
train_inputs, train_outputs, train_inp_lengths, train_out_lengths = padding(source_sent, target_sent,src_dictionary,tgt_dictionary)
tf.reset_default_graph()

enc_train_inputs = []
dec_train_inputs = []

# using pre-trained word embeddings generated using word2vec
encoder_emb_layer = tf.convert_to_tensor(np.load('de-embeddings.npy'))
decoder_emb_layer = tf.convert_to_tensor(np.load('en-embeddings.npy'))

# defining unrolled batch training inputs
for j in range(source_sequence_length):
    enc_train_inputs.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='enc_train_inputs_%d'%j))

dec_train_labels=[]
dec_label_masks = []
for j in range(target_sequence_length):
    dec_train_inputs.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='dec_train_inputs_%d'%j))
    dec_train_labels.append(tf.compat.v1.placeholder(tf.int32, shape=[batch_size],name='dec-train_outputs_%d'%j))
    dec_label_masks.append(tf.compat.v1.placeholder(tf.float32, shape=[batch_size],name='dec-label_masks_%d'%j))
    
# mapping each literal to its embedding
encoder_emb_inp = [tf.nn.embedding_lookup(encoder_emb_layer, src) for src in enc_train_inputs]
encoder_emb_inp = tf.stack(encoder_emb_inp)

decoder_emb_inp = [tf.nn.embedding_lookup(decoder_emb_layer, src) for src in dec_train_inputs]
decoder_emb_inp = tf.stack(decoder_emb_inp)

enc_train_inp_lengths = tf.placeholder(tf.int32, shape=[batch_size],name='train_input_lengths')
dec_train_inp_lengths = tf.placeholder(tf.int32, shape=[batch_size],name='train_output_lengths')

#defining encoder cell using lstm

encoder_cell = tf.nn.rnn_cell.BasicLSTMCell(num_units)
initial_state = encoder_cell.zero_state(batch_size, dtype=tf.float32)
encoder_outputs, encoder_state = tf.nn.dynamic_rnn(
    encoder_cell, encoder_emb_inp, initial_state=initial_state,
    sequence_length=enc_train_inp_lengths, 
    time_major=True, swap_memory=False)


#defining decoder cell using lstm and Badanau attention

decoder_cell = tf.nn.rnn_cell.BasicLSTMCell(num_units)
projection_layer = Dense(units=vocab_size, use_bias=True)
helper = tf.contrib.seq2seq.TrainingHelper(
    decoder_emb_inp, [tgt_max_sentence_length-1 for _ in range(batch_size)], time_major=True)

attention_mechanism = tf.contrib.seq2seq.LuongAttention(
    num_units = 128, 
    memory = encoder_outputs,
    memory_sequence_length = enc_train_inp_lengths)

decoder_cell = tf.contrib.seq2seq.AttentionWrapper(
    cell = tf.nn.rnn_cell.BasicLSTMCell(128),
    attention_mechanism = attention_mechanism,
    attention_layer_size = 128)

training_decoder = tf.contrib.seq2seq.BasicDecoder(
    cell = decoder_cell,
    helper = helper,
    initial_state = decoder_cell.zero_state(batch_size,tf.float32).clone(cell_state=encoder_state),
    output_layer = projection_layer)
    
outputs, _, _ = tf.contrib.seq2seq.dynamic_decode(
    training_decoder, output_time_major=True,
    swap_memory=False
)

logits = outputs.rnn_output
crossent = tf.nn.sparse_softmax_cross_entropy_with_logits(labels=dec_train_labels, logits=logits)
loss = (tf.reduce_sum(crossent*tf.stack(dec_label_masks)) / (batch_size*target_sequence_length))
train_prediction = outputs.sample_id

#defining learning rate with exponential decay and adam optimizer
global_step = tf.Variable(0, trainable=False)
inc_gstep = tf.compat.v1.assign(global_step,global_step + 1)
learning_rate = tf.compat.v1.train.exponential_decay(
    0.01, global_step, decay_steps=10, decay_rate=0.9, staircase=True)

with tf.compat.v1.variable_scope('Adam'):
    adam_optimizer = tf.compat.v1.train.AdamOptimizer(learning_rate)

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

num_steps = 60001
avg_loss = 0

file_avg = open("avg_file.txt","w") 
 
print('Started Training')
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
    
    feed_dict[dec_train_inp_lengths] = du_lengths
    for j,(dat,lbl) in enumerate(zip(du_data,du_labels)):            
        feed_dict[dec_train_inputs[j]] = dat
        feed_dict[dec_train_labels[j]] = lbl
        feed_dict[dec_label_masks[j]] = (np.array([j for _ in range(batch_size)])<du_lengths).astype(np.int32)
 
#defining model saver
    saver = tf.compat.v1.train.Saver()


    
    _,l,tr_pred = sess.run([adam_optimize,loss,train_prediction], feed_dict=feed_dict)
    
        
    tr_pred = tr_pred.flatten()
        
            
    if (step+1)%100==0:   
    
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
    
    if (step+1)%200==0:
        print('============= Step ', str(step+1), ' =============')
        print('\t Loss: ',avg_loss/500.0)
        save_path = saver.save(sess, "/log/testmodel")
        print("Model saved in path: %s" % save_path)
        avg_file.write(avg_loss)

        
             
        avg_loss = 0.0
        sess.run(inc_gstep)

avg_file.close()