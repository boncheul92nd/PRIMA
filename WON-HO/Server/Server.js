/**
 * Created by USER on 2017-08-25.
 */
var express = require('express');
var app = express();
var fs = require('fs');
var multer = require('multer');
var mkdirp = require('mkdirp');
var PythonShell = require('python-shell');
var options = {args:''};
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/PRIMA_DB');
var db = mongoose.connection;
var Record = require('./models/Record');

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
    console.log('log: 몽고DB 연결 완료');
});
app.set('views', './views');
app.set('view engine', 'jade');

app.use(multer({
    dest:'./uploads',
    rename: function (field, filename) {
        var d = new Date();
        var newDir = '/'+genDirnames()+'/';
        mkdirp('./uploads'+newDir, function (err) {
            if (err) throw err;
        });
        options.args = newDir;
        return newDir+'Original';
    },
    onFileUploadComplete: function (file) {
        PythonShell.run('opencvOCR.py', options, function (err, results) {
            if (err) throw err;
            console.log(results);
        });
    }
}));


app.get('/', function (req, res) {
    Record.find({}).exec(function (err, result) {
        res.render('index', {result:result});
    });
});
app.post('/input', function (req, res) {
    var tempDir = options.args.toString(),
        dir = tempDir.substring(1,tempDir.length-1);

    var newRecord = new Record();
    newRecord.date = dir;
    newRecord.location = req.body.location;

    newRecord.img_original.data = fs.readFileSync(req.files.userPhoto.path);
    newRecord.img_original.contentType = 'image/png';

    newRecord.save(function (err, result) {
        if(err) {
            res.send('error saving Photo');
        } else {
            console.log('success saving Photo');
        }
    });
    res.redirect('/');
});

app.get('/delete/:id', function (req, res) {

    var date = req.params.id;
    Record.find({"date":date}).select({
        "date":1, "location":1}).exec(function (err, result) {
        res.send(result);
    });
});

function genDirnames() {
    var dayArr = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
    var d = new Date();
    var year = d.getUTCFullYear().toString().substring(2,4);

    var tempMonth = (d.getUTCMonth()+1).toString(),
        month = (tempMonth.length < 2)? '0'+tempMonth : tempMonth;

    var tempDate = d.getUTCDate().toString(),
        date = (tempDate.length < 2)? '0'+tempDate : tempDate;

    var day = dayArr[d.getUTCDay()-1];
    var hour = d.getHours();

    var tempMin = d.getMinutes().toString(),
        min = (tempMin.length < 2)? '0'+tempMin : tempMin;

    var tempSec = d.getSeconds().toString(),
        sec = (tempSec.length < 2)? '0'+tempSec : tempSec;

    var filename = year+month+date+'_'+day+'_'+hour+min+sec;
    return filename;
}
app.listen(8082, function () {
    console.log("log: 8082번 포트에서 동작중");
});