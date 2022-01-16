from  bleu import bleu
import tensorflow as tf
import os
from translation import *

src_test_sent=[]
tgt_test_sent=[]
with open("de_dev.txt") as f:
    for l_i, line in enumerate(f):  
        if l_i<50:
            continue
        src_test_sent.append(line)
with open("en_dev.txt")  as f: 
    for l_i, line in enumerate(f):  
        if l_i<50:
            continue
        tgt_test_sent.append(line)
# with tf.Session() as sess:
#     reloaded =tf.compat.v1.saved_model.load(sess,'checkpoint','/logs')
#     result = reloaded.tf_translate(src_test_sent)
batch_size=60
def evaluate(src_test,tgt_test):
    num_examples = len(src_test)
    total_accuracy = 0
    total_loss = 0
    sess = tf.get_default_session()
    acc_steps = len(src_test) // batch_size
    
    test_inputs, test_outputs, test_inp_lengths, test_out_lengths = padding(src_test, tgt_test,src_dictionary,tgt_dictionary)
    outputs, _, _ = tf.contrib.seq2seq.dynamic_decode(
    training_decoder, output_time_major=True,
    swap_memory=False)

    for i in range(acc_steps):
        

        test_prediction=outputs.sample_id  

        loss, accuracy = sess.run([adam_optimize,loss,test_prediction], feed_dict=feed_dict)
        total_accuracy += (accuracy * 60)
        total_loss += (loss * 60)
        
    return (total_accuracy / num_examples, total_loss / num_examples)

# sess=tf.Session()
# sess.run(tf.global_variables_initializer())


# check_file=tf.train.latest_checkpoint('./logs/testmodel')
# saver.restore(sess,check_file)
variable=tf.get_variable()

saver = tf.train.Saver()

with tf.Session() as sess:
    sess.run(tf.initialize_all_variables())
    ckpt = tf.train.get_checkpoint_state('./logs')
    if ckpt and ckpt.model_checkpoint_path:
        saver.restore(sess, ckpt.model_checkpoint_path)
        test_accuracy = evaluate(src_test_sent, tgt_test_sent)
        print(test_accuracy[0])

    else:
        pass

# checkpoint_file=tf.train.latest_checkpoint('./logs')
# print("checkpoint:",checkpoint_file)
# reloaded = tf.saved_model.load(sess,'testmodel','./logs/testmodel')
# saver = tf.train.import_meta_graph("{}.meta".format(checkpoint_file))



