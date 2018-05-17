import scrapy

AMAZON_URL = 'https://www.amazon.com'
MAX_PRODUCT_COUNT = 1000


class ProductIdsSpider(scrapy.Spider):
    name = 'product_ids'
    user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36'
    start_urls = [
        'https://www.amazon.com/s/ref=sr_st_featured-rank?lo=none&rh=n%3A172282%2Cn%3A!493964%2Cn%3A541966%2Cn%3A13896617011%2Cn%3A565108%2Cn%3A13896609011&qid=1526498705&sort=featured-rank'
    ]

    def parse(self, response):
        products_selector = response.xpath('//li[re:test(@id, "^result_(\d+)$")]')
        for product in products_selector:
            yield {
                'id': product.xpath('@data-asin').extract_first(),
                'reviews_count': self._parse_reviews_count(
                    product.xpath('.//a[re:test(@href, ".*#customerReviews$")]/text()').extract_first()
                ),
                'url': response.url
            }

        next_page_url = response.xpath('//a[@id="pagnNextLink"]/@href').extract_first()
        if next_page_url is not None:
            yield response.follow(AMAZON_URL + next_page_url, self.parse)

    def _parse_reviews_count(self, count):
        try:
            return int(count)
        except (ValueError, TypeError):
            self.logger.error('could not parse: %s.', count)
            return 0
