#!/bin/sh

build=0

show_help() {
  echo "./run.sh to run"
  echo "./run.sh -b to build and run"
}

while getopts "h?b" opt; do
  case "$opt" in
    h)
      show_help
      exit 0
      ;;
    b)  build=1
      ;;
  esac
done

if [ $build -eq 1 ]; then
  ./gradlew --no-daemon uberJar --console=plain
fi

java --enable-native-access=ALL-UNNAMED -jar build/libs/ttyper.jar

