var webpack = require('webpack');

module.exports = {
    context: __dirname + "/",
    entry: "./require.js",
    mode: 'development',
    module: {
        rules: [
            {
                test: /\.ts$/, 
                use: [
                    {
                        loader: 'ts-loader'
                    },
                    {
                        loader: 'angular-router-loader'
                    }
                ]
            }
        ]
    },
    output: {
        chunkFilename: 'angular_orcid_generated-chunk.js',
        filename: "angular_orcid_generated.js",
        path: __dirname + '/'
    },
    plugins: [
        new webpack.DefinePlugin(
            {
                'NODE_ENV': JSON.stringify(process.env.NODE_ENV),
                'process.env': {
                    'NODE_ENV': JSON.stringify(process.env.NODE_ENV)
                }
            }
        ),

        new webpack.optimize.CommonsChunkPlugin(
            {
                name: 'vendor',
                minChunks: (module) => module.context && /node_modules/.test(module.context)
            }
        ),

        new webpack.LoaderOptionsPlugin(
            {
                minimize: true,
                debug: false
            }
        ),
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: true
};
