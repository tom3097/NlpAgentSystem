# NlpAgentSystem

## scrapper

### Installation
* create new python virtualenv
* pip install -r requirements.txt
* cd ..

### Fetch product ids

Will fetch product ids and review_count, sometimes it makes mistakes with review count 
(it happened for one product out of 150 products).
* scrapy runspider scrapper/fetch_product_ids.py --logfile data/pre-nlp/product_ids.log -o data/pre-nlp/product_ids.json
### Fetch reviews

Will fetch 150 products with most reviews, up to 100 reviews per product 
(can be changed in static variables).
* scrapy runspider scrapper/fetch_reviews.py --logfile data/pre-nlp/all_reviews.log -o data/pre-nlp/all_reviews.json

### Pick 100 best products reviews

Will pick 100 products reviews with most reviews per product 
and save it into data/pre-nlp/reviews.json
* python scrapper/pick_best_products_reviews.py 

All of above needs to be done one after another.

Data after being processed by nlp should go to data/post-nlp.