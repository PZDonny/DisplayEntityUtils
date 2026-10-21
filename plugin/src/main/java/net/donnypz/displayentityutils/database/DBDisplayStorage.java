package net.donnypz.displayentityutils.database;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public abstract class DBDisplayStorage extends DisplayStorage {

    public DBDisplayStorage(ComponentLogger logger) {
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
