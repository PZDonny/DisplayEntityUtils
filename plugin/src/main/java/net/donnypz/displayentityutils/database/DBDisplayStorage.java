package net.donnypz.displayentityutils.database;

import org.slf4j.Logger;

public abstract class DBDisplayStorage extends DisplayStorage {

    public DBDisplayStorage(Logger logger) {
        super(logger);
    }

    /**
     * Check if this Database store is connected
     * @return a boolean
     */
    public abstract boolean isConnected();

    /**
     * Close this active database connection
     */
    public abstract void closeConnection();
}
