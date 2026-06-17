package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DisplayControllerTests {

    Logger logger;

    @BeforeAll
    void beforeAll() {
        DisplayAnimationManager.createExpirationMap(-1);
        logger = Logger.getLogger("test");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "missing_animation_states",
            "missing_part_follow_properties",
            "old_vertical_offset",
            "robot"
    })
    void testValidConfigs(String configName) {
        DisplayController controller = testConfig(configName, "valid");
        assertNotNull(controller);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid_follow_type",
            "missing_controller_id",
            "missing_default_follow_properties",
            "missing_key_under_required_sect"
    })
    void testInvalidConfigs(String configName) {
        DisplayController controller =  testConfig(configName, "invalid");
        assertNull(controller);
    }

    private DisplayController testConfig(String configName, String packageName){
        DisplayController controller;
        try (InputStream stream = getClass().getResourceAsStream("/test/controllers/"+packageName+"/" + configName + ".yml")) {
            InputStreamReader reader = new InputStreamReader(stream);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            controller = DisplayControllerReader.read(
                    config,
                    "test",
                    false,
                    logger,
                    false
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return controller;
    }


}
