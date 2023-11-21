import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB from './db/mysql';

dotenv.config();

const app: Express = express();
app.use(express.json());
const port:string | undefined = process.env.PORT || '3000';
const db = new DB();

app.get('/api/schema', async (req: Request, res: Response) => {
    await db.initSchema();
    res.send('');
});

app.post('/api/heartrate', (req: Request, res: Response) => {
    db.insertHeartRateData(req.body);
    res.send('Express + TypeScript Server');
});

app.post('/api/bloodoxygen', (req: Request, res: Response) => {
    db.insertBloodOxygenData(req.body);
    res.send('Ok');
});

app.post('/api/falldetection', (req: Request, res: Response) => {
    db.insertFallEventData(req.body);
    res.send('Ok');
});

app.get('/api/heartrate', (req: Request, res: Response) => {
    res.send('TODO');
});

app.get('/api/bloodoxygen', (req: Request, res: Response) => {
    res.send('TODO');
});

app.get('/api/falldetection', (req: Request, res: Response) => {
    res.send('TODO');
});

app.get('/api/notification', (req: Request, res: Response) => {
    res.send('TODO');
});

app.post('/api/notification', (req: Request, res: Response) => {
    res.send('TODO');
});

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});