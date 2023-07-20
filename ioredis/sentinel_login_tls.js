import fs from "fs"
import Redis from 'ioredis'

async function ioredisDemo() {
    try {
        console.log(process.env);
        const host = process.env.SENTINEL_HOST;
        const port = process.env.SENTINEL_PORT;
        const client_name = process.env.SENTINEL_MASTER;
        const username = process.env.REDIS_USERNAME;
        const password = process.env.REDIS_PASSWORD;

        const sentinelConfig = [
            { host: host, port: port },
        ];

        // Redis TLS Configuration
        const tlsOptions = {
            key: fs.readFileSync('/scripts/sentinel_tests/tls/private.key'),
            cert: fs.readFileSync('/scripts/sentinel_tests/tls/san.crt'),
            ca: fs.readFileSync('/scripts/sentinel_tests/tls/CA-cert.pem'),
            rejectUnauthorized: false, // Need this for self-signed certs. Don't want this in production
        };

        // Redis Connection Options
        const redisOptions = {
            sentinels: sentinelConfig,
            name: client_name,
            tls: tlsOptions,
        };

        console.log(`tlsOptions: ${JSON.stringify(tlsOptions)}`)

        const client = new Redis(redisOptions);

        /*
        const client = new Redis({
            sentinels: [{
                host: host,
                port: port,
            }],
            name: client_name,
            username: username,
            password: password,
            enableTLSForSentinelMode: true,
            tls: {
                // Refer to `tls.connect()` section in
                // https://nodejs.org/api/tls.html
                // for all supported options
                rejectUnauthorized: false, // Need this for self-signed certs. Don't want this in production
                key: fs.readFileSync('/scripts/sentinel_tests/tls/private.key', 'ascii'),
                cert: fs.readFileSync('/scripts/sentinel_tests/tls/san.crt', 'ascii'),
                ca: fs.readFileSync('/scripts/sentinel_tests/tls/CA-cert.pem', 'ascii'),
                showFriendlyErrorStack: true,
            },
        });

         */

        await client.set('mykey', 'Hello from io-redis Sentinel with TLS!', (error, result) => {
            if (error) {
                console.error('Error setting Redis key:', error);
            } else {
                console.log('Redis key set successfully!');
            }
        });

        const myKeyValue = await client.get('mykey', (error, result) => {
            if (error) {
                console.error('Error retrieving Redis value:', error);
            } else {
                console.log('Redis value:', result);
            }
        });
        console.log(myKeyValue);

        const numAdded = await client.zadd('vehicles', 4, 'car', 2, 'bike');
        console.log(`Added ${numAdded} items.`);

        const stream = client.zscanStream('vehicles');

        stream.on('data', (items) => {
            // items = array of value, score, value, score...
            for (let n = 0; n < items.length; n += 2) {
                console.log(`${items[n]} -> ${items[n + 1]}`);
            }
        });

        stream.on('end', async () => {
            await client.quit();
        });
    } catch (e) {
        console.error(e);
    }
}

ioredisDemo();
