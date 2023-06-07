apt-get update && apt-get install -y git
apt install -y openjdk-17-jdk
java --version
git clone https://github.com/joosing/fireball.git
cd fireball
chmod 744 mvnw
./mvnw clean package -DskipTests
java -Dfireball.server.root-path="/app/files" -Dfireball.client.root-path="/app/files"  -jar ./target/fireball-0.0.1.jar &

# -XX:MaxDirectMemorySize=3g
