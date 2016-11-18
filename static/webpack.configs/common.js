
let webpack = require('webpack');
let path = require('path');

module.exports = {
  config: function config(name) {
    return {
      context: path.join(__dirname, '../', 'src', name),
      entry: './index.ts',
      output: {
        path: path.join(__dirname, '../', 'dist', name),
        filename: 'platform.min.js'
      },
      resolve: {
        extensions: ['', '.webpack.js', '.html', '.ts', '.js']
      },
      module: {
        loaders: [{
          test: /\.ts?$/,
          loader: 'ts-loader',
          exclude: /node_modules/
        },
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