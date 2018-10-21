#!/bin/bash

DIR=$(cd $(dirname $0) && pwd)
BRANCH=$1
MESSAGE=$2

cd $DIR

#git branch $BRANCH
#git checkout $BRANCH
git add *
git commit -m $MESSAGE
git push origin $BRANCH

echo "test"

expect -c "

"
