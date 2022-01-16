import os
import sys

import numpy as np
import torch
import torch.autograd as autograd
import torch.nn.functional as F
import torch.nn as nn


class Config:
    def __init__(self):
        self.lr = 0.001
        self.epochs = 20
        self.batch_size = 64
        self.log_interval = 1
        self.test_interval = 100
        self.save_interval = 500
        self.save_dir = './model/snapshot'
        self.early_stop = 1000
        self.save_best = True
        self.shuffle = False
        self.dropout = 0.5
        self.max_norm = 3.0
        self.embed_dim = 300
        self.kernel_num = 100
        self.kernel_sizes = '3, 4, 5'
        self.static = False
        self.device = -1
        self.cuda = torch.cuda.is_available()
        self.snapshot = './model/snapshot/best_steps_200.pt'


class TextCnn(nn.Module):
    def __init__(self, embed_num, embed_dim, class_num, kernel_num, kernel_sizes, dropout=0.5):
        super(TextCnn, self).__init__()
        Ci = 1
        Co = kernel_num

        self.embed = nn.Embedding(embed_num, embed_dim)
        self.convs1 = nn.ModuleList([nn.Conv2d(Ci, Co, (f, embed_dim), padding=(2, 0)) for f in kernel_sizes])
        self.dropout = nn.Dropout(dropout)
        self.fc = nn.Linear(Co * len(kernel_sizes), class_num)

    def forward(self, x):
        x = self.embed(x)  # (N, token_num, embed_dim)
        x = x.unsqueeze(1)  # (N, Ci, token_num, embed_dim)
        x = [F.relu(conv(x)).squeeze(3) for conv in self.convs1]  # [(N, Co, token_num) * len(kernel_sizes)]
        x = [F.max_pool1d(i, i.size(2)).squeeze(2) for i in x]  # [(N, Co) * len(kernel_sizes)]
        x = torch.cat(x, 1)  # (N, Co * len(kernel_sizes))
        x = self.dropout(x)  # (N, Co * len(kernel_sizes))
        pred = self.fc(x)  # (N, class_num)
        return pred


def train(train_iter, dev_iter, model, args):
    model.train()

    optimizer = torch.optim.Adam(model.parameters(), lr=args.lr)

    steps = 0
    best_acc = 0
    last_step = 0

    for epoch in range(1, args.epochs + 1):
        for batch in train_iter:
            feature, target = batch.text, batch.label
            feature.t_(), target.sub_(1)  # batch first, index align
            if args.cuda:
                feature, target = feature.cuda(), target.cuda()

            optimizer.zero_grad()
            logit = model(feature)

            loss = F.cross_entropy(logit, target)
            loss.backward()
            optimizer.step()

            steps += 1
            if steps % args.log_interval == 0:
                corrects = (torch.max(logit, 1)[1].view(
                    target.size()).data == target.data).sum()
                accuracy = 100.0 * float(corrects) / batch.batch_size
                sys.stdout.write('\rBatch[{}] - loss: {:.6f}  acc: {:.3f}%({}/{})'.format(steps,
                                                                                          loss.data,
                                                                                          accuracy,
                                                                                          corrects,
                                                                                          batch.batch_size))
            if steps % args.test_interval == 0:
                dev_acc = val(dev_iter, model, args)
                if dev_acc > best_acc:
                    best_acc = dev_acc
                    last_step = steps
                    save(model, args.save_dir, 'best', steps)
            if steps % args.save_interval == 0:
                save(model, args.save_dir, 'snapshot', steps)


def val(data_iter, model, args):
    model.eval()
    corrects, avg_loss = 0, 0
    for batch in data_iter:
        feature, target = batch.text, batch.label
        feature.t_(), target.sub_(1)  # batch first, index align
        if args.cuda:
            feature, target = feature.cuda(), target.cuda()

        logit = model(feature)
        loss = F.cross_entropy(logit, target, size_average=False)

        avg_loss += loss.data
        corrects += (torch.max(logit, 1)[1].view(target.size()).data == target.data).sum()

    size = len(data_iter.dataset)
    avg_loss /= size
    accuracy = 100.0 * float(corrects) / size
    print('Evaluation - loss: {:.6f}  acc: {:.3f}% ({}/{}) \n'.format(avg_loss,
                                                                      accuracy,
                                                                      corrects,
                                                                      size))
    return accuracy


def test(data_iter, model, args):
    pass


def predict(text, model, text_field, label_field):
    assert isinstance(text, str)
    model.eval()

    text = text_field.preprocess(text)
    text = [[text_field.vocab.stoi[x] for x in text]]
    x = torch.tensor(text)
    x = autograd.Variable(x)

    output = model(x)
    _, predicted = torch.max(output, 1)
    return label_field.vocab.itos[predicted.data + 1]


def save(model, save_dir, save_prefix, steps):
    if not os.path.isdir(save_dir):
        os.makedirs(save_dir)
    save_path = os.path.join(save_dir, '{}_steps_{}.pt'.format(save_prefix, steps))
    torch.save(model.state_dict(), save_path)
