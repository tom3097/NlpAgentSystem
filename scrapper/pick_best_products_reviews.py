import json
from collections import Counter

PRODUCTS_COUNT = 100


def main():
    with open('reviews.json') as fr:
        reviews = json.loads(fr.read())

    reviews.sort(key=lambda r: r['product_id'])
    counter = Counter([r['product_id'] for r in reviews])
    ids = [c[0] for c in counter.most_common(PRODUCTS_COUNT)]
    best_products_reviews = [r for r in reviews if r['product_id'] in ids]
    with open('best_products_reviews.json', 'w') as fw:
        fw.write(json.dumps(best_products_reviews))


if __name__ == '__main__':
    main()
