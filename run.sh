./gradlew --no-daemon uberJar --console=plain
echo "needs java version 22 or 24 or something"
echo "your path java is:"
java --version
java --enable-native-access=ALL-UNNAMED -jar build/libs/ttyper.jar

