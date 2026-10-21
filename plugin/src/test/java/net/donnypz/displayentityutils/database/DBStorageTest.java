package net.donnypz.displayentityutils.database;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DBStorageTest {

    protected static final ComponentLogger log = ComponentLogger.logger();
    protected DisplayStorage storage;

    protected abstract void connect();

    protected abstract void wipeTables();

    @BeforeEach
    void beforeEach() {
        connect();
        wipeTables();
    }

    @AfterEach
    void afterEach() {
        if (storage instanceof DBDisplayStorage db){
            db.closeConnection();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "star_platinum",
            "wumpus"
    })
    public void displayEntityGroupPersistsInDatabase(String resource) {
        String resourcePath = "/test/groups/"+resource+".deg";
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            DisplayEntityGroup group = DisplayGroupManager.getGroup(stream);
            String tag = group.getTag();

            storage.saveDisplayEntityGroup(group, null);
            DisplayEntityGroup savedGroupFromDB = storage.getDisplayEntityGroup(tag);

            assertNotNull(savedGroupFromDB, "Failed to save group tagged \"" + tag + "\" to database");
            assertEquals(tag, savedGroupFromDB.getTag(), "Group tags are not the same");

        } catch (IOException e) {
            throw new RuntimeException("Failed to get group from resources", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "star_platinum",
            "wumpus"
    })
    public void displayEntityGroupDeletesFromDatabase(String resource){
        String resourcePath = "/test/groups/"+resource+".deg";
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            DisplayEntityGroup group = DisplayGroupManager.getGroup(stream);
            String tag = group.getTag();

            storage.saveDisplayEntityGroup(group, null);
            storage.deleteDisplayEntityGroup(tag, null);

            DisplayEntityGroup savedGroupFromDB = storage.getDisplayEntityGroup(tag);

            assertNull(savedGroupFromDB, "Failed to delete group tagged \"" + tag + "\" from database");

        } catch (IOException e) {
            throw new RuntimeException("Failed to get group from resources", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sp_sp_summon",
            "wave_anim_wave"
    })
    public void displayAnimationPersistsInDatabase(String resource) {
        String resourcePath = "/test/animations/"+resource+".deanim";
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            DisplayAnimation anim = DisplayAnimationManager.getAnimation(stream);
            String tag = anim.getAnimationTag();

            storage.saveDisplayAnimation(anim, null);
            DisplayAnimation savedAnimationFromDB = storage.getDisplayAnimation(tag);

            assertNotNull(savedAnimationFromDB, "Failed to save animation tagged \"" + tag + "\" to database");
            assertEquals(tag, savedAnimationFromDB.getAnimationTag(), "Animation tags are not the same");

        } catch (IOException e) {
            throw new RuntimeException("Failed to get animation from resources", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sp_sp_summon",
            "wave_anim_wave"
    })
    public void displayAnimationDeletesFromDatabase(String resource){
        String resourcePath = "/test/animations/"+resource+".deanim";
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            DisplayAnimation anim = DisplayAnimationManager.getAnimation(stream);
            String tag = anim.getAnimationTag();

            storage.saveDisplayAnimation(anim, null);
            storage.deleteDisplayAnimation(tag, null);

            DisplayAnimation savedAnimationFromDB = storage.getDisplayAnimation(tag);

            assertNull(savedAnimationFromDB, "Failed to delete animation tagged \"" + tag + "\" from database");

        } catch (IOException e) {
            throw new RuntimeException("Failed to get animation from resources", e);
        }
    }
}
