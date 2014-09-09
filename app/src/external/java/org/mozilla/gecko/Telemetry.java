/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

import android.os.SystemClock;
import android.util.Log;

import org.mozilla.gecko.TelemetryContract.Event;
import org.mozilla.gecko.TelemetryContract.Method;
import org.mozilla.gecko.TelemetryContract.Reason;
import org.mozilla.gecko.TelemetryContract.Session;

/**
 * All telemetry times are relative to one of two clocks:
 *
 * * Real time since the device was booted, including deep sleep. Use this
 * as a substitute for wall clock.
 * * Uptime since the device was booted, excluding deep sleep. Use this to
 * avoid timing a user activity when their phone is in their pocket!
 *
 * The majority of methods in this class are defined in terms of real time.
 */
public class Telemetry {
    private static final String LOGTAG = "Telemetry";

    public static long uptime() {
        return SystemClock.uptimeMillis();
    }

    public static long realtime() {
        return SystemClock.elapsedRealtime();
    }

    public static void HistogramAdd(String name, int value) {

    }

    public static void startUISession(final Session session, final String sessionNameSuffix) {
    }

    public static void startUISession(final Session session) {
    }

    public static void stopUISession(final Session session, final String sessionNameSuffix,
                                     final Reason reason) {
    }

    public static void stopUISession(final Session session, final Reason reason) {
    }

    public static void stopUISession(final Session session, final String sessionNameSuffix) {
    }

    public static void stopUISession(final Session session) {
    }

    public static void sendUIEvent(final Event event, final Method method, final long timestamp,
                                   final String extras) {
    }

    public static void sendUIEvent(final Event event, final Method method, final long timestamp) {
    }

    public static void sendUIEvent(final Event event, final Method method, final String extras) {
    }

    public static void sendUIEvent(final Event event, final Method method) {
    }

    public static void sendUIEvent(final Event event) {
    }

    public static void sendUIEvent(final Event event, final boolean eventStatus) {
    }


}
