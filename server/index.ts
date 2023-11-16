import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';

dotenv.config();

const app: Express = express();
const port:string | undefined = process.env.PORT;

app.get('/', (req: Request, res: Response) => {
    res.send('<meta http-equiv="refresh" content="0; url=https://www.youtube.com/watch?v=dQw4w9WgXcQ">');
});

app.post('/data/add/heartrate', (req: Request, res: Response) => {
    res.send('Express + TypeScript Server');
});

app.post('/data/add/bloodoxygen', (req: Request, res: Response) => {
    res.send('a');
});

app.post('/data/add/falldetection', (req: Request, res: Response) => {
    res.send('ad');
});

app.get('/data/retrieve/heartrate', (req: Request, res: Response) => {
    res.send('adri');
});

app.get('/data/retrieve/bloodoxygen', (req: Request, res: Response) => {
    res.send('adria');
});

app.get('/data/retrieve/falldetection', (req: Request, res: Response) => {
    res.send('adrian');
});




app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});