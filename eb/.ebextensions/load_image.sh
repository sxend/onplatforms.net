#!/bin/bash

aws s3 cp s3://internal-storage.onplatforms.net/zero/$1/containers/zero.tar.gz - | sudo docker load
