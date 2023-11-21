import mysql from "mysql2/promise";
import dotenv from "dotenv";
import * as fs from "fs";

dotenv.config();


export type QueryResult = {
    rows: object[],
    fields: object[]
}
export default class DB {
    connection: mysql.Connection | undefined = undefined;
    constructor() {
        this.connection = mysql.createPool({
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PW,
            database: process.env.DB_NAME,
            ssl: {
                rejectUnauthorized: false
            }
        });
    }

    async initSchema() {
        const schema = fs.readFileSync("./db/schema.sql", 'utf8');
        const statements: string[] = schema.split(";");
        return await this.executeMultiple(statements)
    }

    async executeMultiple(queries: string[], params: any[][] = []) {
        let resultPromises: Promise<QueryResult>[] = [];
        for (let i = 0; i < queries.length; i++) {
            resultPromises.push(this.executePreparedStatement(queries[i], params[i]));
        }
        Promise.all(resultPromises);
        return resultPromises;
    }

    async executePreparedStatement(sql: string, params: any[] = []) {
        let [rows, fields]: any = await this.connection?.execute(sql, params)
        let res: QueryResult = { rows: rows, fields: fields };
        return res;
    }

    async insertHeartRateData(data: any) {
        let sql = `INSERT INTO HeartRate (timestamp, value, device_id) VALUES (?,?,?)`;
        let resultPromises: Promise<QueryResult>[] = [];
        for (let i = 0; i < data.values.length; i++) {
            resultPromises.push(this.executePreparedStatement(sql, [data.measurements[i].timestamp, data.measurements[i].value, data.device_id]));
        }
        return await Promise.all(resultPromises);
    }

    async insertBloodOxygenData(data: any) {
        let sql = `INSERT INTO BloodOxygen (timestamp, value, device_id) VALUES (?,?,?)`;
        let resultPromises: Promise<QueryResult>[] = [];
        for (let i = 0; i < data.length; i++) {
            console.log(data[i])
            resultPromises.push(this.executePreparedStatement(sql, [data.measurements[i].timestamp, data.measurements[i].value, data.device_id]));
        }
        return await Promise.all(resultPromises);
    }

    async insertFallEventData(data: any) {
        let sql = `INSERT INTO FallEvents (timestamp, event_type, device_id) VALUES (?,?,?)`;
        let resultPromises: Promise<QueryResult>[] = [];
        for (let i = 0; i < data.length; i++) {
            resultPromises.push(this.executePreparedStatement(sql, [data[i].timestamp, data[i].event_type, data[i].device_id]));
        }
        return await Promise.all(resultPromises);
    }

    async insertNotification(data: any) {
        let sql = `INSERT INTO notification (timestamp, message, device_id) VALUES (?,?,?)`;
        let resultPromises: Promise<QueryResult>[] = [];
        for (let i = 0; i < data.length; i++) {
            resultPromises.push(this.executePreparedStatement(sql, [data[i].timestamp, data[i].message, data[i].device_id]));
        }
        return await Promise.all(resultPromises);
    }



}