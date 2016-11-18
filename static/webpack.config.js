'use strict';

let webpack = require('webpack');

module.exports = {
    context: __dirname,
    entry: "./index.ts",
    output: {
        path: __dirname + "/dist",
        filename: "platform.min.js"
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