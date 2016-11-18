let webpack = require('webpack');
let path = require('path');

module.exports = {
  context: path.join(__dirname, '../', 'src', 'www'),
  entry: './index.ts',
  output: {
    path: path.join(__dirname, '../', 'dist', 'www'),
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
    }]
  },
  plugins: [new webpack.optimize.UglifyJsPlugin({
    compress: {
      warnings: false
    }
  })]
};