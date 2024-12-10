package net.donnypz.displayentityutils.command;

public enum Permission {
    HELP("deu.help"),
    RELOAD("deu.reload"),

    LIST_GROUPS("deu.list"),
    LIST_ANIMATIONS("deu.list"),

    GROUP_INFO("deu.group.info"),
    GROUP_SPAWN("deu.group.spawn"),
    GROUP_SAVE("deu.group.save"),
    GROUP_DELETE("deu.group.delete"),
    GROUP_DESPAWN("deu.group.despawn"),
    GROUP_SELECT("deu.group.select"),
    GROUP_SETTAG("deu.group.settag"),
    GROUP_TRANSFORM("deu.group.transform"),
    GROUP_BRIGHTNESS("deu.group.brightness"),
    GROUP_MERGE("deu.group.merge"),
    GROUP_UNGROUP_INTERACTIONS("deu.group.ungroupi"),
    GROUP_ADD_TARGET("deu.group.addtarget"),
    GROUP_CLONE("deu.group.clone"),
    GROUP_GLOW("deu.group.glow"),
    GROUP_GLOW_COLOR_SET("deu.group.glow.set"),
    GROUP_COPY_POSE("deu.group.copy"),
    GROUP_SET_SPAWN_ANIM("deu.group.spawnanim"),
    GROUP_TOGGLE_PERSIST("deu.group.persist"),


    PARTS_CYCLE("deu.parts.cycle"),
    PARTS_GLOW("deu.parts.glow"),
    PARTS_GLOW_SET_COLOR("deu.parts.glow.set"),
    PARTS_SELECT("deu.parts.select"),
    PARTS_TAG("deu.parts.tag"),
    PARTS_LIST_TAGS("deu.parts.tag.list"),
    PARTS_REMOVE("deu.parts.remove"),
    PARTS_TRANSLATE("deu.parts.translate"),
    PARTS_SEED_UUIDS("deu.parts.seed"),
    PARTS_SET_BLOCK("deu.parts.setblock"),


    TEXT_SET_TEXT("deu.text.settext"),
    TEXT_SET_FONT("deu.text.setfont"),
    TEXT_TOGGLE_SHADOW("deu.text.shadow"),
    TEXT_TOGGLE_SEE_THROUGH("deu.text.seethrough"),
    TEXT_SET_ALIGNMENT("deu.text.align"),
    TEXT_SET_LINE_WIDTH("deu.text.linewidth"),

    ITEM_TOGGLE_GLINT("deu.item.glint"),
    ITEM_SET("deu.item.setitem"),
    ITEM_SET_TRANSFORM("deu.item.settransform"),


    INTERACTION_ADD_CMD("deu.interaction.addcmd"),
    INTERACTION_REMOVE_CMD("deu.interaction.removecmd"),
    INTERACTION_LIST_CMD("deu.interaction.listcmd"),
    INTERACTION_DIMENSION("deu.interaction.dim"),
    INTERACTION_PIVOT("deu.interaction.pivot"),

    ANIM_NEW("deu.anim.new"),
    ANIM_SAVE("deu.anim.save"),
    ANIM_DELETE("deu.anim.delete"),
    ANIM_INFO("deu.anim.info"),
    ANIM_FRAME_INFO("deu.anim.info"),
    ANIM_ADD_FRAME("deu.anim.addframe"),
    ANIM_REMOVE_FRAME("deu.anim.removeframe"),
    ANIM_OVERWRITE_FRAME("deu.anim.overwriteframe"),
    ANIM_EDIT_FRAME("deu.anim.editframe"),
    ANIM_SHOW_FRAME("deu.anim.showframe"),
    ANIM_ADD_SOUND("deu.anim.addsound"),
    ANIM_REMOVE_SOUND("deu.anim.removesound"),
    ANIM_ADD_PARTICLE("deu.anim.addparticle"),
    ANIM_REMOVE_PARTICLE("deu.anim.removeparticle"),
    ANIM_REVERSE("deu.anim.reverse"),
    ANIM_TOGGLE_SCALE("deu.anim.scale"),
    ANIM_SET_TAG("deu.anim.settag"),
    ANIM_SET_FRAME_TAG("deu.anim.setframetag"),
    ANIM_PLAY("deu.anim.play"),
    ANIM_STOP("deu.anim.stop"),
    ANIM_SELECT("deu.anim.select"),

    BDENGINE_CONVERT_ANIM("deu.bdengine.convertanim"),
    BDENGINE_SPAWN_MODEL("deu.bdengine.spawnmodel");


    private final String permission;
    Permission(String permission){
        this.permission = permission;
    }

    String getPermission(){
        return permission;
    }
}
