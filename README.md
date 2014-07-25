# Fennec Search Activity

## Building with Fennec

The search activity lives in Mozilla Central, but it currently is disabaled
behind a build flag. In order to build Fennec with the search activity
enabled:

1) Edit mobile/android/confvars.sh, and uncomment the following line:

```
# Don't enable the Search Activity.
MOZ_ANDROID_SEARCH_ACTIVITY=1
```

2) Notify the build system of the configuration change:

```
mach configure
```

## grunt

The Search Activity repository uses [grunt](http://gruntjs.com/) tasks to
integrate with a *Mozilla source tree* such as `fx-team` or `mozilla-inbound`.
All grunt tasks take such a *source tree* specified either via a command line
argument like `--tree=PATH` or via the environment variable `MC`.

### Getting started with grunt

First, install [node.js](http://nodejs.org/) and [npm](http://npmjs.org) using
your OS-level package manager or similar.  Then, in the Search Activity
repository root directory, execute

    $ npm install

You can check that grunt is working and the local dependencies are installed by
executing `grunt --help`.  You should see a list of available tasks, including a
`default` task.

### Grunt tasks

#### preprocess (default)

The default task, executed when you run `grunt`, is to preprocess the Android
manifest and Android string resources.  The inputs have the suffix `.in` and the
outputs are written into the source tree.  (This is so that gradle and Android
Studio can find them without having additional paths specified.  We might change
this in future.)

#### clean

Delete all of the preprocessed outputs created by the *preprocess* task.

#### export

Copy the current Java source code, Android resources, string definitions, and
Android manifest snippets to the Mozilla source tree provided.  **Does not copy
any preprocessed outputs.** (Preprocessed outputs must be created by the Mozilla
source tree's build system at Fennec build time.)  Use this to update your
Mozilla source tree with the changes you've made in your local Search Activity
repository.
