# Build the img and push to gcr
```
docker build -t tinyurl:latest .

docker tag tinyurl:latest gcr.io/useful-art-450916-u3/tinyurl:latest

gcloud auth configure-docker

docker push gcr.io/useful-art-450916-u3/tinyurl:latest 


```