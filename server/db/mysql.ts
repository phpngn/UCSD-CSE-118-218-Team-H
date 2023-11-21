import mysql from "mysql2/promise";
import dotenv from "dotenv";
import * as fs from "fs";

dotenv.config();


export type QueryResult = {
    success:boolean,
    rows:object[],
    fields:object[]
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
        const statements:string[] = schema.split(";");
        return await this.executeMultiple(statements)
    }

    async executeMultiple(queries:string[],params:any[][] = []) {
        let res:QueryResult[] = [];
        for(let i =0; i < queries.length; i++){
            res.push(await this.executePreparedStatement(queries[i],params[i]));
        }
        return res;
    }

    async executePreparedStatement(sql:string, params:any[] = []) {
        try {
            let [rows, fields]:any = await this.connection?.execute(sql,params)
            let res:QueryResult = {success:true,rows:rows, fields:fields};
            return res;
        } catch (error) {
            console.error(error);
            let res:QueryResult = {success:false, rows:[], fields:[]};
            return res;
        }
    }

    async insertHeartRateData(data:any) {
        let sql = `INSERT INTO heartrate (timestamp, heartrate) VALUES (?,?)`;
        return await this.executePreparedStatement(sql,[data.timestamp,data.heartrate]);
    }

    async insertBloodOxygenData(data:any) {
        let sql = `INSERT INTO bloodoxygen (timestamp, bloodoxygen) VALUES (?,?)`;
        return await this.executePreparedStatement(sql,[data.timestamp,data.bloodoxygen]);
    }

    async insertFallEventData(data:any) {
        let sql = `INSERT INTO falldetection (timestamp, fall) VALUES (?,?)`;
        return await this.executePreparedStatement(sql,[data.timestamp,data.fall]);
    }

    async insertNotification(data:any) {
        let sql = `INSERT INTO notification (timestamp, message) VALUES (?,?)`;
        return await this.executePreparedStatement(sql,[data.timestamp,data.message]);
    }

        
}