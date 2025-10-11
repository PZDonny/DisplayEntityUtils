package net.donnypz.displayentityutils.command;

public enum Permission {
    HELP("deu.help"),
    RELOAD("deu.reload"),

    LIST_GROUPS("deu.list"),
    LIST_ANIMATIONS("deu.list"),

    GROUP_INFO("deu.group.info"),
    GROUP_SPAWN("deu.group.spawn"),
    GROUP_SAVE("deu.group.save"),
    GROUP_TO_PACKET("deu.group.topacket"),
    GROUP_DELETE("deu.group.delete"),
    GROUP_MARK_PACKET_GROUPS("deu.group.markpg"),
    GROUP_CHUNK_PACKET_GROUP_VISIBILITY("deu.group.pgvisbility"),
    GROUP_DESPAWN("deu.group.despawn"),
    GROUP_SELECT("deu.group.select"),
    GROUP_SETTAG("deu.group.settag"),
    GROUP_TRANSFORM("deu.group.transform"),
    GROUP_RIDE("deu.group.ride"),
    GROUP_DISMOUNT("deu.group.dismount"),
    GROUP_BRIGHTNESS("deu.group.brightness"),
    GROUP_MERGE("deu.group.merge"),
    GROUP_UNGROUP_INTERACTIONS("deu.group.ungroupi"),
    GROUP_ADD_TARGET("deu.group.addtarget"),
    GROUP_CLONE("deu.group.clone"),
    GROUP_GLOW("deu.group.glow"),
    GROUP_GLOW_COLOR("deu.group.glow.set"),
    GROUP_COPY_POSE("deu.group.copy"),
    GROUP_SET_SPAWN_ANIM("deu.group.spawnanim"),
    GROUP_TOGGLE_PERSIST("deu.group.persist"),
    GROUP_BILLBOARD("deu.group.billboard"),
    GROUP_VIEWRANGE("deu.group.viewrange"),
    GROUP_CULLING("deu.group.culling"),
    GROUP_WORLD_EDIT("deu.group.worldedit"),


    PARTS_INFO("deu.parts.info"),
    PARTS_CREATE("deu.parts.create"),
    PARTS_CYCLE("deu.parts.cycle"),
    PARTS_GLOW("deu.parts.glow"),
    PARTS_GLOW_COLOR("deu.parts.glow.set"),
    PARTS_SELECT("deu.parts.select"),
    PARTS_TAG("deu.parts.tag"),
    PARTS_LIST_TAGS("deu.parts.tag.list"),
    PARTS_REMOVE("deu.parts.remove"),
    PARTS_TRANSLATE("deu.parts.translate"),
    PARTS_TRANSFORM("deu.parts.transform"),
    PARTS_SET_BLOCK("deu.parts.setblock"),
    PARTS_BILLBOARD("deu.parts.billboard"),
    PARTS_BRIGHTNESS("deu.parts.brightness"),
    PARTS_VIEWRANGE("deu.parts.viewrange"),


    TEXT_EDIT("deu.text.edit"),
    TEXT_SET_TEXT("deu.text.set"),
    TEXT_SET_FONT("deu.text.font"),
    TEXT_TOGGLE_SHADOW("deu.text.shadow"),
    TEXT_TOGGLE_SEE_THROUGH("deu.text.seethrough"),
    TEXT_SET_ALIGNMENT("deu.text.align"),
    TEXT_SET_LINE_WIDTH("deu.text.linewidth"),
    TEXT_OPACITY("deu.text.opacity"),
    TEXT_BACKGROUND("deu.text.background"),

    ITEM_TOGGLE_GLINT("deu.item.glint"),
    ITEM_SET("deu.item.setitem"),
    ITEM_TRANSFORM("deu.item.transform"),


    INTERACTION_ADD_CMD("deu.interaction.addcmd"),
    INTERACTION_REMOVE_CMD("deu.interaction.removecmd"),
    INTERACTION_LIST_CMD("deu.interaction.listcmd"),
    INTERACTION_DIMENSION("deu.interaction.dim"),
    INTERACTION_RESPONSIVE("deu.interaction.responsive"),
    INTERACTION_PIVOT("deu.interaction.pivot"),
    INTERACTION_SPAWN("deu.interaction.spawn"),
    INTERACTION_INFO("deu.interaction.info"),

    ANIM_NEW("deu.anim.new"),
    ANIM_SAVE("deu.anim.save"),
    ANIM_DELETE("deu.anim.delete"),
    ANIM_INFO("deu.anim.info"),
    ANIM_FRAME_INFO("deu.anim.info"),
    ANIM_USE_FILTER("deu.anim.usefilter"),
    ANIM_UNFILTER("deu.anim.unfilter"),
    ANIM_ADD_FRAME("deu.anim.addframe"),
    ANIM_REMOVE_FRAME("deu.anim.removeframe"),
    ANIM_OVERWRITE_FRAME("deu.anim.overwriteframe"),
    ANIM_EDIT_FRAME("deu.anim.editframe"),
    ANIM_SHOW_FRAME("deu.anim.showframe"),
    ANIM_ADD_FRAME_POINT("deu.anim.addpoint"),
    ANIM_REMOVE_FRAME_POINT("deu.anim.removepoint"),
    ANIM_DRAW_FRAME_POINTS("deu.anim.drawpoints"),
    ANIM_COPY_FRAME_POINT("deu.anim.copypoint"),
    ANIM_MOVE_FRAME_POINT("deu.anim.movepoint"),
    ANIM_ADD_SOUND("deu.anim.addsound"),
    ANIM_REMOVE_SOUND("deu.anim.removesound"),
    ANIM_ADD_PARTICLE("deu.anim.addparticle"),
    ANIM_REVERSE("deu.anim.reverse"),
    ANIM_TOGGLE_SCALE("deu.anim.scale"),
    ANIM_TOGGLE_TEXTURE_CHANGES("deu.anim.texturechanges"),
    ANIM_SET_TAG("deu.anim.settag"),
    ANIM_SET_FRAME_TAG("deu.anim.setframetag"),
    ANIM_PLAY("deu.anim.play"),
    ANIM_STOP("deu.anim.stop"),
    ANIM_SELECT("deu.anim.select"),
    ANIM_PREVIEW("deu.anim.preview"),

    BDENGINE_CONVERT_DATAPACK("deu.bdengine.convertdp"),
    BDENGINE_SPAWN_MODEL("deu.bdengine.spawnmodel");


    private final String permission;
    Permission(String permission){
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
