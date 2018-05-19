import json
from collections import Counter

PRODUCTS_COUNT = 100


def main():
    with open('data/pre-nlp/all_reviews.json') as fr:
        all_reviews = json.loads(fr.read())

    all_reviews.sort(key=lambda r: r['product_id'])
    counter = Counter([r['product_id'] for r in all_reviews])
    ids = [c[0] for c in counter.most_common(PRODUCTS_COUNT)]
    best_reviews = [r for r in all_reviews if r['product_id'] in ids]
    with open('data/pre-nlp/reviews.json', 'w') as fw:
        fw.write(json.dumps(best_reviews))


if __name__ == '__main__':
    main()
