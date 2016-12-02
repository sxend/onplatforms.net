
let webpack = require('webpack');
let path = require('path');

module.exports = {
  config: function config(name) {
    return {
      context: path.join(__dirname, '../', 'src', name),
      entry: './index.tsx',
      output: {
        path: path.join(__dirname, '../', 'dist', name),
        filename: 'app.js'
      },
      devtool: "source-map",
      resolve: {
        extensions: ['', '.webpack.js', '.html', '.js', '.tsx', '.scss']
      },
      module: {
        loaders: [{
          test: /\.tsx$/,
          exclude: /(node_modules|bower_components)/,
          loader: 'ts-loader'
        },
          { test: /\.html$/, loader: "html-loader" },
          { test: /\.css$/, loader: "style-loader!css-loader" },
          { test: /\.png$/, loader: "url-loader?limit=100000" },
          { test: /\.jpg$/, loader: "file-loader" },
          { test: /\.scss$/, loaders: ["style", "css", "sass"] }
        ],
        preLoaders: [
          { test: /\.js$/, loader: "source-map-loader" }
        ]
      },
      plugins: [
        new webpack.DefinePlugin({
          'process.env': {
            NODE_ENV: JSON.stringify('production')
          }
        }),
        new webpack.optimize.UglifyJsPlugin({
          compress: {
            warnings: false
          }
        })
      ]
    }
  }
};