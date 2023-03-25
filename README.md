# RediSentinel with Jedis


## Important Links/Notes
- [Jedis and Spring version](https://stackoverflow.com/questions/72194259/is-it-possible-to-use-the-newest-jedis-in-spring-project)
- [Spring pull request including Jedis 4.x](https://github.com/spring-projects/spring-data-redis/pull/2287)
- [GitHub deploying this java application with redis enterprise on AWS](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)

### Deploy redis enterprise
This github is about using redis sentinel with Redis Enterprise.  Within the code is a [Jedis spring sentinel connection](https://github.com/jphaugla/redisSentinel/blob/main/src/main/java/com/redis/sentinel/config/RedisConfig.java) and a [Jedis non-spring jedis connection](https://github.com/jphaugla/redisSentinel/blob/main/src/main/java/com/redis/sentinel/service/RediSearchService.java#L76).  The non-spring connection is used for doing the redisjson commands.  To deploy redis enterprise on AWS, use [this github](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)
In this github, a database is also created with redis search and redis json deployed.  json is needed if the environment variable WRITE_JSON is set to true.  Search is not used in this application but if curious about search, the code is *lifted* from [my github that uses search and json](https://github.com/jphaugla/redisearchStock).

### Prepare database
Need to create an ACL for the database to be used as the login for the application.  The following steps cover doing this:
* [Configure ACLs](https://docs.redis.com/latest/rs/security/access-control/configure-acl/)
* Create Roles](https://docs.redis.com/latest/rs/security/access-control/create-roles/)
* [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/)

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
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
```bash
source ../scripts/app.env
java -jar target/redisentinel-0.0.1-SNAPSHOT.jar
```

### What happens
When the code starts the redis enterprise endpoint (environment variable is *REDIS_HOST*) is used for the server with the redis enterprise sentinel port of 8100.  This is log from the code as each of the sentinal masters is resolved:
```bash
2023-03-24T16:48:13.771-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-03-24T16:48:14.010-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 54.241.107.136:12128, starting Sentinel listeners...
2023-03-24T16:48:14.015-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 54.241.107.136:12128
2023-03-24T16:48:14.094-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : Init RediSearchService
2023-03-24T16:48:14.095-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : redisPassword is jasonrocks
2023-03-24T16:48:14.097-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-03-24T16:48:14.252-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 54.241.107.136:12128, starting Sentinel listeners...
2023-03-24T16:48:14.253-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 54.241.107.136:12128
2023-03-24T16:48:14.254-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : looging in using username jph
``` 

