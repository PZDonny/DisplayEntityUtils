package net.donnypz.displayentityutils.command;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CommandFrameIdsTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "1,2",
            "9,3,5",
    })
    void tests(String idString){
        assertNotNull(CMDUtils.commaSeparatedIDs(idString));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "1.1",
            "1.1,2.5",
            "1-2",
            "abc123",
            "3,a"
    })
    void invalidThrowsIllegalArgumentException(String idString){
        assertThrows(IllegalArgumentException.class, () -> CMDUtils.commaSeparatedIDs(idString));
    }
}
