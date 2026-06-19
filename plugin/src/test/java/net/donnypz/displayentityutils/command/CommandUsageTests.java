package net.donnypz.displayentityutils.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandUsageTests {

    DisplayEntityPluginCommand deuCmd;

    @BeforeAll
    void before(){
        this.deuCmd = new DisplayEntityPluginCommand();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "group,spawn",
            "group,save",
            "group,despawnat",
            "group,ride",
            "anim,addframe",
            "anim,editframe",
            "anim,frameinfo",
            "anim,showpoints",
            "anim,drawpoints",
            "anim,drawpos",
            "text,edit",
            "parts,create",
            "mannequin,mainhand",
            "mannequin,togglegravity"
    })
    void testCmd(String cmds){
        String[] split = cmds.split(",");
        DEUSubCommand subCmd = deuCmd
                .getCommand(split[0])
                .getCommand(split[1]);

        System.out.println();
        System.out.println(subCmd.getShortCommandUsage());
        System.out.println(subCmd.getCommandUsage());
    }
}
