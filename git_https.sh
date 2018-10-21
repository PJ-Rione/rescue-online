#!/bin/bash

DIR=$(cd $(dirname $0) && pwd)
BRANCH=$1
MESSAGE=$2
PASSWORD=$4
USERNAME=$3
cd $DIR

#git branch $BRANCH
#git checkout $BRANCH
git add *
git commit -m $MESSAGE

expect -c "
spawn git push origin $BRANCH
    expect -nocase \"Username\" {
	send \"$USERNAME\"
	    expect -nocase \"Password\" {
	    send \"$PASSWORD\"
        }
    }
}
"
echo "exit"
