/**
 * Created by USER on 2017-08-01.
 */

var express = require('express');
var multer = require('multer');
var app = express();
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/PRIMA');
var fs = require('fs');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
    console.log('mongo db connection OK.');
});
var Car = require('./models/Car');
var Photo = require('./models/Photo');
var User = require('./models/User');
var logedIn = false;

app.use(multer({
    dest: './uploads/',
    rename: function (fieldname, filename) {
        return Date.now();
    },
    onFileUploadStart: function (file) {},
    onFileUploadComplete: function (file) {}
}));

app.set('views', './views');
app.set('view engine', 'jade');

app.get('/', function (req, res) {
    Car.find({}).exec(function (err, result) {
        if(logedIn) {
            res.render('index', {result: result});
        } else {
            res.render('login');
        }
    });
});

app.get('/logout', function (req, res) {
    logedIn = false;
    res.redirect('/')
});
app.post('/login', function (req, res) {
    var id = req.body.username;
    var pwd = req.body.password;

    User.findOne({'username':id}).exec(function (err, matchedID) {
        if (matchedID) {
            matchedID.comparePassword(pwd, function (err, matchedPWD) {
                if(matchedPWD) {
                    logedIn = true;
                    res.redirect('/');
                } else {
                    var output = `
                    <script type="text/javascript">
                        alert("비밀번호를 틀리셨습니다.");
                        history.back();
                    </script>
                    `;
                    res.send(output);
                }
            });
        } else {
            var output = `
            <script type="text/javascript">
                alert("존재하지 않는 아이디입니다.");
                history.back();
            </script>
            `;
            res.send(output);
        }
    });
});

app.get('/register', function (req, res) {
    res.render('register');
});
app.post('/register', function (req, res) {

    var username = req.body.username;
    var password = req.body.password;

    dupleIDCheck(username, password, res);
});

function dupleIDCheck(username, password, res) {
    User.findOne({'username':username}).exec(function (err, MatchedID) {
        if(MatchedID) {
            var output = `
                <script type="text/javascript">
                    alert('이미 존재하는 아이디입니다.');
                    history.back();
                </script>
            `;
            res.send(output);
            return false;
        } else {
            var newUser = new User();
            newUser.username = username;
            newUser.password = password;
            newUser.save(function (err, result) {
                if(err) {
                    res.send('error saving User');
                } else {
                    console.log(result);
                    res.render('welecome');
                }
            });
       }
    });
}















app.get('/input', function (req, res) {
    res.render('input');
});
app.get('/record', function (req, res) {
    Photo.find({}).select('_id').exec(function (err, result) {
       res.render('record', {result:result});
    });
});

app.post('/input', function (req, res) {
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
            console.log(result._id);
        }
    });

    var newPhoto = new Photo();
    newPhoto.img.data = fs.readFileSync(req.files.userPhoto.path);
    newPhoto.img.contentType = 'image/png';
    newPhoto.save(function (err, result) {
        if(err) {
            res.send('error saving Photo');
        } else {
            console.log(result._id);
        }
    });
    res.redirect('/');
});



app.listen(8080, function () {
    console.log("Working on port 8080");
});

