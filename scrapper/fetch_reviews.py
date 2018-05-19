import json
import re
from collections import Counter

import scrapy


REVIEWS_URL = 'https://www.amazon.com/product-reviews/{}/ie=UTF8&reviewerType=all_reviews'
AMAZON_URL = 'https://www.amazon.com'
MAX_REVIEWS_PER_PRODUCT = 100
MAX_PRODUCTS_COUNT = 150

with open('product_ids.json') as f:
    PRODUCTS = json.loads(f.read())

PRODUCTS_COUNTER = Counter({p['id']: p['reviews_count'] for p in PRODUCTS})
MOST_REVIEWED_PRODUCTS = [p[0] for p in PRODUCTS_COUNTER.most_common(MAX_PRODUCTS_COUNT)]
MOST_REVIEWED_PRODUCTS_URLS = [REVIEWS_URL.format(p) for p in MOST_REVIEWED_PRODUCTS]


class ReviewsSpider(scrapy.Spider):
    name = 'reviews'
    user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36'
    start_urls = MOST_REVIEWED_PRODUCTS_URLS
    review_counter = Counter()

    def parse(self, response):
        product_id = self._get_product_id(response.url)
        reviews_selector = response.xpath('//div[@data-hook="review"]')
        for review in reviews_selector:
            review = {
                'product_id': product_id,
                'url': response.url,
                'review_id': review.xpath('@id').extract_first(),
                'text': ' '.join(review.xpath('.//span[@data-hook="review-body"]/text()').extract()),
                'title': review.xpath('.//a[@data-hook="review-title"]/text()').extract_first(),
                'score': self._parse_score(
                    review.xpath('.//i[@data-hook="review-star-rating"]/span/text()').extract_first()
                ),
                'helpful_vote_statements': self._parse_vote_statement(
                    review.xpath('.//span[@data-hook="helpful-vote-statement"]/text()').extract_first()
                )
            }
            self.review_counter[product_id] += 1
            yield review

        pagination_selector = response.css('ul.a-pagination').xpath('.//a/@href')
        if not pagination_selector:
            yield
        else:
            next_page_url = pagination_selector[-1].extract()
            if (
                    next_page_url is not None
                    and self.review_counter[product_id] < MAX_REVIEWS_PER_PRODUCT
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



