# Simple Jedis

## Outline

- [Overview](#overview)
- [Jedis code](#jedis-code)
- [Using redis-stack](#using-redis-stack)
  - [Create trust and key store](#create-trust-and-key-store-rs)
  - [Build docker application](#build-docker-application-rs)
  - [Run docker application](#run-docker-application)
- [Using redis enterprise](#using-redis-enterprise)
  - [Create trust and key store](#create-trust-and-key-store-rs)
  - [Build docker application](#build-docker-application-rs)
  - [Run docker application](#run-docker-application-rs)
- [Run application outside of docker](#run-application-outside-of-docker)
  - [Install java](#install-java)
    - [redhat](#redhat)
    - [ubuntu](#ubuntu)
  - [Compile application](#compile-application)
  - [Run](#run)
- [Shutdown](#shutdown)
## Overview
This github shows simple jedis code (not spring version) to connect to redis enterprise or redis stack using TLS

## Important Links
see parent directory [README.md important links](https://github.com/jphaugla/redisSentinel#important-links)

## Jedis code
This github shows simple jedis code (not spring version) to connect to redis enterprise using TLS.  This is only a simple connection test to print out a single word *bar* of output to verify the TLS connection from Jedis is used.

## Using redis-stack
Redis stack (rs) has redis modules built in
### Deploy redis stack
see parent directory [README.md deploy on docker](https://github.com/jphaugla/redisSentinel#deploy-on-docker)
### Create trust and key store-rs
the java application needs a keystore and truststore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory

If it doesn't already exist:
```bash
mkdir -p src/main/resources/tls
```
```bash
cd redisSentinel/simple-jedis
export TRUSTSTORE_PASSWORD=jasonrocks
export KEYSTORE_PASSWORD=jasonrocks
./generatekeystore.sh
./importkey.sh
```
### Build docker application-rs
```bash
docker-compose -f docker-compose-jedis-app.yml build
```

### Run docker application-rs
```bash
# back to parent directory
cd ..
docker-compose -f docker-compose.tls.yml -f simple-jedis/docker-compose-jedis-app.yml up -d
docker logs jedis-app
```
jedis app logs should say *bar*

## using redis-enterprise

### Deploy redis enterprise
see parent directory [README.md deploy on redis enterprise](https://github.com/jphaugla/redisSentinel#deploy-redis-enterprise)

### Create trust and key store
The java application needs a keystore and truststore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
```bash
cd redisSentinel/simple-jedis
source app.env
./generatekeystore-RE.sh
./importkey-RE.sh
```
### Use Docker for application
#### Build docker application
```bash
docker-compose -f docker-compose-jedis-app-re.yml build
```

#### Run docker application
```bash
# back to parent directory
cd ..
docker-compose -f simple-jedis/docker-compose-jedis-app-re.yml up -d
docker logs jedis-app
```

### Shutdown
```bash
docker-compose  -f simple-jedis/docker-compose-jedis-app-re.yml down
```
### Run application outside of docker
#### Install Java
* redhat
  * install java
  * set java home
```bash
sudo yum install java-18-openjdk
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk
```
  * download and install maven following [these steps](https://linuxize.com/post/how-to-install-apache-maven-on-centos-7) - NOTE:  yum installs older version
  * this worked with java 18
```bash
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

* ubuntu
```bash
mkdir binaries
cd binaries
apt install openjdk-18-jdk openjdk-18-jre
cat <<EOF | sudo tee /etc/profile.d/jdk18.sh
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk-amd64
EOF
```
  * download and install maven following [these steps](https://phoenixnap.com/kb/install-maven-on-ubuntu)  Note:  apt-get installs older version
#### Compile application
```bash
mvn clean package
```

##### Run
edit the [app.env](scripts/app.env) appropriately for desires and environment...
* example values for redis enterprise in app.env
* have only tried running the application outside of docker with redis enterprise, not with redis-stack.  Guessing there would need to be changed in the key generation for the hostname.

```bash
source scripts/app.env
java -jar target/jedisTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar
```

