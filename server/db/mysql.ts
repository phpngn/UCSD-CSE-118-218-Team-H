import mysql from "mysql2/promise";
import dotenv from "dotenv";
import * as fs from "fs";

export type Device = {
    id?: number,
    serial: string,
    name: string
}

export type Event = {
    id?: number,
    device_id: number,
    type: string,
    alert: boolean,
    timestamp: string
}

export type Datapoint = {
    id?: number,
    event_id: number,
    sensor: string,
    value: number
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
        let result: any = await this.executePreparedStatement(sql, [device.serial, device.name]);
        return result.insertId;
    }

    async insertEvent(device: Event): Promise<number> {
        let sql = `INSERT INTO Events (device_id, type, alert, timestamp) VALUES (?,?,?,?)`;
        let result: any = await this.executePreparedStatement(sql, [device.device_id, device.type, device.alert, device.timestamp]);
        console.log(result)
        return result[0].insertId;
    }

    async insertDatapoints(datapoints: Datapoint[]) {
        let entries: { queries: string[], params: any[][] } = { queries: [], params: [] };
        for (let i = 0; i < datapoints.length; i++) {
            let datapoint = datapoints[i];
            let sql = `INSERT INTO Datapoints (event_id, sensor, value) VALUES (?,?,?)`;
            let params = [datapoint.event_id, datapoint.sensor, datapoint.value]
            entries.queries.push(sql);
            entries.params.push(params);
        }
        return await this.executeMultiple(entries.queries, entries.params);
    }

    async getDeviceBySerial(serial: string) {
        let sql = `SELECT * FROM Devices WHERE serial=? LIMIT 1`;
        let [rows, fields]: any = await this.executePreparedStatement(sql, [serial]);
        if (rows.length >= 1) {
            return rows[0] as Device;
        }
        else return undefined;
    }

    async getAverage(field: string, minuteCount: number, infinite: boolean) {
        let query = "SELECT AVG(d.value) AS average FROM Datapoints d JOIN Events e ON d.event_id = e.id WHERE d.sensor = '" + field + "' AND e.type = '" + field + "'";
        query = infinite ? query : query + " AND e.timestamp >= DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        console.log(query)
        const [rows]: any = await this.executePreparedStatement(query, [minuteCount]);
        return rows;
    }

    async getMaximum(seconds: number) {
        let query = "SELECT MAX(d.value) AS maximum FROM Datapoints d JOIN Events e ON d.event_id = e.id WHERE d.sensor = 'heartrate' AND e.type = 'heartrate' AND e.timestamp >= DATE_SUB(NOW(), INTERVAL ? SECOND)";
        const [rows]: any = await this.executePreparedStatement(query, [seconds]);
        return rows;
    }

    async getCurrentHeartrate() {
        let query = "SELECT value FROM Datapoints WHERE sensor = 'heartrate' ORDER BY id DESC LIMIT 1";
        const [rows]: any = await this.executePreparedStatement(query);
        return rows
    }

    async getLastFall() {
        let maxSeconds = 20;
        let query = "SELECT * FROM Events e WHERE e.type = 'fall' AND e.timestamp >= DATE_SUB(NOW(), INTERVAL ? SECOND) ORDER BY e.timestamp DESC LIMIT 1";
        const [rows]: any = await this.executePreparedStatement(query, [maxSeconds]);
        return rows
    }

    async getNotifications() {
        let maxSeconds = 20;
        let query = "SELECT * FROM Notifications n WHERE checked = false AND n.timestamp >= DATE_SUB(NOW(), INTERVAL ? SECOND) ORDER BY n.timestamp DESC LIMIT 1";
        const [rows]: any = await this.executePreparedStatement(query, [maxSeconds]);
        return rows
    }

    async markNotificationAsRead(type: string) {
        let query = "UPDATE Notifications SET checked = true WHERE title = ?";
        const [rows]: any = await this.executePreparedStatement(query, [type]);
        return rows
    }

    async insertNotification(title: string, timestamp: string) {
        console.log("inserting notification")
        let query = "INSERT INTO Notifications (title, timestamp) VALUES (?,?)";
        const [rows]: any = await this.executePreparedStatement(query, [title, timestamp]);
        console.log("inserted: ", rows)
        return rows
    }

    async getEmergency() {
        let maxSeconds = 20;
        let query = "SELECT value FROM Events WHERE sensor = 'emergency' AND e.timestamp >= DATE_SUB(NOW(), INTERVAL ? SECOND) ORDER BY id DESC LIMIT 1";
        const [rows]: any = await this.executePreparedStatement(query, [maxSeconds]);
        return rows
    }
}