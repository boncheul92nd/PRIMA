/**
 * Created by USER on 2017-08-01.
 */

var mogoose = require('mongoose');
var Schema = mogoose.Schema;
var PhotoSchema = new Schema({
    img: {data:Buffer, contentType:String}
});

module.exports = mogoose.model('Photos', PhotoSchema);