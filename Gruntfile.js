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
        grunt.task.run(["mozbuild"]);

        var options = this.options();
        options.dest = path.join(TREE, options.dest);

        var taskName = this.nameArgs.replace(':', '_');
        grunt.config("rsync." + taskName + '.options', options);

        grunt.task.run(["rsync:" + taskName]);
    });

    grunt.task.registerMultiTask("mozbuild", "Generate a mozbuild file from a local directory.", function () {
        var done = this.async();

        var options = this.options({
        });

        var fileNames = [];
        this.files.forEach(function(file) {
            file.src.forEach(function(src) {
                if (src.indexOf("MockHistory") >= 0) {
                    return;
                }
                fileNames.push(path.join(file.dest, src));
            });
        });
        fileNames.sort(function (a, b) {
            return a.toLowerCase().localeCompare(b.toLowerCase());
        });

        var fs = require('fs');
        var stream = fs.createWriteStream(options.dest);
        stream
            .once('open', function(fd) {
                stream.write("# -*- Mode: python; c-basic-offset: 4; indent-tabs-mode: nil; tab-width: 40 -*-\n");
                stream.write("# vim: set filetype=python:\n");
                stream.write("# This Source Code Form is subject to the terms of the Mozilla Public\n");
                stream.write("# License, v. 2.0. If a copy of the MPL was not distributed with this\n");
                stream.write("# file, You can obtain one at http://mozilla.org/MPL/2.0/.\n");
                stream.write("\n");
                stream.write(options.python_list + " = [\n");
                fileNames.forEach(function (file) {
                    stream.write("    '" + file + "',\n");
                });
                stream.write("]\n");
                stream.end();
            })
            .once('finish', function () {
                done();
            });
    });

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        mozbuild: {
            java: {
                options: {
                    dest: path.join(TREE, "mobile/android/search/search_activity_sources.mozbuild"),
                    python_list: "search_activity_sources",
                },
                files: [{
                    cwd: "app/src/main/java",
                    src: "org/mozilla/search/**/*.java",
                    dest: "java/",
                }],
            },
        },

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
                    exclude: ["*BrowserContract.java", "*AppConstants.java", "*MockHistoryProvider.java"],
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
            res: {
                options: {
                    src: "res",
                    dest: "mobile/android/base/resources/",
                }
            },
            branding: {
                options: {
                    src: "branding",
                    dest: "mobile/android/branding/",
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
