# NlpAgentSystem
## scrapper
### Installation
* create new python >=3.5 virtualenv
* cd scrapper
* pip install -r requirements.txt
### Fetch product ids
Will fetch product ids and review_count, sometimes it makes mistakes with review count 
(it happened for one product out of 150 products).
* scrapy runspider fetch_product_ids.py --logfile product_ids.log -o product_ids.json
### Fetch reviews 
Will fetch 150 products with most reviews, up to 100 reviews per product 
(can be changed in static variables).
* scrapy runspider fetch_reviews.py --logfile reviews.log -o reviews.json

### Pick 100 best products reviews
Will pick 100 products reviews with most reviews per product 
and save it into best_products_reviews.json
* python pick_best_products_reviews.py 

All of above needs to be done one after another.