package net.donnypz.displayentityutils.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandUsageTests {

    record CommandCase(String arg1, String arg2, String commandSubArgs){ }

    DisplayEntityPluginCommand deuCmd;

    @BeforeAll
    void before(){
        this.deuCmd = new DisplayEntityPluginCommand();
    }

    Stream<CommandCase> shortCommandUsageCases(){
        return Stream.of(
                new CommandCase("group", "spawn", null),
                new CommandCase("group", "save", null),
                new CommandCase("group", "despawnat", null),
                new CommandCase("group", "ride", null),
                new CommandCase("anim", "addframe", null),
                new CommandCase("anim", "editframe", null),
                new CommandCase("anim", "frameinfo", null),
                new CommandCase("anim", "showpoints", null),
                new CommandCase("anim", "drawpoints", null),
                new CommandCase("anim", "drawpos", null),
                new CommandCase("text", "edit", null),
                new CommandCase("parts", "create", null),
                new CommandCase("mannequin", "mainhand", null),
                new CommandCase("mannequin", "togglegravity", null)
        );
    }

    @ParameterizedTest
    @MethodSource("shortCommandUsageCases")
    void shouldBeValidShortCommandUsage(CommandCase commandCase){
        String arg1 = commandCase.arg1;
        String arg2 = commandCase.arg2;

        DEUSubCommand subCmd = deuCmd
                .getCommand(arg1)
                .getCommand(arg2);

        assertEquals("/deu "+arg1+" "+arg2, subCmd.getShortCommandUsage());
    }

    Stream<CommandCase> fullCommandUsageCases() {
        return Stream.of(
                new CommandCase("group", "spawn", "<group-tag> <local | mysql | mongodb | all> [-packet]"),
                new CommandCase("group", "save", "<local | mysql | mongodb>"),
                new CommandCase("group", "despawnat", "<x> <y> <z> <distance> [-force] [-packet <include | only>] [-world <world_name>]"),
                new CommandCase("group", "ride", "<-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]"),
                new CommandCase("anim", "addframe", "<tick-delay> <tick-duration>"),
                new CommandCase("anim", "drawpoints", "<straight | arc> <point-tag> <start-frame> <end-frame> [points-per-frame]"),
                new CommandCase("parts", "create", "<block | item | text | interaction | mannequin> [-addtogroup]"),
                new CommandCase("mannequin", "togglegravity", "[-all <on | off>]")
        );
    }

    @ParameterizedTest
    @MethodSource("fullCommandUsageCases")
    void shouldBeValidFullCommandUsage(CommandCase commandCase){
        String arg1 = commandCase.arg1;
        String arg2 = commandCase.arg2;
        String cmdArgs = commandCase.commandSubArgs;

        DEUSubCommand subCmd = deuCmd
                .getCommand(arg1)
                .getCommand(arg2);

        assertEquals("/deu "+arg1+" "+arg2+" "+cmdArgs, subCmd.getCommandUsage());
    }
}
