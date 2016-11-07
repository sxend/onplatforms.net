"use strict";

var express = require('express');
var handlebars  = require('express-handlebars');
var app = express();

var hbs = handlebars({
  extname: '.html'
});

app.engine('.html', hbs);
app.set('view engine', '.html');

app.get('/', function (req, res) {
  res.render("index");
});

app.listen(3000);