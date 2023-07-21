const Redis = require('ioredis');

// Redis Sentinel Configuration
const sentinelConfig = [
    { host: 'sentinel1.example.com', port: 26379 },
    { host: 'sentinel2.example.com', port: 26379 },
    { host: 'sentinel3.example.com', port: 26379 }
];

// Redis TLS Configuration
const tlsOptions = {
    key: fs.readFileSync('client-key.pem'),
    cert: fs.readFileSync('client-cert.pem'),
    ca: fs.readFileSync('ca-cert.pem'),
};

// Redis Connection Options
const redisOptions = {
    sentinels: sentinelConfig,
    name: 'mymaster', // Replace with your Redis master name
    tls: tlsOptions,
};

// Create a new Redis client
const redisClient = new Redis(redisOptions);

// Example: Perform Redis operations
redisClient.set('mykey', 'myvalue', (error, result) => {
    if (error) {
        console.error('Error setting Redis key:', error);
    } else {
        console.log('Redis key set successfully!');
    }
});

// Example: Retrieve Redis value
redisClient.get('mykey', (error, result) => {
    if (error) {
        console.error('Error retrieving Redis value:', error);
    } else {
        console.log('Redis value:', result);
    }
});

// Example: Subscribe to a Redis channel
const redisSubscriber = new Redis(redisOptions);
redisSubscriber.subscribe('mychannel', (error, count) => {
    if (error) {
        console.error('Error subscribing to Redis channel:', error);
    } else {
        console.log('Subscribed to Redis channel!');
    }
});

// Handle incoming messages from the subscribed Redis channel
redisSubscriber.on('message', (channel, message) => {
    console.log(`Received message from Redis channel '${channel}': ${message}`);
});

// Cleanup: Close Redis connections when done
redisClient.quit();
redisSubscriber.quit();
