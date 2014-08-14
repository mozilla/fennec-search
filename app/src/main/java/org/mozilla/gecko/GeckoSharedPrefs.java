/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.EnumSet;

/**
 * {@code GeckoSharedPrefs} provides scoped SharedPreferences instances.
 * You should use this API instead of using Context.getSharedPreferences()
 * directly. There are three methods to get scoped SharedPreferences instances:
 *
 * forApp()
 * Use it for app-wide, cross-profile pref keys.
 * forProfile()
 * Use it to fetch and store keys for the current profile.
 * forProfileName()
 * Use it to fetch and store keys from/for a specific profile.
 *
 * {@code GeckoSharedPrefs} has a notion of migrations. Migrations can used to
 * migrate keys from one scope to another. You can trigger a new migration by
 * incrementing PREFS_VERSION and updating migrateIfNecessary() accordingly.
 *
 * Migration history:
 * 1: Move all PreferenceManager keys to app/profile scopes
 */
public final class GeckoSharedPrefs {
    // Name for app-scoped prefs
    public static final String APP_PREFS_NAME = "GeckoApp";

    // For disabling migration when getting a SharedPreferences instance
    private static final EnumSet<Flags> disableMigrations = EnumSet.of(Flags.DISABLE_MIGRATIONS);

    // For optimizing the migration check in subsequent get() calls
    private static volatile boolean migrationDone;

    public enum Flags {
        DISABLE_MIGRATIONS
    }

    public static SharedPreferences forApp(Context context) {
        return forApp(context, EnumSet.noneOf(Flags.class));
    }

    /**
     * Returns an app-scoped SharedPreferences instance. You can disable
     * migrations by using the DISABLE_MIGRATIONS flag.
     */
    public static SharedPreferences forApp(Context context, EnumSet<Flags> flags) {

        return context.getSharedPreferences(APP_PREFS_NAME, 0);
    }

}
