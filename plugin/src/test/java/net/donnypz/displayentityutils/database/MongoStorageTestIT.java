package net.donnypz.displayentityutils.database;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayAPIInitializer;
import net.donnypz.displayentityutils.managers.LoadMethod;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;


public class MongoStorageTestIT extends DBStorageTest{

    static DockerImageName imageName = DockerImageName.parse("mongo:9.0.4");
    static MongoDBContainer mongoDB = new MongoDBContainer(imageName);

    @BeforeAll
    void beforeAll() {
        DisplayAPIInitializer.initializeImpls(log);
        storage = DisplayAPI.getStorage(LoadMethod.MONGODB);
        mongoDB.start();
    }

    @AfterAll
    void afterAll() {
        mongoDB.stop();
    }

    @Override
    protected void connect() {
        ((MongoDisplayStorage) storage).connect(
                mongoDB.getConnectionString(),
                "db",
                "groups",
                "animations"
        );
    }


    @Override
    public void wipeTables() {
        MongoDisplayStorage.GROUP_COLLECTION.deleteMany(new Document());
        MongoDisplayStorage.ANIMATION_COLLECTION.deleteMany(new Document());
    }
}
