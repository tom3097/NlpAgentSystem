from neuralcoref import Coref
import json


def main():
    with open('data/pre-nlp/reviews.json') as fr:
        reviews = json.load(fr)

    coref = Coref()
    progress = 1
    coref_reviews = []
    for review in reviews:
        text = review['text']
        try:
            coref.one_shot_coref(utterances=text)
        except Exception as e:
            print e.message
            continue
        review['text'] = coref.get_resolved_utterances()[0]
        coref_reviews.append(review.copy())
        print progress
        progress += 1

    with open('data/pre-nlp/reviews-after-coreference.json', 'w') as fw:
        fw.write(json.dumps(coref_reviews))


if __name__ == '__main__':
    main()