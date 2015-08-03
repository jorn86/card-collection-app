/**
 * Build order:
 * - Copy all files that need their name changed to 'before'
 * - Copy all files that don't need their name changed, but do contain references to changed names in 'during'
 * - Copy all other files to 'after'
 * - Let revison run on 'before', putting the renamed files in 'during'
 * - Let revreplace run on 'during', putting the modified files in 'after'
 * - Build temp.war from files in 'after'
 * - Optionally, run tests
 * - Move temp.war to ROOT.war
 */
var gulp = require('gulp');
//var debug = require('gulp-debug');
var sass = require('gulp-ruby-sass');
var merge = require('merge-stream');
var exec = require('child_process');
var fs = require('fs-extra');
var del = require('del');
var rev = require('gulp-rev');
var replace = require('gulp-replace');
var runSequence = require('run-sequence');
var karma = require('karma');

var beforeRevreplace = 'temp/before/';
var duringRevreplace = 'temp/during/';
var afterRevreplace = 'temp/after/';

gulp.task('clean', function() {
	del.sync('temp/**');
});

gulp.task('bower', function(done) {
	exec.exec('bower install', function(err, out) {
		console.log(out);
		done(err);
	});
});

gulp.task('styles', function() {
	return gulp.src(['app/scss/app.scss'])
		.pipe(sass({ style: 'expanded' }))
		.pipe(gulp.dest(beforeRevreplace + 'css'))
});

gulp.task('styles-develop', function() {
	return gulp.src(['app/scss/app.scss'])
		.pipe(sass({ style: 'expanded' }))
		.on('error', function (err) { console.log(err.message); })
		.pipe(gulp.dest('app/css/'));
});

gulp.task('fonts', function() {
	return gulp.src('app/fonts/**/*')
		.pipe(gulp.dest(afterRevreplace + 'fonts'));
});

gulp.task('lib', function() {
	var angular = gulp.src('app/lib/angular/angular.js')
		.pipe(gulp.dest(beforeRevreplace + 'lib/angular/'));

	var angularUIRouter = gulp.src('app/lib/angular-ui-router/release/angular-ui-router.js')
		.pipe(gulp.dest(beforeRevreplace + 'lib/angular-ui-router/release/'));

	return merge(angular, angularUIRouter);
});

gulp.task('js', function() {
	return gulp.src('app/js/**/*')
		.pipe(gulp.dest(beforeRevreplace + 'js'));
});

gulp.task('partials', function() {
	return gulp.src('app/partials/**/*')
		.pipe(gulp.dest(beforeRevreplace + 'partials/'));
});

gulp.task('index', function() {
	return gulp.src(['app/*.html'])
		.pipe(gulp.dest(duringRevreplace));
});

gulp.task('images', function() {
	return gulp.src('app/img/**/*')
		.pipe(gulp.dest(afterRevreplace + 'img'));
});

gulp.task('revision', function() {
	return gulp.src(beforeRevreplace + '**/*')
		.pipe(rev())
		.pipe(gulp.dest(duringRevreplace))
		.pipe(rev.manifest())
		.pipe(gulp.dest('temp'));
});

gulp.task('revreplace', function() {
	var manifest = require('./temp/rev-manifest.json');
	var stream = gulp.src(duringRevreplace + '**/*');

	stream = Object.keys(manifest)
		.reduce(function(stream, key) {
			return stream.pipe(replace(key, manifest[key]));
		}, stream);

	return stream.pipe(gulp.dest(afterRevreplace));
});

gulp.task('war', function(done) {
	exec.exec('jar cf temp/temp.war WEB-INF -C ' + afterRevreplace + ' .', done);
});

gulp.task('deployWar', function(done) {
	fs.move('temp/temp.war', 'ROOT.war', { clobber: true }, done);
});

gulp.task('test', function(done) {
	karma.server.start({
		browsers: ['PhantomJS'],
		configFile: __dirname + '/spec/karma.conf.js',
		singleRun: true
	}, function(exitStatus) {
		done(exitStatus ? "There are failing unit tests" : undefined);
	});
});

gulp.task('build', function(done) {
	return runSequence('clean', 'bower', ['styles', 'fonts', 'lib', 'js', 'index', 'partials', 'images'], 'revision' ,'revreplace', 'war', done);
});

gulp.task('deployDirk', function(done) {
	return runSequence('clean', ['styles', 'fonts', 'lib', 'js', 'index', 'partials', 'images'], 'revision' ,'revreplace', 'war', 'deployWar', done);
});

gulp.task('deploy', function(done) {
    return runSequence('build', 'deployWar', done);
});

gulp.task('deployTest', function(done) {
	return runSequence('build', 'deployWar', 'test', done);
});
