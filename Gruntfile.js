/**
 * Created by vbabin on 10.10.2016.
 */
module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        copy: {
            main: {
                files: [
                    {expand: true
                        , cwd: 'bower_components/'
                        , src: ['angular/angular.js',
                        'angular-resource/angular-resource.js',
                        'angular-route/angular-route.js',
                        'angular-translate/angular-translate.js',
                        'angular-xeditable/dist/js/xeditable.js',
                        'me-lazyload/me-lazyload.js']
                        , dest: 'src/main/resources/static/js', flatten: true},
                    {expand: true
                        , cwd: 'bower_components/'
                        , src: ['angular-xeditable/dist/css/xeditable.css',
                        'bootstrap/dist/css/bootstrap.css']
                        , dest: 'src/main/resources/static/css', flatten: true},
                    {expand: true
                        , cwd: 'bower_components/bootstrap/dist/fonts/'
                        , src: ['glyphicons-halflings-regular.ttf',
                        'glyphicons-halflings-regular.woff']
                        , dest: 'src/main/resources/static/fonts', flatten: true}
                ],
            }
        },
        // uglify: {
        //     options: {
        //         banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
        //     },
        //     build: {
        //         src: 'src/<%= pkg.name %>.js',
        //         dest: 'build/<%= pkg.name %>.min.js'
        //     }
        // }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    // Load the plugin that provides the "uglify" task.
    //grunt.loadNpmTasks('grunt-contrib-uglify');

    // Default task(s).
    //grunt.registerTask('default', ['uglify']);

};