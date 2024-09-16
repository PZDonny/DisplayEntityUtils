# DisplayEntityUtils (LAST UPDATE | 9/10/2024) 
[![](https://jitpack.io/v/PZDonny/DisplayEntityUtils.svg)](https://jitpack.io/#PZDonny/DisplayEntityUtils)

DisplayEntityUtils is a extensive plugin designed for use with BDEngine, a modeling/animation engine designed for display entities without the need for resource packs or mods.

## What you'll need: BDEngine
BDEngine is what you'll use to create models and animations that can be transferred into your game world. Info on BDEngine and the BDCatalog, collection of BDEngine models and animations, can be found [HERE](block-display.com)
> DISCLAIMER: I DO NOT OWN BDENGINE! ALL ISSUES AND SUGGESTIONS RELATED TO THAT PROJECT SHOULD BE BROUGHT TO THE OWNER, [ILLYSTRAY](https://illystray.com), IN THE BDENGINE DISCORD FOUND [HERE](https://discord.com/invite/VCeHfSd6Xa)
## What can DisplayEntityUtils do?
- Manipulate Groups (BDEngine Models)
- Manipulate every part (Display Entity) within a model
- Manipulate Interaction Entities
- Include Interaction Entities as part of groups
- Execute commands through Interaction Entities
- Create Animations through the plugin
- Convert Animations from BDEngine (preferred over creating in-game)
- Retrieve models from BDEngine and spawn them in-game
- Save groups and animations through Local Storage, MySQL, or MongoDB
- And so much more!

## What can it do for developers?
- Tools to manipulate Display and Interaction entity as individual entities
- Show/Hide groups to/from certain players
- Play looping animations
- Create animation state machines
- Mount groups on entities (or vice-versa)
- Theres too many things to list!

## Video Showcases
> [Conversion of Datapack Animations into Animations usable for the plugin](https://streamable.com/6ly7r8)

> [API: Display Entities rotating smoothly with yaw changes, and Interaction entities pivoting around the center.](https://streamable.com/jqun87)

> [API: Animation State Machines](https://streamable.com/m2jagj)


## WIKI [WIP]
Access the wiki [HERE](https://github.com/PZDonny/DisplayEntityUtils/wiki)

## How can you access the API?
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>net.donnypz</groupId>
  <artifactId>displayentityutils</artifactId>
  <version>2.4.1</version>
  <scope>provided</scope>
</dependency>
```
