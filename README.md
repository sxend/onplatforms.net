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
sudo docker build --build-arg APP_VERSION=b15-ac95f2e05e109eb41ae0afa556c183ee8aa9a215 -f docker/Dockerfile .
```
run
```
sudo docker run -d -e MAIN_CLASS=net.onplatforms.platform.www.WwwSystem <image-id>
```
