import json
import re
from collections import Counter

import scrapy


REVIEWS_URL = 'https://www.amazon.com/product-reviews/{}/ie=UTF8&reviewerType=all_reviews'
AMAZON_URL = 'https://www.amazon.com'
MAX_REVIEWS_COUNT_THRESHOLD = 11
with open('product_ids.json') as f:
    PRODUCTS = [
        p for p in
        json.loads(f.read())
    ]
PRODUCT_REVIEWS_START_URLS = [
    REVIEWS_URL.format(product['product_id'])
    for product in PRODUCTS
][:3]


class ReviewsSpider(scrapy.Spider):
    name = 'reviews'
    user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36'
    start_urls = PRODUCT_REVIEWS_START_URLS
    review_counter = Counter()

    def parse(self, response):
        product_id = self._get_product_id(response.url)
        reviews = response.xpath('//div[@id="cm_cr-review_list"]')
        texts = reviews.xpath('.//span[@data-hook="review-body"]/text()').extract()
        ids = reviews.xpath('./div/@id').extract()
        titles = reviews.xpath('.//a[@data-hook="review-title"]/text()').extract()
        scores = [
            self._parse_score(score) for score
            in reviews.xpath('//i[@data-hook="review-star-rating"]/span/text()').extract()
        ]
        helpful_vote_statements = [
            self._parse_vote_statement(vote_statement) for vote_statement in
            reviews.xpath('.//span[@data-hook="helpful-vote-statement"]/text()').extract()
        ]

        if len({
            len(ids), len(texts),
            len(titles), len(scores),
            # len(helpful_vote_statements)
        }) > 1:
            self.logger.error(
                'lengths are different, skipping this page. %s'
                'ids: %s, texts: %s, titles: %s, scores: %s',
                response.url, len(ids), len(texts), len(titles), len(scores)
            )
        else:
            for i, _ in enumerate(ids):
                self.review_counter[product_id] += 1
                self.logger.info(
                    '%s: %s', product_id, str(self.review_counter[product_id])
                )
                yield {
                    'product_id': product_id,
                    'id': ids[i],
                    'title': titles[i],
                    'text': texts[i],
                    'score': scores[i],
                    # 'helpful_vote_statement': helpful_vote_statements[i]
                }

        next_page_url = response.css('ul.a-pagination').xpath('.//a/@href')[-1].extract()
        if (
                next_page_url is not None
                and self.review_counter[product_id] < MAX_REVIEWS_COUNT_THRESHOLD
        ):
            yield response.follow(AMAZON_URL + next_page_url, self.parse)

    def _get_product_id(self, url):
        regex = re.search(r'/product-reviews/(.*)/', url)
        if not regex:
            return None
        return regex.group(1)

    def _parse_score(self, score):
        if len(score) < 3:
            return 0.0
        return float(score[:3])

    def _parse_vote_statement(self, vote_statement):
        if not vote_statement:
            return 0
        vote_statement = vote_statement.replace(',', '')
        regex = re.search(r'(^\d+)', vote_statement)
        if not regex:
            return 0
        return int(regex.group(1))



