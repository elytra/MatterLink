#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

RUN="$PWD/run/1.7.10"

rm -rf "$RUN/mods"
mkdir -p "$RUN/mods"

"$PWD/gradlew" :1.7.10:clean :1.7.10:build && cp -f $DIR/1.7.10/build/libs/MatterLink-1.7.10-*-dev.jar "$RUN/mods"
if [ ! $? -eq 0 ]; then
    echo "Error compiling matterlink"
    exit 1
fi

cd "$RUN"

curl -o forge-installer.jar "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-installer.jar"

$DIR/scripts/start.sh
