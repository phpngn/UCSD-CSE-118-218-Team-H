import mysql from "mysql2/promise";
import dotenv from "dotenv";
import * as fs from "fs";

export type Device = {
    id?: number,
    serial: string,
    name:string
}

export type Event = {
    id?: number,
    device_id: number,
    type:string,
    alert:boolean,
    timestamp:string
}

export type Datapoint = {
    id?: number,
    event_id: number,
    sensor:string,
    value:number
}




dotenv.config();

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

    async executeMultiple(queries: string[], params: any[][] = []) {
        let resultPromises = [];
        for (let i = 0; i < queries.length; i++) {
            resultPromises.push(this.executePreparedStatement(queries[i], params[i]));
        }
        return await Promise.all(resultPromises);
    }

    async executePreparedStatement(sql: string, params: any[] = []) {
        return await this.connection?.execute(sql, params);
    }







    async initSchema() {
        const schema = fs.readFileSync("./db/schema.sql", 'utf8');
        const statements: string[] = schema.split(";");
        return await this.executeMultiple(statements)
    }





    async insertDevices(device: Device) {
        let sql = `INSERT INTO Devices (serial, name) VALUES (?,?)`;
        return await this.executePreparedStatement(sql, [device.serial,device.name]);
    }

    async insertEvent(device: Event) {
        let sql = `INSERT INTO Events (device_id, type, alert, timestamp) VALUES (?,?,?,?)`;
        let result:any =  await this.executePreparedStatement(sql, [device.device_id,device.type,device.alert,device.timestamp]);
        return result.insertId;
    }

    async insertDatapoints(datapoints: Datapoint[]) {
        let entries:{queries:string[],params:any[][]} = {queries:[], params:[]};
        for(let i = 0; i < datapoints.length; i++) {
            let datapoint = datapoints[i];
            let sql = `INSERT INTO Datapoints (event_id, sensor, value) VALUES (?,?,?)`;
            let params = [datapoint.event_id,datapoint.sensor,datapoint.value]
            entries.queries.push(sql);
            entries.params.push(params);
        }
        return await this.executeMultiple(entries.queries,entries.params);
    }




    async getDeviceBySerial(serial: string) {
        let sql = `SELECT * FROM Devices WHERE serial=? LIMIT 1`;
        let [rows,fields]:any = await this.executePreparedStatement(sql, [serial]);
        if(rows.length >= 1) {
            return rows[0] as Device;
        }
        else return undefined;
    }

}