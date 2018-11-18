#!/usr/bin/env bash

java -jar forge-installer.jar --installServer

FORGE_FILE=`grep "The server installed successfully, you should now be able to run the file " forge-installer.jar.log | tail -1`
FORGE_FILE=${FORGE_FILE#"The server installed successfully, you should now be able to run the file "}
echo FORGE_FILE

cp -f "$FORGE_FILE" forge.jar
if [ ! $? -eq 0 ]; then
    echo "Error installing forge"
    exit 1
fi

echo "installed forge"

cp ../eula.txt .

mkdir -p config
rm -rf config/matterlink
cp -r ../matterlink_config config/matterlink

java -jar forge.jar