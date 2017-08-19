/**
 * Created by USER on 2017-08-01.
 */

var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var PhotoSchema = new Schema({
    img: {
        data:Buffer,
        contentType:String
    }
});

module.exports = mongoose.model('Photos', PhotoSchema);