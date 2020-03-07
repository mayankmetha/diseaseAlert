var express = require('express');
var path = require('path');

count = path.resolve(__dirname+'/../tmp')

var app = express();

app.get('/', (req, res) => {
    res.sendFile(count);
});

app.listen(8080, () => {
    console.log("http://localhost:8080");
});