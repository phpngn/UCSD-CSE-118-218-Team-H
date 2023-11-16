import express, { Express, Request, Response } from 'express';
import dotenv from 'dotenv';

dotenv.config();

const app: Express = express();
const port:string | undefined = process.env.PORT;

app.get('/', (req: Request, res: Response) => {
    res.send('Express + TypeScript Server');
});

//heart rate
//blood oxygen
//fall detection

//get current heart rate
//get current blood oxygen

app.listen(port, () => {
    console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});