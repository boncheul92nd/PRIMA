/**
 * Created by USER on 2017-08-01.
 */

var express = require('express');
var multer = require('multer');
var app = express();
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/Car');
var fs = require('fs');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
    console.log('mongo db connection OK.');
});
var Car = require('./models/Car');
var Photo = require('./models/Photo');
var imageSchema = mongoose.Schema({
    img: {data:Buffer, contentType:String}
});
var Item = mongoose.model('Photo', imageSchema);


app.use(multer({
    dest: './uploads/',
    rename: function (fieldname, filename) {
        return filename;
    },
    onFileUploadStart: function (file) {
        // console.log(file.originalname + ' is starting ...')
    },
    onFileUploadComplete: function (file) {
        // console.log(file.fieldname + ' uploaded to  ' + file.path)
    }
}));

app.set('views', './views');
app.set('view engine', 'jade');

app.get('/', function (req, res) {
    Car.find({}).exec(function (err, result) {
        console.log(result);
        res.render('index', {result:result});
    });
});
app.get('/input', function (req, res) {
    res.render('input');
});
app.get('/record', function (req, res) {
    res.send('123');
});

app.post('/api/photo', function (req, res) {

    var newCar = new Car();
    var date = req.body.date;
    var location = req.body.location;
    var number = req.body.number;

    newCar.date = date;
    newCar.location = location;
    newCar.number = number;

    newCar.save(function (err, result) {
        if(err) {
            res.send('error saving Car');
        } else {
            console.log(result);
        }
    });

    var newPhoto = new Photo();
    newPhoto.img.data = fs.readFileSync(req.files.userPhoto.path);
    newPhoto.img.contentType = 'image/png';
    newPhoto.save(function (err, result) {
        if(err) {
            res.send('error saving Photo');
        } else {

            console.log(result);
        }
    });
    res.redirect('/');
});



app.listen(8080, function () {
    console.log("Working on port 8080");
});

