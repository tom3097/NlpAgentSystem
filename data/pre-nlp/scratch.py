# coding: utf-8
get_ipython().run_line_magic('ls', '')
import nltk
import json
reviews = json.loads(open('reviews.json').read())
review = reviews[0]
review['text']
tokens = nltk.word_tokenize(review['text'])
nltk.download('punkt')
tokens = nltk.word_tokenize(review['text'])
tokens
tagged = nltk.pos_tag(tokens)
nltk.downlad('averaged_perceptron_tagger')
nltk.download('averaged_perceptron_tagger')
tagged = nltk.pos_tag(tokens)
tagged
entities = nltk.chunk.ne_chunk(tagged)
nltk.download('maxent_ne_chunker')
entities = nltk.chunk.ne_chunk(tagged)
entities = nltk.chunk.ne_chunk(tagged)
nltk.download('words')
entities = nltk.chunk.ne_chunk(tagged)
entities
from nltk.corpus import treebank
treeback.parsend_sents(entities)
treebank.parsend_sents(entities)
nltk.download('treebank')
treebank.parsend_sents(entities)
treebank.parsed_sents(entities)
treebank.parsed_sents('wsj_0001.mrg')[0].draw()
entities.draw()
entities.draw()
get_ipython().run_line_magic('ls', '')
from nltk.corpus import sentiwordnet as swn
swn
swn.senti_sysnet
nltk.download('sentiwordnet')
swn.senti_sysnet
swn.senti_synset
swn.senti_synset(tokens)
swn.senti_synset(review['text'])
nltk.download('wordnet')
swn.senti_synset(review['text'])
review['text']
swn.all_senti_synsets()
list(swn.all_senti_synsets())
swn.all_senti_synsets()[:5]
list(swn.all_senti_synsets())[:5]
from nltk import tokenize
sentences = tokenize.sent_tokenize(review['text'])
sentences
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk.sentiment.vader import SentimentIntensityAnalyzer
sid SentimentIntensityAnalyzer()
sid = SentimentIntensityAnalyzer()
nltk.download('vader_lexicon')
sid = SentimentIntensityAnalyzer()
for sentence in sentences:
    print(sentence)
    ss = sid.polarity_scores(sentence)
    print(ss)
    
entites
sentences
for sentence in sentences:
    tokens = nltk.word_tokenize(sentence)
    tagged = nltk.pos_tag(tokens)
    entities = nltk.chunk.ne_chunk(tagged)
    entities
    
for sentence in sentences:
    tokens = nltk.word_tokenize(sentence)
    tagged = nltk.pos_tag(tokens)
    entities = nltk.chunk.ne_chunk(tagged)
    print(entities)
    
    
for sentence in sentences:
    print('begin')
    tokens = nltk.word_tokenize(sentence)
    tagged = nltk.pos_tag(tokens)
    entities = nltk.chunk.ne_chunk(tagged)
    print(entities)
    
    
