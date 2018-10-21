#!/bin/bash

DIR=$(cd $(dirname $0) && pwd)
USER=$1
BRANCH=$2
MESSAGE=$3

cd $DIR

git branch $USER
git checkout $BRANCH
git add *
git commit -m $MESSAGE
git push origin $BRANCH
