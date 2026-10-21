package net.donnypz.displayentityutils.database;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayAPIInitializer;
import net.donnypz.displayentityutils.managers.LoadMethod;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQLStorageTestIT extends DBStorageTest{

    //Testcontainers has a check to make sure official image is used
    //Below bypasses that
    static DockerImageName imageName = DockerImageName.parse("mysql/mysql-server:8.0")
            .asCompatibleSubstituteFor("mysql");

    static MySQLContainer<?> mysql = new MySQLContainer<>(imageName)
            .withDatabaseName("db");

    @BeforeAll
    void beforeAll() {
        DisplayAPIInitializer.initializeImpls(log);
        storage = DisplayAPI.getStorage(LoadMethod.MYSQL);
        mysql.start();
    }

    @AfterAll
    void afterAll() {
        mysql.stop();
    }

    @Override
    protected void connect() {
        ((MYSQLDisplayStorage) storage).connect(
                mysql.getJdbcUrl(),
                mysql.getUsername(),
                mysql.getPassword()
        );
    }


    @Override
    public void wipeTables() {
        try (Connection c = ((MYSQLDisplayStorage) storage).getConnection()) {
            Statement st = c.createStatement();

            st.execute("TRUNCATE TABLE " + MYSQLDisplayStorage.GROUP_TABLE);
            st.execute("TRUNCATE TABLE " + MYSQLDisplayStorage.ANIMATION_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
}
