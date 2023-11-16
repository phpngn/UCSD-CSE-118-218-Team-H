import mysql from "mysql2";
import dotenv from "dotenv";

dotenv.config();

export default mysql.createConnection({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PW,
    database: process.env.DB_NAME,
});