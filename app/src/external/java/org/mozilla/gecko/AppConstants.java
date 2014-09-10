/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

import android.os.Build;

/**
 * A collection of constants that pertain to the build and runtime state of the
 * application. Typically these are sourced from build-time definitions (see
 * Makefile.in). This is a Java-side substitute for nsIXULAppInfo, amongst
 * other things.
 *
 * See also SysInfo.java, which includes some of the values available from
 * nsSystemInfo inside Gecko.
 */
public class AppConstants {
    public static final String ANDROID_PACKAGE_NAME = "org.mozilla.fennec";
    // TEMPORARY: Only in the search activity so that it doesn't fall down when launching itself
    //            When this code is moved into Fennec this should be renamed to match ANDROID_PACKAGE_NAME
    public static final String SEARCH_PACKAGE_NAME = "org.mozilla.search";
    public static final String MANGLED_ANDROID_PACKAGE_NAME = "@MANGLED_ANDROID_PACKAGE_NAME@";
    /**
     * Encapsulates access to compile-time version definitions, allowing
     * for dead code removal for particular APKs.
     */
    public static final class Versions {
        public static final int MIN_SDK_VERSION = 9;
        public static final int MAX_SDK_VERSION = 21;

        /*
         * The SDK_INT >= N check can only pass if our MAX_SDK_VERSION is
         * _greater than or equal_ to that number, because otherwise we
         * won't be installed on the device.
         *
         * If MIN_SDK_VERSION is greater than or equal to the number, there
         * is no need to do the runtime check.
         */
        public static final boolean feature10Plus = MIN_SDK_VERSION >= 10 || (MAX_SDK_VERSION >= 10 && Build.VERSION.SDK_INT >= 10);
        public static final boolean feature11Plus = MIN_SDK_VERSION >= 11 || (MAX_SDK_VERSION >= 11 && Build.VERSION.SDK_INT >= 11);
        public static final boolean feature12Plus = MIN_SDK_VERSION >= 12 || (MAX_SDK_VERSION >= 12 && Build.VERSION.SDK_INT >= 12);
        public static final boolean feature14Plus = MIN_SDK_VERSION >= 14 || (MAX_SDK_VERSION >= 14 && Build.VERSION.SDK_INT >= 14);
        public static final boolean feature15Plus = MIN_SDK_VERSION >= 15 || (MAX_SDK_VERSION >= 15 && Build.VERSION.SDK_INT >= 15);
        public static final boolean feature16Plus = MIN_SDK_VERSION >= 16 || (MAX_SDK_VERSION >= 16 && Build.VERSION.SDK_INT >= 16);
        public static final boolean feature17Plus = MIN_SDK_VERSION >= 17 || (MAX_SDK_VERSION >= 17 && Build.VERSION.SDK_INT >= 17);
        public static final boolean feature19Plus = MIN_SDK_VERSION >= 19 || (MAX_SDK_VERSION >= 19 && Build.VERSION.SDK_INT >= 19);

        /*
         * If our MIN_SDK_VERSION is 14 or higher, we must be an ICS device.
         * If our MAX_SDK_VERSION is lower than ICS, we must not be an ICS device.
         * Otherwise, we need a range check.
         */
        public static final boolean preJB = MAX_SDK_VERSION < 16 || (MIN_SDK_VERSION < 16 && Build.VERSION.SDK_INT < 16);
        public static final boolean preICS = MAX_SDK_VERSION < 14 || (MIN_SDK_VERSION < 14 && Build.VERSION.SDK_INT < 14);
        public static final boolean preHCMR2 = MAX_SDK_VERSION < 13 || (MIN_SDK_VERSION < 13 && Build.VERSION.SDK_INT < 13);
        public static final boolean preHCMR1 = MAX_SDK_VERSION < 12 || (MIN_SDK_VERSION < 12 && Build.VERSION.SDK_INT < 12);
        public static final boolean preHC = MAX_SDK_VERSION < 11 || (MIN_SDK_VERSION < 11 && Build.VERSION.SDK_INT < 11);
    }

    /**
     * The name of the Java class that launches the browser.
     */
    public static final String BROWSER_INTENT_CLASS_NAME = "org.mozilla.gecko.BrowserApp";
    public static final String SEARCH_INTENT_CLASS_NAME = "org.mozilla.search.MainActivity";

    public static final String GRE_MILESTONE = "@GRE_MILESTONE@";

    public static final String MOZ_APP_ABI = "@MOZ_APP_ABI@";
    public static final String MOZ_APP_BASENAME = "@MOZ_APP_BASENAME@";

    // For the benefit of future archaeologists: APP_BUILDID and
    // MOZ_APP_BUILDID are *exactly* the same.
    // GRE_BUILDID is exactly the same unless you're running on XULRunner,
    // which is never the case on Android.
    public static final String MOZ_APP_BUILDID = "@MOZ_APP_BUILDID@";
    public static final String MOZ_APP_ID = "@MOZ_APP_ID@";
    public static final String MOZ_APP_NAME = "@MOZ_APP_NAME@";
    public static final String MOZ_APP_VENDOR = "@MOZ_APP_VENDOR@";
    public static final String MOZ_APP_VERSION = "@MOZ_APP_VERSION@";

    // MOZILLA_VERSION is already quoted when it gets substituted in. If we
    // add additional quotes we end up with ""x.y"", which is a syntax error.

    public static final String MOZ_CHILD_PROCESS_NAME = "@MOZ_CHILD_PROCESS_NAME@";
    public static final String MOZ_UPDATE_CHANNEL = "@MOZ_UPDATE_CHANNEL@";
    public static final String OMNIJAR_NAME = "@OMNIJAR_NAME@";
    public static final String OS_TARGET = "@OS_TARGET@";

    public static final String USER_AGENT_BOT_LIKE = "Redirector/" + AppConstants.MOZ_APP_VERSION +
        " (Android; rv:" + AppConstants.MOZ_APP_VERSION + ")";

    public static final String USER_AGENT_FENNEC_MOBILE = "Mozilla/5.0 (Android; Mobile; rv:" +
        AppConstants.MOZ_APP_VERSION + ") Gecko/" +
        AppConstants.MOZ_APP_VERSION + " Firefox/" +
        AppConstants.MOZ_APP_VERSION;

    public static final String USER_AGENT_FENNEC_TABLET = "Mozilla/5.0 (Android; Tablet; rv:" +
        AppConstants.MOZ_APP_VERSION + ") Gecko/" +
        AppConstants.MOZ_APP_VERSION + " Firefox/" +
        AppConstants.MOZ_APP_VERSION;


    // Official corresponds, roughly, to whether this build is performed on
    // Mozilla's continuous integration infrastructure. You should disable
    // developer-only functionality when this flag is set.
    public static final boolean MOZILLA_OFFICIAL = true;
}
