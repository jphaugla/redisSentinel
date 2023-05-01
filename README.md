# RediSentinel and TLS with multiple client libraries

## Outline

- [Overview](#overview)
- [Important Links](#important-linksnotes)
- [Deploy git](#deploy-github)
- [Deploy redis](#deploy-redis)
  - [Deploy TLS on docker](#deploy-on-docker)
  - [Deploy TLS Redis Enterprise](#deploy-redis-enterprise)
  - [Deploy Sentinel TLS on docker](#deploy-sentinel-docker)
  - [Deploy Sentinel TLS Redis Enterprise](#deploy-redis-enterprise)
- [Prepare database](#prepare-database)
- [Verify Sentinel](#verify-sentinel)
- [Jedis code](#jedis-code)
  - [Install java](#install-java)
    - [redhat](#redhat)
    - [ubuntu](#ubuntu)
  - [Compile application](#compile-application)
  - [Run](#run)
  - [What happens](#what-happens)
- [TLS](#tls)
  - [Redis Enterprise Server steps](#redis-enterprise) 

## Overview
This github shows code to connect to redis enterprise and redis-stack using sentinel and/or TLS.  Links provided in this github, show redisson sentinel with TLS as well.  Additionally, additional steps are needed on the redis enterprise server if sentinel is used with TLS-these steps are also provided.  Redis enterprise as a standalone redis can be used or a docker solution based on redis stack.   Each client tool is in a separate subdirectory with a separate README.md as the main directory holds all the docker-compose files.  This README covers the database deployment.  Trying to give as broad a set of working examples in the TLS and sentinel space with a variety of client tools such as jedis, spring jedis, lettuce, spring lettuce, python, and node.js.  


## Deploy github
```bash
get clone https://github.com/jphaugla/redisSentinel.git
```

## Important Links
- [GitHub deploying this java application with redis enterprise on AWS](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)
- [Generate a self-signed SSL certificate for IP address](https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754)
- [Redis CLI with tls](https://redis.io/docs/ui/cli)
- [TLS with redis](https://redis.io/docs/management/security/encryption/)
- [TLS with redis docker](https://blog.shahid.codes/setup-redis-with-tls-using-docker)
- [Enabling Secure Connections to Redis Enterprise](https://redis.com/blog/enabling-secure-connections-to-redis-enterprise/)
- [SSL/TLS with Redis Enterprise blog](https://tgrall.github.io/blog/2020/01/02/how-to-use-ssl-slash-tls-with-redis-enterprise/)
- [Where to install redis certiciate-springboot](https://www.appsloveworld.com/springboot/100/95/where-to-install-redis-certificate)
- [mTLS/TLS with Lettuce](https://stackoverflow.com/questions/63177538/mtls-tls-redis-6-issues-java)
- [Hostname must match for TLS](https://stackoverflow.com/questions/3093112/certificateexception-no-name-matching-ssl-someurl-de-found)
- [Got Jedis SSL code from this link](https://redis.io/docs/clients/java/)
- [Use SSL/TLS wiith Redis Enterprise](https://developer.redis.com/howtos/security/)
- [Redis Sentinel open source documentation](https://redis.io/docs/management/sentinel/)
- [Redis Sentinel on docker youtube](https://www.youtube.com/watch?v=XxR6M6XQq6I)
- [Redis Sentinel with TLS stackoverflow](https://stackoverflow.com/questions/61327471/redis-6-tls-support-and-redis-sentinel)
- [Redis spring boot with sentinel](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:sentinel)
- [ioredis with TLS](https://github.com/luin/ioredis#sentinel)
- [Redisson github including sentinel steps](https://github.com/jphaugla/Redis-Digital-Banking-redisson/)
## Deploy redis
The following sections cover running redis-stack on docker or redis enterprise in standard TLS or Sentinel with TLS.  No changes are needed with Redis Enterpise to run sentinel without TLS-works with default installation.  Each application subdirectory will link back to the appropriate section here for the type of database setup needed to support the application.  This is a work in progress as not all combinations have been tested/completed at this time.  
### Deploy on Docker
* may need to adjust environment variables in the docker compose file for the environment
* ACL is also set in the Docker environment variable section
* TLS is built into redis-stack so this was easy enough

#### Deploy redis with TLS
* Download the latest tool from redis to generate the certificates
* Start redis using docker-compose file without TLS
* Build the certificates in the redis docker image
  * since the target directory for the certificates is docker volume mounted, the generated files will be available in the github directory as well
  * these certificates will be needed by the running application.  Each application README.md will have instructions to leverage the certificates by the application
```bash
   cd redisSentinel/scripts
   ./get-gen-test-certs.sh
   cd ..
   docker-compose -f docker-compose.no-tls.yml up -d 
   docker exec -it redis bash
#  in the docker container for redis
   apt-get update
   apt-get install openssl
   cd scripts
   ./gen-test-certs.sh
   exit
#  back in github directory
   docker-compose -f docker-compose.no-tls.yml down
```
* bring up tls redis-stack
```bash
docker-compose -f docker-compose.tls.yml up -d
```

### Verify using redis-cli
edit scripts/app.env to have redis as REDIS_HOST and 6379 as REDIS_PORT
```bash
source scripts/app.env
cd redisSentinel
./redis-cli-redis.sh
```
* shutdown tls redis-stack
```bash
docker-compose -f docker-compose.tls.yml down
```

Go back to your application and get it connected to redis stack!


### Deploy redis enterprise
* In [this github](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)), a database is created with redis search and redis json deployed.  json is needed if the environment variable WRITE_JSON is set to true.  Search is not used in this application but if curious about search, the code is *lifted* from [my github that uses search and json](https://github.com/jphaugla/redisearchStock).
* This readme is following the steps from this [TLS with Redis Enterprise github](https://developer.redis.com/howtos/security/)

#### Deploy redis enterprise database without TLS
using [redis create database documentation](https://docs.redis.com/latest/rs/databases/create/), create a database.  NOTES:  don't set up for TLS yet, set port to 12000 for simplicity, add a password to the database
edit scripts/app.env to have redis enterprise  as REDIS_HOST and 12000 as REDIS_PORT
#### Verify access to the database
* edit scripts/app.env for any differences
* verify connectivity
```bash
./redis-cli-re-no-tls.sh
set jason funny
```

#### Prepare database
Need to create an ACL in redis enterprise for the database to be used as the login (sample code uses un=jph pw=jasonrocks) for the application.  The following steps cover doing this:
* [Configure ACLs](https://docs.redis.com/latest/rs/security/access-control/configure-acl/)
* [Create Roles](https://docs.redis.com/latest/rs/security/access-control/create-roles/)
* [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/)
* steps used
![](images/create_role.png)
![](images/create_user.png)

#### Verify connectivity to database
```bash
./redis-cli-re-no-tls-un.sh
```
#### Setup TLS for redis enterprise
* get the proxy cert from redis-enterprise
  * it is located in /etc/opt/redislabs/proxy_cert.pem
```bash
cd re_keys/tls
# this is assuming redis enterprise was built using above terraform script
scp -i aws_pem_file ubuntu@52.9.197.238:/etc/opt/redislabs/proxy_cert.pem .
./gen_client_cert.sh
# copy the generated cert file
pbcopy < client_cert_app_001.pem
```
##### Go to the Redis Enterprise Admin Web Console and enable TLS on your database:

* Edit the database configuration
* Check TLS
* Select "Require TLS for All communications"
* Check "Enforce client authentication"
* Paste the certificate in the text area
* Click the Save button to save the certificate
* Click the Update button to save the configuration.
![](images/re-tls.png)

#### Verify no long connect without TLS
```bash
./redis-cli-re-no-tls-un.sh
```

#### Verify can connect with TLS
NOTE:  stunnel is no longer needed so use format in  redis-cli-re-tls.sh
```bash
cd redisSentinel
./redis-cli-re-tls.sh
```
Can now go back to chosen application to connect the application to redis enterprise with TLS

## Redis Enterprise with TLS
Redis Enterprise does not need any configuration changes to work with Sentinel. However, using sentinel and TLS with Redis Enterprise becomes a bit more difficult.  The given proxy key must be replaced with a proxy key that allows traffic through the redis enterprise endpoint as well as the redis enterprise IPs.  This is needed because when sentinel (actually the Redis Enterprise Discover service) retrieves the redis enterprise server, the IPs and not the DNS name is retrieved.

The techniques described in [generate a self-signed SSL certificate for IP address github](https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754) are used below.  Review the link for more in-depth explanations.

### Redis Enterprise steps
Log into a redis enterprise node
* Check the current key using openssl to get the information for the cnf file running this on redis server
```bash
 openssl s_client -connect jphterra2.demo-rlec.redislabs.com:8443 </dev/null 2>/dev/null | openssl x509 -noout -text | grep DNS
```
* copy the ./ssl/san.cnf file up the the redis server and save in a temp dir under /opt/redislabs/bin
```bash
sudo bash
cd /opt/redislabs/bin
mkdir temp
cd temp
```
copy contents of san.cnf into a file int this temp directory
* using the DNS lines, edit the san.cnf to have correct DNS and IP addresses
* create new key and cert pem files
```bash
openssl req -x509 -nodes -days 730 -newkey rsa:2048 -keyout key.pem -out cert.pem -config san.cnf
```
Make redis server updates using rladmin and supervisorctl
```bash
cd /opt/redislabs/bin
./rladmin
rladmin> cluster config sentinel_ssl_policy allowed
Cluster configured successfully
rladmin> cluster certificate set proxy certificate_file temp/cert.pem key_file temp/key.pem
Set proxy certificate to contents of file temp/cert.pem
Set proxy key to contents of file temp/key.pem
exit
./supervisorctl restart dmcproxy
./supervisorctl restart sentinel_service
```
Verify the sentinel service has the IPs in the certificate
```bash
 openssl s_client -connect jphterra2.demo-rlec.redislabs.com:8001 </dev/null 2>/dev/null | openssl x509 -noout -text | grep IP
```

### Redis OSS steps

#### Generate certs
```bash
./get-gen-test-certs.sh
./gen-tests-certs.sh
```
 
## Verify sentinel 
Get the SENTINEL_MASTER use redis cli to connect to the sentinel (8100) port and query for the sentinel information

```bash
# using redis enterprise
[root@ip-172-16-32-11 ~]# redis-cli -p 8001 -h redis_enterprise_endpoint
# using docker
# using docker
[root@ip-172-16-32-11 ~]# redis-cli -p 26379 -h localhost
127.0.0.1:8001> SENTINEL masters
1) 1) “name”
  2) “TestDB@internal”
  3) “ip”
  4) “172.16.32.11"
  5) “port”
  6) “12000"
  7) “flags”
  8) “master”
  9) “num-other-sentinels”
  10) “0"
2) 1) “name”
  2) “TestDB”
  3) “ip”
  4) “3.239.252.137"
  5) “port”
  6) “12000"
  7) “flags”
  8) “master”
  9) “num-other-sentinels”
  10) “0"
```

go back to the redisson github for the testing of TLS with Redisson

copy this generated cert key at /opt/redislabs/bin/temp/cert.key to the connecting client in the redisson github directory
