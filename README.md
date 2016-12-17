onplatforms.net
==============

### prepare

#### migration

```bash
sbt -Dflyway.schemas=accounts.onplatforms.net \
    -Dflyway.locations="accounts/schema" \
    -Dflyway.user=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.name - ) \
    -Dflyway.password=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.pass - ) \
    flywayMigrate
```

#### production migration
```bash
sbt -Dflyway.schemas=accounts.onplatforms.net \
    -Dflyway.locations="accounts/schema" \
    -Dflyway.user=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.name - ) \
    -Dflyway.password=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.pass - ) \
    -Dsbt.RDB_HOST=xxx.xxx.xxx.xxx -Dsbt.RDB_PORT=xxxxx \
    flywayMigrate
```

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