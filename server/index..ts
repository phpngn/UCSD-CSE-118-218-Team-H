import {express} from 'express';

const app = express();
const port = 3000;

// Set EJS as the view engine
app.set('view engine', 'ejs');

// Define a route to render a template
app.get('/', (req, res) => {
    // Render the 'index' template and pass some data to it
    res.render('index', { title: 'Express Template Example', message: 'Hello, World!' });
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});