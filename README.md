onplatforms.net
==============

### prepare

`sudo docker run -p 11211:11211 -p 11212:11211 -d memcached:alpine`

### build

#### sbt

```
sbt assembly
```

#### docker build
```
sudo docker build -f docker/Dockerfile .
```
```
sudo docker build --build-arg APP_VERSION=<APP_VERSION> -f docker/Dockerfile .
```
run
```
sudo docker run -d -p 9090:9090 -e APP_PROFILE=www -e APP_VERSION=<APP_VERSION> <image-id>
```

#### use deploy script
```
curl -s https://s3-ap-northeast-1.amazonaws.com/public.onplatforms.net/onplatforms.net/zero/scripts/deploy.sh | sh -s -- <APP_PROFILE> <LISTEN_PORT> <APP_VERSION>
```