import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB, { Datapoint, Device } from './db/mysql';
import { type Event } from "./db/mysql";

export enum EventType {
    Heartrate = "heartrate",
    Bloodoxygen = "bloodoxygen",
    Fall = "fall"
}

export enum EventLevel {
    Report = "report",
    Alert = "alert"
}

export enum Sensor {
    Heartrate = "heartrate",
    Bloodoxygen = "bloodoxygen",
    Fall = "fall"
}

export type RequestDatapoint = {
    sensor: Sensor,
    value: number
}

export type EventRequest = {
    event_type: EventType,
    event_level: EventLevel,
    device: string,
    timestamp: string,
    datapoints: RequestDatapoint[]
}

dotenv.config();

const app: Express = express();
app.use(express.json());
const port: string | undefined = process.env.PORT || '3000';
const db = new DB();


app.get('/', async (req: Request, res: Response) => {
    res.send('Ubiqui.care API');
});

app.get('/api/schema', async (req: Request, res: Response) => {
    console.log("Initializing schema")
    await db.initSchema();
    res.send('Ok');
});

const insertEventHandler = async (requestObj: EventRequest) => {
    let device: Device | undefined = await db.getDeviceBySerial(requestObj.device);

    if (device === undefined || device.id === undefined) {
        throw new Error("Device not found");
    }

    let newEvent: Event = {
        device_id: device.id,
        type: requestObj.event_type,
        alert: requestObj.event_level === EventLevel.Alert,
        timestamp: requestObj.timestamp
    };

    console.log(newEvent);
    let event_id = await db.insertEvent(newEvent);
    console.log("Inserted event with id: " + event_id)

    let dps: Datapoint[] = [];
    for (let i = 0; i < requestObj.datapoints.length; i++) {
        let datapoint: RequestDatapoint = requestObj.datapoints[i];
        let newDatapoint: Datapoint = {
            event_id: event_id,
            sensor: datapoint.sensor,
            value: datapoint.value
        }
        dps.push(newDatapoint);
    }
    //TODO check if insert successful otherwise return error
    let insertDPsResult = await db.insertDatapoints(dps);
}

app.post('/api/event', async (req: Request, res: Response) => {
    let promises = [];
    for (let i = 0; i < req.body.length; i++) {
        let requestObj = req.body[i];
        promises.push(insertEventHandler(requestObj));
    }
    await Promise.all(promises);
    res.send('{"message": "ok"}');
});

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});