#!/bin/bash
#!/usr/bin/expect -d

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

expect -c　 "
set timeout 5
spawn cd $DIR
spawn git add *
spawn git push origin $BRANCH
spawn git commit -m $MESSAGE
    expect -nocase \"Username\" {
	send \"${USERNAME}\n\"
	expect -nocase \"Password\" {
	    send \"${PASSWORD}\n\"
	    spawn sleep 3
	    spawn echo \"exit\"
        }
    }
"

