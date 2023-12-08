import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB, { Datapoint, Device } from './db/mysql';
import { type Event } from "./db/mysql";
import cors from 'cors';

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
app.use(cors());
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
    console.log(req.body)
    console.log(JSON.stringify(req.body))
    console.log("Received event")
    let promises = [];
    let requestObj = req.body;
    promises.push(insertEventHandler(requestObj));
    await Promise.all(promises);
    res.send("Ok");
});


app.get('/api/summary/heartrate/average', async (req: Request, res: Response) => {
    let mc = req.query.minutes;
    console.log(mc)
    let minuteCount = 0;
    if (mc !== undefined && typeof mc === "string") {
        minuteCount = parseInt(mc, 10);
    }

    let infinite = false;
    if (isNaN(minuteCount) || minuteCount <= 0) {
        infinite = true;
    }
    let rows = await db.getAverage("heartrate", minuteCount, infinite);
    if (rows !== undefined && rows.length >= 0 && rows[0].average !== null) {
        res.send({ "message": "ok", "value": + rows[0].average });
    }
    else {
        res.send({ "message": "notfound" });
    }
});

app.get('/api/notification', async (req: Request, res: Response) => {
    let rows = await db.getNotifications();
});

app.get('/api/notification/read', async (req: Request, res: Response) => {
    let rows = await db.markNotificationAsRead(req.query.type as string);
});

app.post('/api/notification', async (req: Request, res: Response) => {
    await db.insertNotification(req.body.id as string, req.body.title as string, req.body.message as string, req.body.timestamp as string);
});

app.get('/api/heartrate', async (req: Request, res: Response) => {
    let rows = await db.getCurrentHeartrate();
    if (rows !== undefined && rows.length >= 0) {
        console.log(rows[0]);
        res.send({ "message": "ok", "value": rows[0].value });
    }
});

app.get('/api/summary/fall/last', async (req: Request, res: Response) => {
    let rows = await db.getLastFall();
    console.log(rows);
    if (rows !== undefined && rows.length > 0) {
        res.send({ "message": "ok", "last": rows[0].value });
    }
    else {
        res.send({ "message": "notfound" });
    }
});

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});