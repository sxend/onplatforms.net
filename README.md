onplatforms.net
==============

### build

#### sbt

```
sbt assembly
```

#### docker build

```
sudo docker build \
    --build-arg APP_VERSION=<APP_VERSION> \
    --build-arg LISTEN_PORT=<LISTEN_PORT> \
    --build-arg APP_PROFILE=<APP_PROFILE> -f docker/Dockerfile .
```

run
```
sudo docker run --name=${APP_PROFILE}.onplatforms.net -d -it \
    -p <LISTEN_PORT>:<LISTEN_PORT> \
    -e RDB_HOST=<RDB_HOST> \
    -e RDB_USER=<RDB_USER> \
    -e RDB_PASS=<RDB_PASS> \
    -e PASSWORD_HASH_SALT=<PASSWORD_HASH_SALT> \
    <image-id>
```

#### use deploy script
```
curl -s https://s3-ap-northeast-1.amazonaws.com/public.onplatforms.net/onplatforms.net/app/scripts/deploy.sh | sh -s -- <APP_PROFILE> <LISTEN_PORT> <APP_VERSION>
```