package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Utils;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Active Part Data")
@Description("Get or change the data of an active part based on its part type.")
@Examples({"#Get Data",
            "set {_height} to {_spawnedpart}'s deu interaction height",
            "set {_text} to {_packetpart}'s deu text",
            "",
            "#Change Data",
            "set {_parts::*}'s deu text to \"&aMy New Text\"",
            "",
            "if {_spawnedpart}'s part type is block_display:",
            "\tset {_spawnedpart}'s deu block to oak_stairs[facing=east]"})
@Since("3.3.6")
public class ExprActivePartData extends SimplePropertyExpression<ActivePart, Object> {
    static {
        register(ExprActivePartData.class, Object.class, "deu (1¦block|2¦item|3¦text|4¦[interaction] width|5¦[interaction] height)", "activeparts");
    }

    int mark;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult){
        mark = parseResult.mark;
        super.init(expressions, matchedPattern, isDelayed, parseResult);
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    @Nullable
    public Object convert(ActivePart part) {
        switch (mark) {
            case 1 -> {
                return part.getBlockDisplayBlock();
            }
            case 2 -> {
                return part.getItemDisplayItem();
            }
            case 3 -> {
                Component comp = part.getTextDisplayText();
                return Utils.replaceChatStyles(LegacyComponentSerializer.legacySection().serialize(comp));
            }
            case 4 -> {
                return part.getInteractionWidth();
            }
            case 5 -> {
                return part.getInteractionHeight();
            }
            default -> {
                 return null;
            }
        }
    }

    @Override
    protected String getPropertyName() {
        return "active part data";
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (delta == null) return;
        for (ActivePart part : getExpr().getArray(event)){
            if (part == null) continue;
            Object obj = delta[0];
//          if (part == null){
//             return;
//          }
            switch(mark){
                //Block Displays
                case 1 -> {
                    if (mode == Changer.ChangeMode.RESET){
                        part.setBlockDisplayBlock(Material.AIR.createBlockData());
                        return;
                    }
                    if (obj instanceof BlockData bd){
                        part.setBlockDisplayBlock(bd);
                    }
                    else if (obj instanceof ItemType it){
                        part.setBlockDisplayBlock(it.getMaterial().createBlockData());
                    }
                }
                //Item Displays
                case 2 -> {
                    if (mode == Changer.ChangeMode.RESET){
                        part.setItemDisplayItem(new ItemStack(Material.AIR));
                    }
                    else{
                        if (obj instanceof ItemStack i){
                            part.setItemDisplayItem(i);
                        }
                        else if (obj instanceof BlockData bd){
                            part.setItemDisplayItem(new ItemStack(bd.getMaterial()));
                        }
                        else{
                            part.setItemDisplayItem(new ItemStack(((ItemType) obj).getMaterial()));
                        }
                    }
                }
                //Text Displays
                case 3 -> {
                    if (mode == Changer.ChangeMode.RESET){
                        part.setTextDisplayText(Component.empty());
                    }
                    else{
                        String value = delta == null ? null : String.join("\n", Arrays.copyOf(delta, delta.length, String[].class));
                        BaseComponent[] components = BungeeConverter.convert(ChatMessages.parseToArray(value));
                        part.setTextDisplayText(BungeeComponentSerializer.get().deserialize(components));
                    }
                }
                //Interaction Width
                case 4 -> {
                    if (mode == Changer.ChangeMode.RESET){
                        part.setInteractionWidth(1);
                    }
                    else{
                        part.setInteractionWidth(((Number) obj).floatValue());
                    }
                }
                //Interaction Height
                case 5 -> {
                    if (mode == Changer.ChangeMode.RESET){
                        part.setInteractionHeight(1);
                    }
                    else{
                        part.setInteractionHeight(((Number) obj).floatValue());
                    }
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(BlockData.class, ItemStack.class, ItemType.class, String.class, Number.class);
        }
        return null;
    }
}
