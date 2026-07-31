# DisplayEntityUtils

[![Jitpack releases](https://jitpack.io/v/PZDonny/DisplayEntityUtils.svg)](https://jitpack.io/#PZDonny/DisplayEntityUtils)

[**Discord**](https://discord.gg/k3wtdG5fRZ) | [**WIKI**](https://jay-12.gitbook.io/displayentityutils)

**DisplayEntityUtils makes handling customizable entities simpler.**

### Supported Entities

- **Display Entities (Block, Item, Text)**
- **Interactions**
- **Mannequins**

### Dependencies

- **[PacketEvents](https://modrinth.com/plugin/packetevents)**

### BDEngine

- **[BDEngine](https://block-display.com)** is a modeling and animation engine designed for display entities,
  without the need for resource packs or mods
- The created models and animations can be transferred into your game world and reused with **DisplayEntityUtils**.

> DISCLAIMER: I DO NOT OWN **BDEngine**! ALL ISSUES AND SUGGESTIONS RELATED TO THAT PROJECT SHOULD BE BROUGHT TO THE
> OWNER, [ILLYSTRAY](https://illystray.com), IN THE BDENGINE DISCORD FOUND [HERE](https://discord.com/invite/VCeHfSd6Xa)

## Features
- Save groups and animations through Local Storage, MySQL, or MongoDB
- Interaction entity click commands

### [BDEngine](https://block-display.com)

- Save and load BDEngine Projects
- Import Models from BDEngine
- Convert BDEngine Datapacks into DEU groups & animations
- Save/Load/Play BDEngine Animations

### Transformation Gizmo

- **Individual entities**
  ![A transformation gizmo rotating and translating an iron sword item display](https://raw.githubusercontent.com/PZDonny/DisplayEntityUtils/master/docs/images/single_entity_gizmo.gif)

- **Groups/Models**
  ![A transformation gizmo scaling, rotating, and translating an atm model](https://raw.githubusercontent.com/PZDonny/DisplayEntityUtils/master/docs/images/group_gizmo.gif)
  > *ATM Model by [MAYAK](https://block-display.com/author/MAYAK/)*

### Animations

- **Animation**
  ![An old wizard waiting patiently](https://raw.githubusercontent.com/PZDonny/DisplayEntityUtils/master/docs/images/wizard_anim.gif)
  > *Old Wizard by [Mynoteet](https://block-display.com/author/mynoteet)*
  

- **Animation Camera**
  ![A shotgun reloading and the player's camera moves, following the animation](https://raw.githubusercontent.com/PZDonny/DisplayEntityUtils/master/docs/images/shotgun_anim.gif)
  > *Animated Ithaca 37 shotgun by [gameridk](https://block-display.com/author/gamerer)*

### Animation State Machines (DisplayControllers)

  ![A dodo bird model animating based on it's controlling entity's state](https://raw.githubusercontent.com/PZDonny/DisplayEntityUtils/master/docs/images/dodo.gif)
  
  > *An invisible chicken using a dodo bird, modeled from display entities*
  > 
  > *Dodo Bird Model and Animations by [Yegor_Mechanic](https://block-display.com/author/yegor_mechanic/)*

- Integration with **MythicMobs**
### Skript Syntax
[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=DisplayEntityUtils)

### And so much more!

## API

**v3.3.0+**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.PZDonny.DisplayEntityUtils</groupId>
    <artifactId>api</artifactId>
    <version>PLUGIN-VERSION</version>
    <scope>provided</scope>
</dependency>
```

**Before v3.3.0**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.PZDonny</groupId>
    <artifactId>DisplayEntityUtils</artifactId>
    <version>PLUGIN-VERSION</version>
    <scope>provided</scope>
</dependency>
```
