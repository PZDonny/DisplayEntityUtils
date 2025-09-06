package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.events.NullGroupLoaderEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;

import java.util.Map;

public class DisplayControllerUtils {

    public static void registerNullLoaderControllers(){
        for (Map.Entry<DisplayController, String> entry : DisplayController.grouplessControllers.entrySet()){
            DisplayController controller = entry.getKey();
            String groupTag = entry.getValue();
            NullGroupLoaderEvent e = new NullGroupLoaderEvent(controller, groupTag);
            e.callEvent();
            DisplayEntityGroup group = e.getGroup();
            if (group != null){
                controller.group = group;
                controller.register();
            }
        }
    }
}
