module.exports = function(config) {
    config.set({
        basePath : '../',

        files : [
            'app/bower_components/jquery/dist/jquery.js',
            'app/bower_components/q/q.js',
            'app/bower_components/angular/angular.js',
            'app/bower_components/angular-ui-router/release/angular-ui-router.js',
            'app/bower_components/angular-filter/dist/angular-filter.js',
            'app/bower_components/angular-directive.g-signin/google-plus-signin.js',
            'app/bower_components/angular-mocks/angular-mocks.js',
            'app/lib/**/*.js',
            'app/js/**/*.js',
            'test/unit/**/*.js'
        ],

        //autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['Chrome', 'Firefox'],

        plugins : [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-jasmine'
        ],

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        }

    });
};
