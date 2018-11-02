#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

RUN="$PWD/run/1.9.4"

rm -rf "$RUN/mods"
mkdir -p "$RUN/mods"

"$PWD/gradlew" :1.9.4:clean :1.9.4:build && cp -f 1.9.4/build/libs/MatterLink-1.9.4-*-dev.jar "$RUN/mods"

cd "$RUN"

curl -o forge-installer.jar "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.9.4-12.17.0.2051/forge-1.9.4-12.17.0.2051-installer.jar"
curl -L -o "$RUN/mods/Forgelin.jar" "https://minecraft.curseforge.com/projects/shadowfacts-forgelin/files/2505090/download" # "https://minecraft.curseforge.com/projects/shadowfacts-forgelin/files/2573311/download"

$DIR/scripts/start.sh


