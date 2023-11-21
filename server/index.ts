import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB from './db/mysql';

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

app.post('/api/event', (req: Request, res: Response) => {
    switch(req.body.sensor) {
        case 'heartrate':
            db.insertHeartRateData(req.body);
            break;
        case 'bloodoxygen':
            db.insertBloodOxygenData(req.body);
            break;
        case 'falldetection':
            db.insertFallEventData(req.body);
            break;
        default:
            console.log("Unknown sensor type");
            res.send('Unknown sensor type');
    }
    
    res.send('Ok');
});

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});