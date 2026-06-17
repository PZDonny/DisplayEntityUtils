import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TagValidationTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "myTag",
            "12345678",
            "_under_score_tag_",
            "tag123",
            "TAG_456"
    })
    void validTags(String tag) {
        assertTrue(DisplayUtils.isValidTag(tag));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "my\ntag",
            "!myTag",
            "my,Tag",
            "my^tag",
            "my`tag",
            "Ab_C+",
            "!@#$%^&*()"

    })
    void invalidTags(String tag){
        assertFalse(DisplayUtils.isValidTag(tag));
    }
}
