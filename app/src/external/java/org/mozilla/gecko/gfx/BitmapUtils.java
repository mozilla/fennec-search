/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko.gfx;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

/**
 * Stubbed out version of BitmapUtils for use with standalone search activity.
 */
public final class BitmapUtils {
    private static final String LOGTAG = "GeckoBitmapUtils";

    private BitmapUtils() {}

    public static Bitmap decodeByteArray(byte[] bytes) {
        return decodeByteArray(bytes, null);
    }

    public static Bitmap decodeByteArray(byte[] bytes, BitmapFactory.Options options) {
        return decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static Bitmap decodeByteArray(byte[] bytes, int offset, int length, BitmapFactory.Options options) {
        if (bytes.length <= 0) {
            throw new IllegalArgumentException("bytes.length " + bytes.length
                    + " must be a positive number");
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(bytes, offset, length, options);
        } catch (OutOfMemoryError e) {
            Log.e(LOGTAG, "decodeByteArray(bytes.length=" + bytes.length
                    + ", options= " + options + ") OOM!", e);
            return null;
        }

        if (bitmap == null) {
            Log.w(LOGTAG, "decodeByteArray() returning null because BitmapFactory returned null");
            return null;
        }

        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            Log.w(LOGTAG, "decodeByteArray() returning null because BitmapFactory returned "
                    + "a bitmap with dimensions " + bitmap.getWidth()
                    + "x" + bitmap.getHeight());
            return null;
        }

        return bitmap;
    }

    /**
     * Decodes a bitmap from a Base64 data URI.
     *
     * @param dataURI a Base64-encoded data URI string
     * @return        the decoded bitmap, or null if the data URI is invalid
     */
    public static Bitmap getBitmapFromDataURI(String dataURI) {
        if (dataURI == null) {
            return null;
        }

        final String base64 = dataURI.substring(dataURI.indexOf(',') + 1);
        try {
            byte[] raw = Base64.decode(base64, Base64.DEFAULT);
            return BitmapUtils.decodeByteArray(raw);
        } catch (Exception e) {
            Log.e(LOGTAG, "exception decoding bitmap from data URI: " + dataURI, e);
        }
        return null;
    }
}

