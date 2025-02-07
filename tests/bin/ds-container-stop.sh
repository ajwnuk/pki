#!/bin/bash -e

# https://fy.blackhats.net.au/blog/html/2020/03/28/389ds_in_containers.html

NAME=$1

if [ "$NAME" == "" ]
then
    echo "Usage: ds-container-stop.sh <name>"
    exit 1
fi

echo "Stopping DS container"
docker stop $NAME > /dev/null

echo "DS container is stopped"
