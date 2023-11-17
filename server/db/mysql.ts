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
}