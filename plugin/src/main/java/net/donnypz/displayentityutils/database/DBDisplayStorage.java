package net.donnypz.displayentityutils.database;

public interface DBDisplayStorage extends DisplayStorage {

    /**
     * Check if this Database store is connected
     * @return a boolean
     */
    boolean isConnected();

    /**
     * Close this active database connection
     */
    void closeConnection();
}
