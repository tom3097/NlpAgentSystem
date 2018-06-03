FROM nginx
COPY nginx.conf /etc/nginx/nginx.conf
WORKDIR /var/www/static
COPY static .
RUN mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.disabled
