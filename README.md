# DisplayEntityUtils
[![](https://jitpack.io/v/PZDonny/DisplayEntityUtils.svg)](https://jitpack.io/#PZDonny/DisplayEntityUtils)

[Join the Discord Here!](https://discord.gg/k3wtdG5fRZ)

## [NEW WIKI](https://jay-12.gitbook.io/displayentityutils#dependencies)

### DisplayEntityUtils makes handling customizable entities simpler.


### Supported Entities
- **Block Displays**
- **Text Displays**
- **Item Displays**
- **Interactions**
- **Mannequins**

### Dependencies
- **[PacketEvents](https://modrinth.com/plugin/packetevents)**

### Supports BDEngine
- **[BDEngine](block-display.com)** is a modeling and animation engine designed for display entities,
  without the need for resource packs or mods
- The created models and animations can be transferred into your game world and reused with **DisplayEntityUtils**.
> DISCLAIMER: I DO NOT OWN **BDEngine**! ALL ISSUES AND SUGGESTIONS RELATED TO THAT PROJECT SHOULD BE BROUGHT TO THE OWNER, [ILLYSTRAY](https://illystray.com), IN THE BDENGINE DISCORD FOUND [HERE](https://discord.com/invite/VCeHfSd6Xa)

## What can DisplayEntityUtils do?
- Manipulate Display, Interaction, Mannequin Entities
- Save and load BDEngine models
- Manipulate entities in a model/group
- Save/Load/Play BDEngine Animations
- Interaction entity click commands
- Intergration with **MythicMobs**
- Create Animation State Machines
- Save groups and animations through Local Storage, MySQL, or MongoDB
- Integration with [Skript](https://github.com/SkriptLang/Skript)

[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=DisplayEntityUtils)

- And so much more!

## Showcases
- ### Using Models as equipment
  ![ezgif-1-873ca90c95 (1)](https://github.com/user-attachments/assets/ee189856-3459-49b8-b75c-4c18d1b43818)

- ### Creating Animation State Machines (DisplayControllers)
  ![](https://github.com/user-attachments/assets/594a4ffe-89cf-4e49-aff5-2f8c43ea21ad)
  >  
  > *Dodo Bird Model and Animations by [Yegor_Mechanic](https://block-display.com/author/yegor_mechanic/)*

## Plugin API

### API for v3.3.0+
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

### API for versions before v3.3.0
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
