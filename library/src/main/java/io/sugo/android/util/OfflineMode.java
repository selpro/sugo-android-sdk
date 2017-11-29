package io.sugo.android.util;

/**
 * Implement this to allow Sugo behave in-sync with your current custom offline logic
 */
public interface OfflineMode {

    /**
     * Returns true if offline-mode is active on the client. When true Sugo will not start
     * new connections, but current active connections will not be interrupted.
     *
     * @return true if offline mode is active, false otherwise
     */
    boolean isOffline();

}
