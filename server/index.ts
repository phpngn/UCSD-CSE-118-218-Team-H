import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB, {Datapoint, Device, QueryResult} from './db/mysql';
import {type Event} from "./db/mysql";

export enum EventType {
    Heartrate = "heartrate",
    Bloodoxygen = "bloodoxygen",
    Fall = "fall"
}

export enum EventLevel {
    Report="report",
    Alert="alert"
}

export enum Sensor {
    Heartrate = "heartrate",
    Bloodoxygen = "bloodoxygen",
    Fall = "fall"
}

export type RequestDatapoint = {
    sensor:Sensor,
    value: number
}

export type EventRequest = {
    event_type: EventType,
    event_level: EventLevel,
    device: string,
    timestamp:string,
    datapoints: RequestDatapoint[]
}

dotenv.config();

const app: Express = express();
app.use(express.json());
const port:string | undefined = process.env.PORT || '3000';
const db = new DB();


app.get('/', (req: Request, res: Response) => {
    res.send('Ubiqui.care API');
});

app.get('/api/schema', async (req: Request, res: Response) => {
    console.log("Initializing schema")
    await db.initSchema();
    res.send('Ok');
});

app.post('/api/event', async (req: Request, res: Response) => {

    let requestObj:EventRequest = req.body;

    let device:Device | undefined = await db.getDeviceBySerial(requestObj.device);

    if(device === undefined || device.id === undefined) {
        res.status(401).send("Error");
        return;
    }

    let newEvent:Event = {
        device_id: device.id,
        type: requestObj.event_type,
        alert: requestObj.event_level === EventLevel.Alert,
        timestamp: requestObj.timestamp
    };
    let insertEventResult = await db.insertEvent(newEvent);
    //todo extract eventid
    let dps:Datapoint[] = [];
    for(let i = 0; i < requestObj.datapoints.length; i++) {
        let datapoint:RequestDatapoint = requestObj.datapoints[i];
        let newDatapoint:Datapoint = {
            event_id: event_id,
            sensor: datapoint.sensor,
            value: datapoint.value
        }
        dps.push(newDatapoint);
    }
    let insertDPsResult = await db.insertDatapoints(dps);
    //todo check if insert successful otherwise return error
    res.send('Ok');
});

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});