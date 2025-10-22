package net.donnypz.displayentityutils.utils.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


public final class TextDisplayDialog{

    private static final String TEXT = "deu_text_display_text";
    private static final String FONT = "deu_text_display_font";
    private static final String SHADOW = "deu_text_display_shadow";
    private static final String SEE_THROUGH = "deu_text_display_see_through";
    private static final String OPACITY = "deu_text_display_opacity";
    private static final String LINE_WIDTH = "deu_text_display_line_width";
    private static final String ALIGNMENT = "deu_text_display_alignment";
    private static final String DEFAULT_BACKGROUND = "deu_text_display_default_background";
    private static final String COLOR_CODE = "deu_text_display_color_code";
    private static final String BACKGROUND_COLOR = "deu_text_display_background_color";

    private static final DialogAction CONFIRM_ACTION = getConfirmAction();


    private TextDisplayDialog(){}

    /**
     * Send this dialog to a player
     * @param player the player
     * @param textDisplay the text display to edit
      * @param miniMessageFormatted whether the text display's text should be formatted as minimessage or ampersand
     */
    public static void sendDialog(@NotNull Player player, @NotNull TextDisplay textDisplay, boolean miniMessageFormatted){
        Dialog dialog = Dialog.create(builder -> {
            builder.empty()
                    .base(DialogBase.builder(Component.text("Edit a Text Display"))
                            .inputs(getInputs(textDisplay, miniMessageFormatted))
                            .build())
                    .type(DialogType.confirmation(ActionButton.create(Component.text("Confirm", NamedTextColor.GREEN),
                            Component.text("Set your selected text display's text", NamedTextColor.YELLOW),
                            200, CONFIRM_ACTION),
                            ActionButton.create(Component.text("Cancel", NamedTextColor.RED),
                                    Component.text("Cancel this action", NamedTextColor.YELLOW),
                                    200, null)));
        });
        player.showDialog(dialog);
    }

    /**
     * Send this dialog to a player
     * @param player the player
     * @param textDisplayPart the text display part to edit
     * @param miniMessageFormatted whether the text display's text should be formatted as minimessage or ampersand
     */
    public static void sendDialog(@NotNull Player player, @NotNull ActivePart textDisplayPart, boolean miniMessageFormatted){
        if (textDisplayPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) return;
        Dialog dialog = Dialog.create(builder -> {
            builder.empty()
                    .base(DialogBase.builder(Component.text("Edit a Text Display"))
                            .inputs(getInputs(textDisplayPart, miniMessageFormatted))
                            .build())
                    .type(DialogType.confirmation(ActionButton.create(Component.text("Confirm", NamedTextColor.GREEN),
                                    Component.text("Set your selected text display's text", NamedTextColor.YELLOW),
                                    200, CONFIRM_ACTION),
                            ActionButton.create(Component.text("Cancel", NamedTextColor.RED),
                                    Component.text("Cancel this action", NamedTextColor.YELLOW),
                                    200, null)));
        });
        player.showDialog(dialog);
    }

    /**
     * Send this dialog to a player
     * @param player the player
     * @param entityUUID the text display to edit
     * @param miniMessageFormatted whether the text display's text should be formatted as minimessage or ampersand
     */
    public static void sendDialog(@NotNull Player player, @NotNull UUID entityUUID, boolean miniMessageFormatted){
        Entity entity = Bukkit.getEntity(entityUUID);
        if (!(entity instanceof TextDisplay textDisplay)){
            player.sendMessage(Component.text("The provided entity is not a text display, or is not loaded!", NamedTextColor.RED));
            return;
        }
        sendDialog(player, textDisplay, miniMessageFormatted);
    }

    private static List<DialogInput> getInputs(TextDisplay textDisplay, boolean miniMessageFormatted){
        return List.of(
                getColorType(miniMessageFormatted),
                getTextInput(textDisplay.text(), miniMessageFormatted),
                getFont(textDisplay.text().font()),
                getAlignment(textDisplay.getAlignment()),
                getLineWidth(textDisplay.getLineWidth()),
                getOpacity(textDisplay.getTextOpacity()),
                getBackgroundColor(),
                getShadow(textDisplay.isShadowed()),
                getSeeThrough(textDisplay.isSeeThrough()),
                getDefaultBackground(textDisplay.isDefaultBackground())
        );
    }

    private static List<DialogInput> getInputs(ActivePart part, boolean miniMessageFormatted){
        Component text = part.getTextDisplayText();
        return List.of(
                getColorType(miniMessageFormatted),
                getTextInput(text, miniMessageFormatted),
                getFont(text.font()),
                getAlignment(part.getTextDisplayAlignment()),
                getLineWidth(part.getTextDisplayLineWidth()),
                getOpacity(part.getTextDisplayTextOpacity()),
                getBackgroundColor(),
                getShadow(part.isTextDisplayShadowed()),
                getSeeThrough(part.isTextDisplaySeeThrough()),
                getDefaultBackground(part.isTextDisplayDefaultBackground())
        );
    }

    private static DialogInput getColorType(boolean miniMessageFormatted){
        return DialogInput.singleOption(COLOR_CODE,
                        Component.text("Color Code"),
                        List.of(SingleOptionDialogInput.OptionEntry.create("mini", Component.text("Mini-Message"), miniMessageFormatted),
                                SingleOptionDialogInput.OptionEntry.create("ampersand", Component.text("Ampersand (&)"), !miniMessageFormatted)))
                .width(175)
                .build();
    }

    private static DialogInput getTextInput(Component text, boolean miniMessageFormatted){
        text = text.font(null);
        String initialString = miniMessageFormatted ? MiniMessage.miniMessage().serialize(text) : LegacyComponentSerializer.legacyAmpersand().serialize(text);
        return DialogInput.text(TEXT,
                512,
                Component.text("Enter Text"),
                true,
                initialString,
                Integer.MAX_VALUE,
                TextDialogInput.MultilineOptions.create(Integer.MAX_VALUE, 100));
    }

    private static DialogInput getFont(Key key){
        String font = key == null ? "default" : key.value();
        return DialogInput.singleOption(FONT,
                Component.text("Font"),
                List.of(SingleOptionDialogInput.OptionEntry.create("default", Component.text("Default"), font.equals("default")),
                        SingleOptionDialogInput.OptionEntry.create("alt", Component.text("Alt"), font.equals("alt")),
                        SingleOptionDialogInput.OptionEntry.create("uniform", Component.text("Uniform"), font.equals("uniform")),
                        SingleOptionDialogInput.OptionEntry.create("illageralt", Component.text("Illageralt"), font.equals("illageralt"))))
                .build();
    }

    private static DialogInput getAlignment(TextDisplay.TextAlignment alignment){
        String name = alignment.name();
        return DialogInput.singleOption(ALIGNMENT,
                        Component.text("Alignment"),
                        List.of(SingleOptionDialogInput.OptionEntry.create("CENTER", Component.text("Center"), name.equals("CENTER")),
                                SingleOptionDialogInput.OptionEntry.create("LEFT", Component.text("Left"), name.equals("LEFT")),
                                SingleOptionDialogInput.OptionEntry.create("RIGHT", Component.text("Right"), name.equals("RIGHT"))))
                .build();
    }

    private static DialogInput getLineWidth(int lineWidth){
        return DialogInput.numberRange(LINE_WIDTH, Component.text("Line Width"), 0.0f, 800.0f)
                .width(200)
                .step(1.0f)
                .initial((float) lineWidth)
                .build();
    }

    private static DialogInput getOpacity(byte opacity){ //Lower = more visible
        int originalOpacity = opacity < 0 ? opacity + 256 : opacity;
        float step = 0.05f;
        float percentage = (Math.round((originalOpacity/255f)/step)) * step;

        return DialogInput.numberRange(OPACITY, Component.text("Text Opacity"), 0.0f, 1.0f)
                .width(250)
                .step(step)
                .initial(percentage)
                .build();
    }


    private static DialogInput getBackgroundColor() {
        return DialogInput.text(BACKGROUND_COLOR,
                100,
                Component.text("Set Background Color (Minecraft Color, Hex, or \"transparent\")"),
                true,
                "",
                15,
                null);
    }

    private static DialogInput getShadow(boolean isShadowed){
        return DialogInput.bool(SHADOW, Component.text("Toggle Shadow"))
                .initial(isShadowed)
                .build();
    }

    private static DialogInput getSeeThrough(boolean isSeeThrough){
        return DialogInput.bool(SEE_THROUGH, Component.text("Toggle See Through"))
                .initial(isSeeThrough)
                .build();
    }

    private static DialogInput getDefaultBackground(boolean defaultBackground){
        return DialogInput.bool(DEFAULT_BACKGROUND, Component.text("Use Default Background"))
                .initial(defaultBackground)
                .build();
    }

    private static DialogAction getConfirmAction(){
        return DialogAction.customClick((view, audience) -> {
            Player p = (Player) audience;
            ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(p);
            if (selection == null){
                p.sendMessage(Component.text("Part selection lost!", NamedTextColor.RED));
                return;
            }
            ActivePart part = selection.getSelectedPart();
            if (part == null || part.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                p.sendMessage(Component.text("You do not have a text display selected!", NamedTextColor.RED));
                return;
            }

            //Set Text Display Properties
            Component text;
            String input = view.getText(TEXT);
            String colorType = view.getText(COLOR_CODE);
            switch (colorType){
                case "mini" -> text = MiniMessage.miniMessage().deserialize(input);
                case "ampersand" -> text = LegacyComponentSerializer.legacyAmpersand().deserialize(input);
                default -> text = Component.text(input);
            }

            boolean backgroundColorSuccess = false;
            part.setTextDisplayText(text.font(Key.key("minecraft", view.getText(FONT))));
            part.setTextDisplayAlignment(TextDisplay.TextAlignment.valueOf(view.getText(ALIGNMENT)));
            part.setTextDisplayTextOpacity(getOpacityAsByte(view.getFloat(OPACITY)));
            part.setTextDisplayLineWidth(view.getFloat(LINE_WIDTH).intValue());

            String bgColor = view.getText(BACKGROUND_COLOR);
            if (bgColor.isBlank()){
                backgroundColorSuccess = true;
            }
            else if (!bgColor.equals("transparent")){
                Color color = ConversionUtils.getColorFromText(bgColor);
                if (color != null){
                    part.setTextDisplayBackgroundColor(color);
                    backgroundColorSuccess = true;
                }
            }
            else{
                part.setTextDisplayBackgroundColor(Color.fromARGB(0));
                backgroundColorSuccess = true;
            }


            part.setTextDisplayShadowed(view.getBoolean(SHADOW));
            part.setTextDisplaySeeThrough(view.getBoolean(SEE_THROUGH));
            part.setTextDisplayDefaultBackground(view.getBoolean(DEFAULT_BACKGROUND));
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Text Display Updated!", NamedTextColor.GREEN)));
            if (!backgroundColorSuccess) p.sendMessage(Component.text("| Failed to set background color. Invalid color input.", NamedTextColor.YELLOW));
            ActiveGroup<?> selected = DEUUser.getOrCreateUser(p).getSelectedGroup();
            if (selected instanceof PacketDisplayEntityGroup pdeg && pdeg.isPersistent()){
                pdeg.update();
            }

        }, ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build());
    }

    private static byte getOpacityAsByte(float input){
        int opacity = Math.round(input * 255);

        if (opacity >= 4 && opacity <= 26) { //Adjusted for Minecraft Shader Values (Rendering is discarded)
            opacity = 25;
        }

        if (opacity > 127) { //Outside of byte range
            opacity -= 256;
        }

        return (byte) opacity;
    }
}
