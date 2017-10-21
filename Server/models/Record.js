/**
 * Created by USER on 2017-08-25.
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var RecordSchema = new Schema({

    date:String,
    location:String,

    img_original: {
        data:Buffer,
        contentType:String
    },
    img_edged: {
        data:Buffer,
        contentType:String
    },
    img_outline: {
        data:Buffer,
        contentType:String
    },
    img_warped: {
        data:Buffer,
        contentType:String
    },
    img_scanned: {
        data:Buffer,
        contentType:String
    },

    number:String
});

module.exports = mongoose.model('Records', RecordSchema);