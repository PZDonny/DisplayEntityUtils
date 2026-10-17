package net.donnypz.displayentityutils.utils.version;

import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VersionTests {

    private static MockedStatic<Bukkit> bukkitMock;

    @BeforeAll
    static void mockBukkit(){
        bukkitMock = Mockito.mockStatic(Bukkit.class);
        UnsafeValues unsafeValues = Mockito.mock(UnsafeValues.class);

        bukkitMock.when(Bukkit::getUnsafe).thenReturn(unsafeValues);
        Mockito.when(unsafeValues.getProtocolVersion()).thenReturn(Integer.MAX_VALUE);
    }

    @AfterAll
    static void closeBukkitMock(){
        bukkitMock.close();
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "2.3.2-ex-39a286e|2.3.2",
            "3.1.0-main|3.1.0",
            "3.5.0-PRE|3.5.0",
            "3.6.1-develop|3.6.1",
            "3.7.0-dev|3.7.0",
            "3.8.0-dev-1da350c|3.8.0",
    })
    void shouldCleanVersionString(String version) {
        String[] split = version.split("\\|");
        String dirtyVersion = split[0];
        String cleanVersion = split[1];

        String cleaned = VersionUtils.cleanVersionString(dirtyVersion);

        assertEquals(cleaned, cleanVersion);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "2.3.2-ex-39a286e",
            "3.1.0-main",
            "3.5.3-develop",
            "3.5.0-PRE",
            "3.7.0-dev",
    })
    void shouldBeDevVersion(String version) {
        boolean isDevVersion = VersionUtils.isDevVersion(version);
        assertTrue(isDevVersion, "Version is not a develop version: " + version);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "3.7.0",
            "3.6.0",
            "3.5.5",
            "3.5.1",
            "3.6.0",
    })
    void currentVersionShouldBeNewer(String comparedVersion) {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = getClass().getResourceAsStream("/plugin.yml")) {
            Map<String, Object> config = yaml.load(inputStream);
            String currentVersion = (String) config.get("version");

            boolean isNewer = VersionUtils.isOlderThan(comparedVersion, currentVersion);

            assertTrue(isNewer, "Current version (" + currentVersion + ") should be newer than compared version: " + comparedVersion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
