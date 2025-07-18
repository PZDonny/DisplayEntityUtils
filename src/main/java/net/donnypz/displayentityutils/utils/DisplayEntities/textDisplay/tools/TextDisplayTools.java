package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayPixelBasicCanvas;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

public class TextDisplayTools {
    //
    public final static NamespacedKey PERSISTENT_DATA_CONTAINER_KEY = new NamespacedKey(DisplayEntityPlugin.getInstance(),"TextDisplayData");
    //
    public static Matrix4f transformationToMatrix(Transformation transformation) {
        Vector3f translationVec = transformation.getTranslation();
        Vector3f scaleVec = transformation.getScale();
        Quaternionf rotationQuat = transformation.getLeftRotation();
        Matrix4f matrix = new Matrix4f()
                .identity()
                .translate(translationVec)
                .rotate(rotationQuat)
                .scale(scaleVec);
        return matrix;
    }
    public static Transformation matrixToTransformation(Matrix4f matrix) {
        Vector3f translation = matrix.getTranslation(new Vector3f());

        // Extract scale
        Vector3f scale = new Vector3f(
                (float) Math.sqrt(matrix.m00() * matrix.m00() + matrix.m01() * matrix.m01() + matrix.m02() * matrix.m02()),
                (float) Math.sqrt(matrix.m10() * matrix.m10() + matrix.m11() * matrix.m11() + matrix.m12() * matrix.m12()),
                (float) Math.sqrt(matrix.m20() * matrix.m20() + matrix.m21() * matrix.m21() + matrix.m22() * matrix.m22())
        );

        // Remove scale from rotation matrix
        Matrix4f rotationMatrix = new Matrix4f(matrix);
        rotationMatrix.m00(rotationMatrix.m00() / scale.x);
        rotationMatrix.m01(rotationMatrix.m01() / scale.x);
        rotationMatrix.m02(rotationMatrix.m02() / scale.x);

        rotationMatrix.m10(rotationMatrix.m10() / scale.y);
        rotationMatrix.m11(rotationMatrix.m11() / scale.y);
        rotationMatrix.m12(rotationMatrix.m12() / scale.y);

        rotationMatrix.m20(rotationMatrix.m20() / scale.z);
        rotationMatrix.m21(rotationMatrix.m21() / scale.z);
        rotationMatrix.m22(rotationMatrix.m22() / scale.z);

        Quaternionf leftRotation = rotationMatrix.getUnnormalizedRotation(new Quaternionf());

        return new Transformation(translation, leftRotation, scale, new Quaternionf());
    }
    public static BufferedImage getTexture(String path) {
        BufferedImage image = null;

        // Try as file
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (IOException ignored) {}

        // Try as URL (with headers)
        try {
            URL url = URI.create(path).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
            connection.connect();
            try (InputStream input = connection.getInputStream()) {
                image = ImageIO.read(input);
            }
        } catch (Exception ignored) {}

        // Try Minecraft assets fallback
        if (image == null) {
            try {
                String fullVersion = org.bukkit.Bukkit.getVersion();
                String mcVersion = fullVersion.replaceAll(".*\\(MC: (.*?)\\).*", "$1");
                URL url = new URL("https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/" + mcVersion + "/assets/minecraft/textures/" + path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                try (InputStream input = connection.getInputStream()) {
                    image = ImageIO.read(input);
                }
            } catch (Exception ignored) {}
        }

        return image;
    }
    public static InputStream getGifStream(String path) {
        try {
            // Try as file path
            File file = new File(path);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        } catch (IOException ignored) {}

        try {
            // Try as URL
            URL url = URI.create(path).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "image/gif,image/png,image/*;q=0.8,*/*;q=0.5");
            connection.connect();
            return connection.getInputStream();
        } catch (Exception ignored) {}

        return null;
    }
   public static Matrix2dContainer<TextDisplayElementPixel> bufferedImageToPixelMatrix(BufferedImage image){
       if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
           BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
           Graphics2D g = converted.createGraphics();
           g.drawImage(image, 0, 0, null);
           g.dispose();
           image = converted;
       }
       Matrix2dContainer<TextDisplayElementPixel> pixels = new Matrix2dContainer<TextDisplayElementPixel>(TextDisplayElementPixel.class);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int RGBA = image.getRGB(x, y);
                int a = (RGBA >> 24) & 255;
                int r = (RGBA >> 16) & 255;
                int g = (RGBA >> 8) & 255;
                int b = RGBA & 255;
                r = Math.min(255, r);
                g = Math.min(255, g);
                b = Math.min(255, b);


                List<Integer> color = Arrays.asList(a, r, g, b);
                TextDisplayElementPixel pixel = new TextDisplayElementPixel(color,x,-y,pixels);
            }
        }
        return pixels;
    }
    public static List<Matrix2dContainer<TextDisplayElementPixel>> gifToPixelMatrixList(InputStream gifStream) {
        List<Matrix2dContainer<TextDisplayElementPixel>> frameMatrices = new ArrayList<>();
        try {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return frameMatrices;

            ImageReader reader = readers.next();
            ImageInputStream input = ImageIO.createImageInputStream(gifStream);
            reader.setInput(input, false);

            int frameCount = reader.getNumImages(true);

            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                BufferedImage frame = reader.read(frameIndex);

                if (frame.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage converted = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = converted.createGraphics();
                    g.drawImage(frame, 0, 0, null);
                    g.dispose();
                    frame = converted;
                }

                Matrix2dContainer<TextDisplayElementPixel> pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
                for (int y = 0; y < frame.getHeight(); y++) {
                    for (int x = 0; x < frame.getWidth(); x++) {
                        int RGBA = frame.getRGB(x, y);
                        int a = (RGBA >> 24) & 255;
                        int r = (RGBA >> 16) & 255;
                        int g = (RGBA >> 8) & 255;
                        int b = RGBA & 255;

                        List<Integer> color = Arrays.asList(a, r, g, b);
                        TextDisplayElementPixel pixel = new TextDisplayElementPixel(color, x, -y, pixels);
                    }
                }
                frameMatrices.add(pixels);
            }

            reader.dispose();
            input.close();
        } catch (Exception e) {
            DisplayEntityPlugin.getInstance().getLogger().severe("Error gifToPixelMatrixList "+e);
        }
        return frameMatrices;
    }
    public static BufferedImage scaleBufferedImage(BufferedImage image, int x, int y){
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = converted.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = converted;
        }
        double scaleX = (double) x / image.getWidth();
        double scaleY = (double) y / image.getHeight();
        AffineTransform transform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp scaleOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(image, new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB));
    }
    static private BufferedImage flattenBufferedImageToOpaque(BufferedImage image) {
        BufferedImage flat = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = flat.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, flat.getWidth(), flat.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return flat;
    }
    static public PersistentDataContainer getNewPersistentDataContainer(){
        return new ItemStack(Material.ACACIA_BOAT).getItemMeta().getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
    }
    static public void loadFromEntity(EntityAddToWorldEvent event){
        loadFromEntity(event.getEntity());
    }
    static public void loadFromEntity(Entity entity){


        Bukkit.getScheduler().scheduleSyncDelayedTask(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

            }
        },1);
    }
    static public void checkSpawnChunks(){
        List<World> worlds = Bukkit.getWorlds();
        List<Entity> entities = new ArrayList<>();
        for (World world: worlds){
            entities.addAll(world.getEntities());
        }
        for (Entity entity: entities){
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            if (dataContainer.has(TextDisplayTools.PERSISTENT_DATA_CONTAINER_KEY)){
                TextDisplayTools.loadFromEntity(entity);
                DisplayEntityPlugin.getInstance().getLogger().severe(entity.getEntityId()+"");
            }
        }
    }

    public static void optimizeScreen(Matrix2dContainer<TextDisplayPixelBasicCanvas> pixels){

    }
}
