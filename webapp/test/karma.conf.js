module.exports = function(config) {
    config.set({
        basePath : '../',

        files : [
            'app/node_modules/jquery/dist/jquery.js',
            'app/node_modules/q/q.js',
            'app/node_modules/underscore/underscore.js',
            'app/node_modules/angular/angular.js',
            'app/node_modules/angular-ui-router/release/angular-ui-router.js',
            'app/node_modules/angular-filter/dist/angular-filter.js',
            'app/node_modules/angular-mocks/angular-mocks.js',
            'app/node_modules/ng-dialog/js/ngDialog.js',
            'app/lib/**/*.js',
            'app/js/**/*.js',
            'test/unit/**/*.js'
        ],

        //autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['Chrome'/*, 'Firefox'*/],

        plugins : [
            'karma-chrome-launcher',
            //'karma-firefox-launcher',
            'karma-jasmine'
        ],

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        }

    });
};
