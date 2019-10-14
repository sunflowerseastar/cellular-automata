#!/bin/sh

rsync -ruv -e ssh resources/public/*.* sfss:/var/www/sunflowerseastar-subdomains/cellular-automata/html
rsync -ruv -e ssh resources/public/css/*.* sfss:/var/www/sunflowerseastar-subdomains/cellular-automata/html/css
rsync -ruv -e ssh resources/public/cljs-out/*.* sfss:/var/www/sunflowerseastar-subdomains/cellular-automata/html/cljs-out
