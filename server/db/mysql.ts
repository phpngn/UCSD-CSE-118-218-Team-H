import mysql from "mysql2/promise";
import dotenv from "dotenv";

dotenv.config();

export default class DB {
    connection: mysql.Connection | undefined = undefined;

    constructor() {
        mysql.createConnection({
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PW,
            database: process.env.DB_NAME,
            ssl: {
                rejectUnauthorized: false
            }
        }).then((conn) => {
            this.connection = conn;
        }).catch((err) => {
            console.error("Error connecting to database:", err);
        });
    }

    async testConnection() {
        const result = await this.connection?.query("SHOW TABLES");
        console.log("rofl", result);

    }
}