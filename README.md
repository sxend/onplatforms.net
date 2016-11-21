Platform
==============

### prepare

`sudo docker run -p 11212:11211 -d memcached:alpine`

### build

#### sbt

```
sbt assembly
```

#### docker build(in-completion)
```
sudo docker build -f docker/Dockerfile .
```