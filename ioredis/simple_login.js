import fs from "fs"
import Redis from 'ioredis'

async function ioredisDemo() {
    try {
        console.log(process.env);
        const host = process.env.REDIS_HOST;
        const port = process.env.REDIS_PORT;
        const username = process.env.REDIS_USERNAME;
        const password = process.env.REDIS_PASSWORD;

        const client = new Redis({
            host: host,
            port: port,
            username: username,
            password: password,
            tls: {
                // Refer to `tls.connect()` section in
                // https://nodejs.org/api/tls.html
                // for all supported options
                ca: fs.readFileSync("ca.crt"),
            },
        });

        /*
        const connection = `rediss://${username}:${password}@${host}:${port}`;
        console.log(`Connection: ${connection}`);
        const client = new Redis(connection);

         */

        await client.set('mykey', 'Hello from io-redis!');
        const myKeyValue = await client.get('mykey');
        console.log(`Simple login: ${myKeyValue}`);

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
