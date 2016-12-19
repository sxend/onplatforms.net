
const webpack = require('webpack');
const path = require('path');
const env = process.env['npm_config_env'] || "production";
const ppOpt = JSON.stringify(({
  "production": {
    API_URL: "//accounts.onplatforms.net"
  },
  "local": {
    API_URL: "//accounts.onplatforms.local:9091"
  }
})[env]);
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
          loader: 'ts-loader!preprocess?' + ppOpt
        },
          { test: /\.html$/, loader: "html-loader" },
          { test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "url-loader?limit=10000&minetype=application/font-woff" },
          { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "file-loader" },
          { test: /\.css$/, loader: "style-loader!css-loader" },
          { test: /\.png$/, loader: "url-loader?limit=100000" },
          { test: /\.jpg$/, loader: "file-loader" },
          { test: /\.scss$/, loaders: ["style", "css", "sass"] },
          { test: /\.less/, loaders: ["less"] }
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