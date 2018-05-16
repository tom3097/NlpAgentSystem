import scrapy

AMAZON_URL = 'https://www.amazon.com'
MAX_PRODUCT_COUNT = 1000
THRESHOLD_REVIEW_COUNT = 100


class ProductIdsSpider(scrapy.Spider):
    name = 'product_ids'
    user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36'
    start_urls = [
        'https://www.amazon.com/s/ref=sr_st_featured-rank?lo=none&rh=n%3A172282%2Cn%3A!493964%2Cn%3A541966%2Cn%3A13896617011%2Cn%3A565108%2Cn%3A13896609011&qid=1526498705&sort=featured-rank'
    ]
    total = 0

    def parse(self, response):
        ids = response.xpath('//li/@data-asin').extract()
        review_counts = [
            int(count.replace(',', '')) for count in
            response.xpath('//a[re:test(@href, ".*#customerReviews$")]/text()').extract()
        ]

        if len(ids) != len(review_counts):
            self.logger(
                'lenghts are different, %s, ids: %s != review_counts %s',
                response.url, len(ids), len(review_counts)
            )
        for i, _ in enumerate(ids):
            try:
                product = {
                    'product_id': ids[i],
                    'review_counter': review_counts[i]
                }
                if product['review_counter'] >= THRESHOLD_REVIEW_COUNT:
                    self.total += 1

                yield product
            except IndexError:
                break
        next_page_url = response.xpath('//a[@id="pagnNextLink"]/@href').extract_first()
        if next_page_url is not None and self.total < MAX_PRODUCT_COUNT:
            yield response.follow(AMAZON_URL + next_page_url, self.parse)
