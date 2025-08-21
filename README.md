# DisplayEntityUtils
[![](https://jitpack.io/v/PZDonny/DisplayEntityUtils.svg)](https://jitpack.io/#PZDonny/DisplayEntityUtils)

[Join the Discord Here!](https://discord.gg/k3wtdG5fRZ)

DisplayEntityUtils is a extensive plugin designed to make the usage of Display/Interaction entities simplier. It also supports BDEngine, a modeling/animation engine designed for display entities without the need for resource packs or mods.

## What you'll need
### Dependencies:
- **[PacketEvents](https://modrinth.com/plugin/packetevents)**
  - **_This dependency allows for usage of packet-based display entities, and is used throughout the plugin_** 

### BDEngine (Recommended)
BDEngine is what you'll use to create models and animations that can be transferred into your game world. Info on BDEngine and the Block Display Place, the collection of BDEngine models and animations, can be found [HERE](block-display.com)
> DISCLAIMER: I DO NOT OWN BDModels/BDEngine! ALL ISSUES AND SUGGESTIONS RELATED TO THAT PROJECT SHOULD BE BROUGHT TO THE OWNER, [ILLYSTRAY](https://illystray.com), IN THE BDENGINE DISCORD FOUND [HERE](https://discord.com/invite/VCeHfSd6Xa)

## What can DisplayEntityUtils do?
- Manipulate Individual Display/Interaction entities
- Manipulate Groups (BDEngine Models)
- Manipulate every part (Display Entity) within a model
- Manipulate Interaction Entities
- Integration with [Skript](https://github.com/SkriptLang/Skript)

[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=DisplayEntityUtils)

- Create and Play Animations through the plugin
- Convert Datapack Animations from BDEngine (preferred over creating in-game)
- Include Interaction Entities as part of groups/models
- Execute commands through Interaction Entities
- Retrieve models from BDEngine and spawn them in-game
- Intergration with **MythicMobs**
- Create Animation State Machines
- Save groups and animations through Local Storage, MySQL, or MongoDB
- And so much more!

## What more can it do for API/Skript users?
- Tools to manipulate Display and Interaction entity as individual entities
- Show/Hide groups to/from certain players
- Mount groups on entities (or vice-versa)
- Request models from BDEngine's website
- Create packets based groups/entities
- Play packet-based animations
- Theres too many things to list!

## Showcases
- Display Controllers (Using Models as cosmetic equipment) - With(out) MythicMobs
> ![ezgif-1-873ca90c95 (1)](https://github.com/user-attachments/assets/ee189856-3459-49b8-b75c-4c18d1b43818)

- Display Controllers (Creating Animation State Machines) - With(out) MythicMobs *(Credit goes to [Yegor_Mechanic](https://block-display.com/author/yegor_mechanic/) for all Dodo Bird Models/Animations)*
> ![](https://github.com/user-attachments/assets/594a4ffe-89cf-4e49-aff5-2f8c43ea21ad)

- API: Display Entities rotating smoothly with yaw changes, and Interaction entities pivoting around the center
> ![](https://github.com/user-attachments/assets/5c333cd4-71ba-4ad1-a631-f8ec648651f0)

## WIKI
### Access the wiki [HERE](https://github.com/PZDonny/DisplayEntityUtils/wiki)

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

### API for v3.3.0 and lower
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
