package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.particles;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class TextDisplayParticle {
    protected final List<String> tags = new ArrayList<>();
    protected TextDisplaySettings settings = new TextDisplaySettings();

    public abstract Location getLocation();

    public abstract void setLocation(Location location);

    public abstract void despawn();

    public abstract void remove();

    public abstract void setColor(List<Integer> color);
    public abstract List<Integer> getColor();
    public void setColor(int alpha, int red, int green, int blue) {
        setColor(List.of(alpha, red, green, blue));
    }

    public void setColor(int red, int green, int blue) {
        setColor(List.of(red, green, blue));
    }
    public List<String> getTags() {
        return tags;
    }
    public void addTag(String tag){
        tags.add(tag);
    }
    public void removeTag(String tag){
        tags.remove(tag);
    }

    protected abstract void setup();

    public abstract void spawn(Location location);
    public TextDisplaySettings getSettings() {
        return settings;
    }

    public void setSettings(TextDisplaySettings settings) {
        this.settings = settings;
        update();
    }
    protected abstract void update();
}
