# RedisSentinel with Python (redis-py)

This is a very simple python app that connects to Redis and sets and gets a value

There are four options:
1. Direct Redis connection without TLS
2. With TLS
3. Connect via Sentinel without TLS
4. Connect via Sentinel with TLS

There are four docker-compose files and four corresponding Docker files.

The parent folder README has instruction how to run Redis and Sentinel in Docker, as well as how to generate the certificates. 
This app is required to run in Docker along with Redis and optionally Sentinel, 
since using Sentinel relies on IP addresses and those IP addresses aren't going to be available outside the Docker container.

To build the container(s):
```bash
docker-commpose -f docker-compose-<connection-type>.yml build
```

An example of running all the services with Sentinel and no TLS:
```bash
docker-compose -f docker-compose.no-tls.yml -f docker-compose.sentinel-no-tls.yml -f python/docker-compose-sentinel.yml up -d
```

To see the results use:
```bash
docker logs <container>
```