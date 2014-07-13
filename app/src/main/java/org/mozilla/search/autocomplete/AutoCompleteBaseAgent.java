/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.autocomplete;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
abstract class AutoCompleteBaseAgent {

    /**
     * A message type that indicates there is a search query in the obj field.
     */
    private static final int MESSAGE_SEARCH_START = 0;

    /**
     * A message type that indicates that the background thread
     * has finished the query.
     */
    private static final int MESSAGE_SEARCH_FINISHED = 1;

    /**
     * Receives messages for the background thread.
     */
    private BackgroundHandler backgroundHandler;

    /**
     * Receives messages for the instantiation thread.
     */
    private ForegroundHandler foregroundHandler;

    /**
     * Supplied by the instantiator to receive the results of the search.
     */
    private AcceptsSearchResults callback;

    /**
     * A counter to determine if messages coming from the background
     * thread are still valid.
     */
    private final AtomicInteger messageNumber = new AtomicInteger(0);

    /**
     * Shared between the background thread and the foreground thread to
     * communicate the results of the search.
     */
    private final List<AutoCompleteModel> sharedSuggestionsBuffer;

    /**
     * @param foregroundLooper A messaging looper to be used in the instantiating (foreground)
     *                         thread. Actions in this thread (and through this looper) should
     *                         not block.
     * @param callback         A receiver to get updates about search results.
     * @throws AgentException Thrown when there is an issue creating the agent.
     */
    public AutoCompleteBaseAgent(Looper foregroundLooper, AcceptsSearchResults callback)
            throws AgentException {
        this(foregroundLooper, null, callback);
    }

    /**
     * @param foregroundLooper A messaging looper to be used in the instantiating (foreground)
     *                         thread. Actions in this thread (and through this looper) should
     *                         not block.
     * @param backgroundLooper A messaging looper to be used on the background (blocking) thread. If
     *                         this is null, then a new background thread will be started.
     * @param callback         A receiver to get updates about search results.
     * @throws AgentException Thrown when there is an issue creating the agent.
     */
    public AutoCompleteBaseAgent(Looper foregroundLooper, Looper backgroundLooper,
                                 AcceptsSearchResults callback) throws AgentException {
        this.callback = callback;

        sharedSuggestionsBuffer = new ArrayList<AutoCompleteModel>();

        foregroundHandler = new ForegroundHandler(foregroundLooper);

        if (backgroundLooper == null) {
            HandlerThread backgroundThread = new HandlerThread(getClass().getSimpleName());
            backgroundThread.start();
            backgroundLooper = backgroundThread.getLooper();
        }

        backgroundHandler = new BackgroundHandler(backgroundLooper);
    }

    public void startSearch(String queryString) {
        backgroundHandler.removeMessages(MESSAGE_SEARCH_START);
        backgroundHandler.sendMessage(
                backgroundHandler.obtainMessage(MESSAGE_SEARCH_START, queryString));
    }

    protected AcceptsSearchResults getCallback() {
        return callback;
    }

    public void shutdown() {
        callback = null;
        if (sharedSuggestionsBuffer != null) {
            sharedSuggestionsBuffer.clear();
        }
        if (backgroundHandler != null) {
            backgroundHandler.getLooper().quit();
            backgroundHandler = null;
        }
    }
    /**
     * Override this method to perform a search; the method will run in a background
     * thread, so clients are able to perform blocking operations.
     *
     * @param queryString The user's query.
     * @return A list of AutoCompleteModels, or null. If null is returned, the
     * foreground thread will not be alerted.
     */
    abstract List<AutoCompleteModel> processQueryInBackground(String queryString);

    /**
     * The mediator between the background thread and the instantiator's thread.
     */
    private class ForegroundHandler extends Handler {
        private ForegroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            /**
             * A note about potential race conditions:
             *
             * This handler wakes up when the background thread has finished populating
             * `sharedSuggestionsBuffer`. During the messaging gap, the background thread
             * could presumably complete a new search and clobber `sharedSuggestionsBuffer`.
             *
             * We tackle this problem by attaching a message number to the incoming message.
             * It is checked twice: once before we perform the work of copying the
             * result list, and once again before we notify the client of the update.
             *
             * If either check fails, we bail and process the next message.
             */
            if (msg.what == MESSAGE_SEARCH_FINISHED && msg.arg1 == messageNumber.intValue()) {
                List<AutoCompleteModel> results = new ArrayList<AutoCompleteModel>();

                synchronized (sharedSuggestionsBuffer) {
                    if (!sharedSuggestionsBuffer.isEmpty()) {
                        results.addAll(sharedSuggestionsBuffer);
                    }
                }

                /**
                 * Ensure the message was not invalidated in while we were creating
                 * a copy of the result list.
                 */
                if (msg.arg1 == messageNumber.intValue()) {
                    getCallback().onSuggestionsReceived(results, AutoCompleteBaseAgent.this);
                }
            }
        }
    }

    /**
     * A message handler for the background thread.
     *
     * The actual work (which may block) gets performed in the abstract method
     * `processQueryInBackground`.
     */
    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            List<AutoCompleteModel> tmp = processQueryInBackground((String) msg.obj);
            if (tmp != null && !tmp.isEmpty()) {
                synchronized (sharedSuggestionsBuffer) {
                    sharedSuggestionsBuffer.clear();
                    sharedSuggestionsBuffer.addAll(tmp);
                }
                foregroundHandler.removeMessages(MESSAGE_SEARCH_FINISHED);
                // `arg2` is a required param that we're not using, so it is set to 0.
                foregroundHandler.sendMessage(
                        foregroundHandler.obtainMessage(MESSAGE_SEARCH_FINISHED, messageNumber.incrementAndGet(), 0));
            }
        }
    }

    public class AgentException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
