/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

module.exports = function (grunt) {
    var TREE = grunt.option('tree') || process.env.MC || grunt.fail.fatal("must specify mozilla source tree");

    require('load-grunt-tasks')(grunt);
    var path = require('path');

    var packageName = "org.mozilla.fennec_" + process.env.USER;
    var defines = {
        ANDROID_PACKAGE_NAME: packageName,
        MOZ_ANDROID_SHARED_ID: packageName + ".sharedID",
        ANDROID_TARGET_SDK: 19,
    };

    // A task for preprocessing a src into a dest using Mozilla's Python preprocessor.
    grunt.task.registerMultiTask("preprocess", "Preprocesses using the Mozilla Python preprocessor.", function () {
        var options = this.options();

        grunt.log.writelns("Preprocessing " + options.src + " >>> " + options.dest);

        var defines = [];
        Object.keys(options.defines).forEach(function(key) {
            defines.push("-D " + key + "=" + options.defines[key]);
        });

        var args = [ "python",
                     path.join(TREE, "python/mozbuild/mozbuild/preprocessor.py"),
                     options.src,
                     "-o",
                     options.dest,
                   ].concat(defines);

        var taskName = this.nameArgs.replace(':', '_');
        grunt.config("shell." + taskName + ".command", args.join(" "));

        grunt.task.run(["shell:" + taskName]);
    });

    // A task for exporting src to a dest under the Mozilla source tree.
    grunt.task.registerMultiTask("export", "export from local to mozilla source tree.", function () {
        var options = this.options();
        options.dest = path.join(TREE, options.dest);

        var taskName = this.nameArgs.replace(':', '_');
        grunt.config("rsync." + taskName + '.options', options);

        grunt.task.run(["rsync:" + taskName]);
    });

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        
        export: {
            options: {
                // args: ["--verbose"],
                recursive: true,
                syncDest: true,
            },
            main: {
                options: {
                    src: [ "app/src/main/java",
                           "strings",
                           "manifests",
                         ],
                    dest: "mobile/android/search/",
                }
            },
            res: {
                options: {
                    src: "app/src/main/res",
                    dest: "mobile/android/search/",
                    exclude: ["*strings.xml"],
                }
            },
            manifests: {
                options: {
                    src: "manifests",
                    dest: "mobile/android/search/",
                    exclude: ["AndroidManifest.xml.in"],
                }
            },
            strings: {
                options: {
                    src: "strings",
                    dest: "mobile/android/search/",
                    exclude: ["strings.xml.in"],
                }
            },
        },

        preprocess: {
            options: {
                defines: defines,
            },

            manifest: {
                options: {
                    src: "manifests/AndroidManifest.xml.in",
                    dest: "app/src/main/AndroidManifest.xml",
                },
            },

            strings: {
                options: {
                    src: "strings/strings.xml.in",
                    dest: "app/src/main/res/values/strings.xml",
                },
            },
        },
    });

    var preprocessedFiles = [];
    Object.keys(grunt.config("preprocess")).forEach(function (task) {
        var task = grunt.config("preprocess." + task);
        if (task.options && task.options.dest) {
            preprocessedFiles.push(task.options.dest);
        }
    });
    grunt.config("clean.preprocessed.src", preprocessedFiles);

    grunt.registerTask('default', ['preprocess']);
};
