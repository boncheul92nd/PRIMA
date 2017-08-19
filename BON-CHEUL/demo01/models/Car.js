/**
 * Created by USER on 2017-08-01.
 */

var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var CarSchema = new Schema({
    date:{
        type:Date,
        default:Date.now
    },
    location:String,
    number:String
});

module.exports = mongoose.model('cars', CarSchema);