from nltk.parse import stanford
from nltk import sent_tokenize
from nltk.sentiment.vader import SentimentIntensityAnalyzer
import json
import re

components = {
    'processor': [
        'processor',
        'processors',
        'cpu',
        'cpus'
    ],
    'keyboard': [
        'keyboard',
        'keyboards'
    ],
    'graphics':
        ['graphic',
         'graphics',
         'gpu',
         'gpus'
    ],
    'touchpad': [
        'touchpad',
        'touchpads',
        'trackpad',
        'trackpads',
        'touch pad',
        'touch pads'
    ],
    'battery': [
        'battery',
        'batteries'
    ],
    'screen': [
        'screen',
        'screens',
        'display',
        'displays',
        'touchscreen',
        'touchscreens'
    ],
    'wifi': [
        'wifi',
        'wi-fi',
        'wireless network',
        'wireless networks',
        'wireless'
    ],
    'usb': [
        'usb',
        'usbs'
    ],
    'fan': [
        'fan',
        'fans',
        'cooling system',
        'cooling systems'
    ],
    'audio': [
        'audio',
        'audios',
        'sound',
        'speaker',
        'speakers'
    ],
    'camera': [
        'camera',
        'cameras'
    ],
    'memory': [
        'memory',
        'memories',
        'hdd',
        'hdds',
        'sdd',
        'sdds',
        'hard drive',
        'hard drives',
        'hard disk',
        'hard disks',
        'storage',
        'storages'
    ]
}

components_counter = {
    'processor': 0,
    'keyboard': 0,
    'graphics': 0,
    'touchpad': 0,
    'battery': 0,
    'screen': 0,
    'wifi': 0,
    'usb': 0,
    'fan': 0,
    'audio': 0,
    'camera': 0,
    'memory': 0
}


def get_regex(sequence):
    return re.compile(r'\b{}\b'.format(sequence))


def convert_PRN(leaves):
    conv_leaves = []
    for l in leaves:
        if l == '-LRB-':
            conv_leaves.append('(')
        elif l == '-RRB-':
            conv_leaves.append(')')
        else:
            conv_leaves.append(l)
    return conv_leaves


def contains_keyword(sentence):
    for val in components.values():
        for v in val:
            if get_regex(v).search(sentence.lower()):
                return True
    return False


with open('../data/pre-nlp/reviews-coref.json') as f:
    reviews = json.load(f)

parser = stanford.StanfordParser()
sid = SentimentIntensityAnalyzer()

feature_reviews = []
review_counter = 0

for review in reviews:
    print(review_counter)
    review_counter += 1
    text = review['text']
    review['features'] = []
    sentences = sent_tokenize(text)
    for sentence in sentences:
        if not contains_keyword(sentence):
            continue
        try:
            root = next(parser.raw_parse(sentence))
            nodes = root.subtrees()
            for n in nodes:
                # case 1
                if n.label() == 'S':
                    comp = []
                    for i in range(len(n)):
                        if n[i].label() == 'NP' and n[i].height() == 3:
                            np = ' '.join(n[i].leaves())
                            for key, val in components.iteritems():
                                for v in val:
                                    if get_regex(v).search(np.lower()):
                                        comp.append(key)
                    if len(comp) > 0:
                        v_review = u' '.join(convert_PRN(n.leaves())).encode('utf-8').strip()
                        compound = sid.polarity_scores(v_review)['compound']
                        feature = {
                            'name': '',
                            'description': v_review,
                            'polarity_score': compound
                        }
                        for c in comp:
                            feature['name'] = c
                            review['features'].append(feature)
                            components_counter[c] += 1
                # case 2
                if n.label() == 'NP':
                    np_child = False
                    whnp_child = False
                    comp = []
                    for i in range(len(n)):
                        if n[i].label() == 'NP' and n[i].height() == 3:
                            np = ' '.join(n[i].leaves())
                            for key, val in components.iteritems():
                                for v in val:
                                    if get_regex(v).search(np.lower()):
                                        comp.append(key)
                                        np_child = True
                        elif n[i].label() == 'SBAR':
                            for j in range(len(n[i])):
                                if n[i][j].label() == 'WHNP':
                                    whnp_child = True
                    if np_child and whnp_child:
                        v_review = u' '.join(convert_PRN(n.leaves())).encode('utf-8').strip()
                        compound = sid.polarity_scores(v_review)['compound']
                        feature = {
                            'name': '',
                            'description': v_review,
                            'polarity_score': compound
                        }
                        for c in comp:
                            feature['name'] = c
                            review['features'].append(feature)
                            components_counter[c] += 1
                # case 3
                if n.label() == 'NP':
                    np_child = False
                    whnp_child = False
                    comp = []
                    for i in range(len(n)):
                        if n[i].label() == 'NP' and n[i].height() == 3:
                            np = ' '.join(n[i].leaves())
                            for key, val in components.iteritems():
                                for v in val:
                                    if get_regex(v).search(np.lower()):
                                        comp.append(key)
                                        np_child = True
                        elif n[i].label() == 'PRN' and len(n[i]) == 3:
                            if n[i][1].label() == 'SBAR':
                                for j in range(len(n[i][1])):
                                    if n[i][1][j].label() == 'WHNP':
                                        whnp_child = True
                    if np_child and whnp_child:
                        v_review = u' '.join(convert_PRN(n.leaves())).encode('utf-8').strip()
                        compound = sid.polarity_scores(v_review)['compound']
                        feature = {
                            'name': '',
                            'description': v_review,
                            'polarity_score': compound
                        }
                        for c in comp:
                            feature['name'] = c
                            review['features'].append(feature)
                            components_counter[c] += 1
                # case 4
                if n.label() == 'NP':
                    comp = []
                    adjp_child = False
                    for i in range(len(n)):
                        if n[i].label() == 'ADJP':
                            adjp_child = True
                    if adjp_child:
                        np = ' '.join(n.leaves())
                        for key, val in components.iteritems():
                            for v in val:
                                if get_regex(v).search(np.lower()):
                                    comp.append(key)
                    if len(comp) > 0:
                        v_review = u' '.join(convert_PRN(n.leaves())).encode('utf-8').strip()
                        compound = sid.polarity_scores(v_review)['compound']
                        feature = {
                            'name': '',
                            'description': v_review,
                            'polarity_score': compound
                        }
                        for c in comp:
                            feature['name'] = c
                            review['features'].append(feature)
                            components_counter[c] += 1
        except Exception as e:
            print(e.message)

    if len(review['features']) > 0:
        feature_reviews.append(review)

print(components_counter)

with open('../data/post-nlp/reviews-with-features.json', 'w') as f:
    f.write(json.dumps(feature_reviews, indent=4))
