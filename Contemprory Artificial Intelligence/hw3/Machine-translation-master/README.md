**German to english translation using encoder-decoder model using attention implemented in tensorflow**
The embeddings are produced using word2vec.

You can set the number of sentences considered for training by changing sentences_to_read

- Dataset used : german - english Europarl Parallel corpus(The dataset can be found at https://nlp.stanford.edu/projects/nmt/data/wmt14.en-de/)
- Library used : seq2seq(tensorflow)
- Trained for 60,000 iterations(can be changed)
- hidden size can be changed by changing num_units
- Embedding(word2vec)(trained embeddings are stored in the embeddings folder otherwise you can train word2vec using gensim library)
- Dictionary of vocabulary (taken from  https://nlp.stanford.edu/projects/nmt/data/wmt14.en-de/)
- maximum sentence length = 61 (can be changed by changing source_sequence_length and target_sequence_length) 
- used Adam optimiser

**References**
- Neubig, G. (2017). Neural Machine Translation and Sequence-to-sequence Models: A Tutorial, 1–65. Retrieved from http://arxiv.org/abs/1703.01619
- Luong, M.-T., Pham, H., & Manning, C. D. (2015). Effective Approaches to Attention-based Neural Machine Translation. https://doi.org/10.18653/v1/D15-1166
- Sutskever, I., Vinyals, O., & Le, Q. V. (2014). Sequence to sequence learning with neural networks. Advances in Neural Information Processing Systems (NIPS), 3104–3112. https://doi.org/10.1007/s10107-014-0839-0
- https://github.com/zhedongzheng/finch/blob/master/nlp-models/tensorflow/
- https://towardsdatascience.com/neural-machine-translator-with-less-than-50-lines-of-code-guide-1fe4fdfe6292
- https://github.com/ilblackdragon/tf_examples/tree/master/seq2seq
- https://github.com/jihoon-ko/seq2seq-nmt
- https://github.com/shawnxu1318/Google-Neural-Machine-Translation-GNMT
- http://adventuresinmachinelearning.com/word2vec-tutorial-tensorflow/
