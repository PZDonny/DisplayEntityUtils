package net.donnypz.displayentityutils.listeners.textDisplays;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplayTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

public class TextDisplayLoadListener implements Listener {
    @EventHandler
    public void entityLoad(EntityAddToWorldEvent event){

        PersistentDataContainer dataContainer = event.getEntity().getPersistentDataContainer();
        if (dataContainer.has(TextDisplayTools.PERSISTENT_DATA_CONTAINER_KEY)){
            TextDisplayTools.loadFromEntity(event);
        }
    }
}
