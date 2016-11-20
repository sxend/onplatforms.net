
let webpack = require('webpack');
let path = require('path');

module.exports = {
  config: function config(name) {
    return {
      context: path.join(__dirname, '../', 'src', name),
      entry: './index.js',
      output: {
        path: path.join(__dirname, '../', 'dist', name),
        filename: 'platform.min.js'
      },
      resolve: {
        extensions: ['', '.webpack.js', '.html', '.js']
      },
      module: {
        loaders: [{
          test: /\.js$/,
          exclude: /(node_modules|bower_components)/,
          loader: 'babel-loader',
          query: {
            presets: ['es2015']
          }
        },
          { test: /\.html$/, loader: "html-loader" },
          { test: /\.css$/, loader: "style-loader!css-loader" },
          { test: /\.png$/, loader: "url-loader?limit=100000" },
          { test: /\.jpg$/, loader: "file-loader" }
        ]
      },
      plugins: [new webpack.optimize.UglifyJsPlugin({
        compress: {
          warnings: false
        }
      })]
    }
  }
};