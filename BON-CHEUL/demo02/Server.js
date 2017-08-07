/**
 * Created by USER on 2017-08-02.
 */

var express = require('express');
var multer = require('multer');
var app = express();
var fs = require('fs');
var mkdirp = require('mkdirp');
var PythonShell = require('python-shell');
var options = {args:''};

app.use(multer({
    dest: './uploads',
    rename: function (field, filename) {
        var d = new Date();
        var newDir = '/'+d.getFullYear()+'_'+d.getMonth()+'_'+d.getDay()+'_'+
                            d.getHours()+'_'+d.getMinutes()+'_'+d.getSeconds()+'/';
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
app.set('views', './views');
app.set('view engine', 'jade');


app.get('/', function (req, res) {
    res.render('index');
});
app.post('/input', function (req,res) {
    res.redirect('/');
});

app.listen(8081, function () {
    console.log("Working on port 8081");
});