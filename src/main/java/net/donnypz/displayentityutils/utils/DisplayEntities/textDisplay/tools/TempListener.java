package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class  TempListener implements Listener {
    private final List<HandlerList> eventHandlers = new ArrayList<>(List.of());

    public void register(){
        DisplayEntityPlugin.getInstance().getServer().getPluginManager().registerEvents(this,DisplayEntityPlugin.getInstance());
    }
    public void unregister(){
        for (HandlerList handlerList:eventHandlers){
            handlerList.unregister(this);
        }
    }
    public void addEventHandler(HandlerList handler){
        eventHandlers.add(handler);
    }
}

