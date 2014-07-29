/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

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
    public static final String MANGLED_ANDROID_PACKAGE_NAME = "@MANGLED_ANDROID_PACKAGE_NAME@";

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
