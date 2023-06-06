# Spring Jedis Sentinel

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
This subdirectory demonstrates jedis without spring to connect to redis enterprise or redis stack using TLS and sentinel

## Important Links
see parent directory [README.md important links](https://github.com/jphaugla/redisSentinel#important-links)

## Jedis code
This github demonstrates jedis without spring sentinel to connect to redis enterprise using TLS.  

## Using redis-stack  
Redis stack (rs) has redis modules built in
### Deploy redis stack
see parent directory [README.md deploy on docker](https://github.com/jphaugla/redisSentinel#deploy-on-docker)
### Create trust and key store-rs
the java application needs a keystore and trustore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
```bash
cd redisSentinel/sentinel-jedis
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
docker-compose -f docker-compose.redis-sentinel-keys.sh -f docker-compose.sentinel-tls.sh -f sentinel-jedis/docker-compose-jedis-app.yml up -d
docker logs jedis-app
```
logs should come back with the word *bar*

### Shut down docker
```bash
docker-compose -f docker-compose.redis-sentinel-keys.sh -f docker-compose.sentinel-tls.sh -f sentinel-jedis/docker-compose-jedis-app.yml down
```

## using redis-enterprise

### Deploy redis enterprise
Do this in two pieces, first verify sentinel is working without TLS by following [these steps in the parent README]( https://github.com/jphaugla/redisSentinel/tree/main#deploy-redis-enterprise-database-without-tls)

### Run application without docker

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
#### Run
edit the [app.env](scripts/app.env) appropriately for desires and environment.  Note example values for docker or redis enterprise
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
```bash
source scripts/app.env
target/jedisSentinelTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar
```
* should see the word "bar" returned


#### Enable Sentinel TLS on redis enterprise
[Setup TLS sentinel redis enterprise](https://github.com/jphaugla/redisSentinel/tree/main#setup-tls-for-redis-enterprise)

### Create trust and key store
The java application needs a keystore and truststore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
Edit scripts/app.env for this environment
```bash
cd redisSentinel/sentinel-jedis
source scripts/app.env
./generatekeystore-RE.sh
./importkey-RE.sh
```
#### Run
edit the [app.env](scripts/app.env) appropriately for desires and environment.  Note example values for docker or redis enterprise
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
```bash
source scripts/app.env
target/jedisSentinelTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar
```
* should see the word "bar" returned

