#!/bin/sh

DIR=$(cd $(dirname $0) && pwd)

cd $DIR

bash compile.sh
bash start.sh -1 -1 -1 -1 -1 -1 localhost
