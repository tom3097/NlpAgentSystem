from neuralcoref import Coref
import json

with open('../data/pre-nlp/reviews.json') as f:
    data = json.load(f)

coref = Coref()

wrong_count = 0
index = 0

print len(data)

for d in data:
    print index
    index += 1
    review = d['text']
    try:
        clusters = coref.one_shot_coref(utterances=review)
    except Exception as e:
        print e.message
        wrong_count += 1
        continue
    d['text'] = coref.get_resolved_utterances()[0]

with open('../data/pre-nlp/reviews-coref.json', 'w') as fw:
    fw.write(json.dumps(data))

print wrong_count
print len(data)

