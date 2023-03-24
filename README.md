# RediSentinel with Jedis


## Important Links/Notes
- [Jedis and Spring version](https://stackoverflow.com/questions/72194259/is-it-possible-to-use-the-newest-jedis-in-spring-project)
- [Spring pull request including Jedis 4.x](https://github.com/spring-projects/spring-data-redis/pull/2287)
- [GitHub deploying this java application with redis enterprise on AWS](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)

### Deploy redis enterprise
This github is about using redis sentinel with Redis Enterprise.  To deploy redis enterprise on AWS, use [this github](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)
In this github, a database is also created with redis search and redis json deployed.  json is needed if the environment variable WRITE_JSON is set to true.
### Install Java
#### on redhat
  * install java 
  * set java home
```bash
sudo yum install java-17-openjdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```
  * download and install maven following [these steps](https://linuxize.com/post/how-to-install-apache-maven-on-centos-7) - NOTE:  yum installs older version
  * this worked with java 17
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

#### on ubuntu
```bash
mkdir binaries
cd binaries
apt install openjdk-18-jdk openjdk-18-jre
cat <<EOF | sudo tee /etc/profile.d/jdk18.sh
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk-amd64
EOF
```
  * download and install maven flollowing [these steps](https://phoenixnap.com/kb/install-maven-on-ubuntu)  Note:  apt-get installs older version
### Compile application
```bash
mvn clean package
```

### Set Environment and Run
edit the [app.env](../scripts/app.env) appropriately for desires and environment
```bash
source ../scripts/app.env
java -jar target/redisentinel-0.0.1-SNAPSHOT.jar
```
