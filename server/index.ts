import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';
import DB from './db/mysql';

dotenv.config();

const app: Express = express();
const port:string | undefined = process.env.PORT;
const db = new DB();

app.get('/api/schema', async (req: Request, res: Response) => {
    await db.initSchema();
    res.send('');
});

app.post('/api/heartrate', (req: Request, res: Response) => {
    res.send('Express + TypeScript Server');
});

app.post('/api/bloodoxygen', (req: Request, res: Response) => {
    res.send('a');
});

app.post('/api/falldetection', (req: Request, res: Response) => {
    res.send('ad');
});

app.get('/api/heartrate', (req: Request, res: Response) => {
    res.send('adri');
});

app.get('/api/bloodoxygen', (req: Request, res: Response) => {
    res.send('adria');
});

app.get('/api/falldetection', (req: Request, res: Response) => {
    res.send('adrian');
});

app.get('/api/notification', (req: Request, res: Response) => {
    res.send('TODO notification');
});

app.post('/api/notification', (req: Request, res: Response) => {
    res.send('TODO notification');
});


app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});