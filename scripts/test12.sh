#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

RUN="$PWD/run/1.12.2"

rm -rf "$RUN/mods"
mkdir -p "$RUN/mods"

"$PWD/gradlew" :1.12.2:clean :1.12.2:build && cp -f 1.12.2/build/libs/MatterLink-1.12.2-*-dev.jar "$RUN/mods"

cd "$RUN"

curl -o forge-installer.jar "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12.2-14.23.4.2705/forge-1.12.2-14.23.4.2705-installer.jar"
curl -L -o "$RUN/mods/Forgelin.jar" "https://minecraft.curseforge.com/projects/shadowfacts-forgelin/files/2573311/download"

$DIR/scripts/start.sh


