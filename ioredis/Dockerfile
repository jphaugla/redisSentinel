FROM node:19

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./

RUN npm install
RUN npm i -s ioredis
# If you are building your code for production
# RUN npm ci --omit=dev

# Bundle app source
COPY . .

EXPOSE 8080
#CMD [ "node", "simple_login.js" ]
#CMD [ "node", "simple_login_tls.js" ]
CMD [ "node", "sentinel_login.js" ]
#CMD [ "node", "sentinel_login_tls.js" ]
