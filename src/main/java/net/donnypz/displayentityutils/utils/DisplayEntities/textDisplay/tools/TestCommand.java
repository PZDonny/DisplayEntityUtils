package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;


import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvas;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)){
            return true;
        }
        switch (args[0]) {
            case "1" -> {

                TestCast cast = new TestCast();
                cast.cast(player);
                return true;
            }
            case "2" -> {

                TextDisplayCanvas screen = new TextDisplayCanvas();
                TextDisplayGifCanvasElement gif = new TextDisplayGifCanvasElement();

                gif.setFrames(TextDisplayTools.gifToPixelMatrixList(TextDisplayTools.getGifStream("https://cdn.pixabay.com/animation/2024/06/23/05/08/05-08-33-791_512.gif")));
                if (gif.getFrames().isEmpty()) {
                    player.sendMessage("Walla");
                }
                screen.addElement(gif);
                screen.spawnFlipped(player.getEyeLocation());

                return true;
            }
            case "3" -> {


                TextDisplayCanvas screen = new TextDisplayCanvas();
                TextDisplayStaticCanvasElement walla = new TextDisplayStaticCanvasElement();
                BufferedImage image = TextDisplayTools.getTexture("https://i.imgur.com/Ioq9Rzu.png");
                if (image == null) {
                    player.sendMessage("walla");
                    return true;
                }

                walla.setImage(TextDisplayTools.bufferedImageToPixelMatrix(image));
                screen.addElement(walla);
                screen.spawnFlipped(player.getEyeLocation());

                return true;
            }
            case "4" -> {

                TextDisplayCanvas screen = new TextDisplayCanvas();
                TextDisplayStaticButtonCanvasElement button = new TextDisplayStaticButtonCanvasElement(screen, 0, 0, new TextDisplayStaticButtonCanvasElement.ClickHandler<TextDisplayStaticButtonCanvasElement>() {
                    @Override
                    public void handle(TextDisplayStaticButtonCanvasElement self) {
                        screen.remove();
                    }
                });
                button.setImage(TextDisplayTools.bufferedImageToPixelMatrix(TextDisplayTools.getTexture(args[1])));
                screen.addElement(button);
                screen.spawnFlipped(player.getEyeLocation());

                return true;

            }
            case "5" -> {

                TextDisplayCanvas screen = new TextDisplayCanvas();
                TextDisplayStaticButtonCanvasElement buttonScreenElement = new TextDisplayStaticButtonCanvasElement(screen, 0, 0, new TextDisplayStaticButtonCanvasElement.ClickHandler<TextDisplayStaticButtonCanvasElement>() {
                    @Override
                    public void handle(TextDisplayStaticButtonCanvasElement self) {
                        self.getCanvas().remove();
                    }
                });
                buttonScreenElement.setImage(TextDisplayTools.bufferedImageToPixelMatrix(TextDisplayTools.getTexture("item/ender_eye.png")));
                screen.addElement(buttonScreenElement);
                screen.spawn(player.getEyeLocation());
                return true;
            }
            case "6" -> {

                return true;
            }
        }
        return true;
    }
}
