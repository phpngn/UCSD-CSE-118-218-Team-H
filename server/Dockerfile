FROM node:slim

WORKDIR /app
COPY package.json ./
COPY package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

WORKDIR /app/dist
COPY db/schema.sql ./db/schema.sql
EXPOSE 3000
CMD ["npm", "start"]
