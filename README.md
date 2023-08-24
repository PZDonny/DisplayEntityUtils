
## About DisplayEntityUtils
This plugin was created to easy save and manipulate display entities and interactions without hassle
## **How do I make my own Models**
[Use this tool to create your own Models.](https://eszesbalint.github.io/bdstudio/editor)
***UNDERSTAND, I DO NOT OWN THIS TOOL! IT IS CREATED BY Eszes Bálint, and the repo for this project can be found [here](https://github.com/eszesbalint/bdstudio)***

## **What are the commands?**

**/mdis help** *(Permission: deu.help)*
- Opens the help page to list all commands

**/mdis partshelp** *(Permission: deu.help)*
- Opens the help page for commands that manipulate parts

**/mdis selectnearest [interaction-distance]** *(Permission: deu.select)*
- Selects the nearest Spawned DisplayEntityGroup from the player's location

**/mdis addinteractions [interaction-distance]** *(Permission: deu.addinteractions)*
- Add all interaction entities in the set distance, to the player's selected display entity group

**/mdis removeinteractions** *(Permission: deu.removeinteractions)*
- Remove all interaction entities in the player's selected display entity group

**/mdis settag [tag]** *(Permission: deu.settag)*
- Set the tag of the player's selected group to the specified tag

**/mdis gettag** *(Permission: deu.gettag)*
- Get the tag of the player's selected group

**/mdis highlight** *(Permission: deu.hightlight)*
- Highlight each individual entity in the player's selected display entity group

**/mdis clone** *(Permission: deu.clone)*
- Creates an identical copy of the player's selected display entity group at the player's location, respecting the pitch and yaw of the display entity group

**/mdis movehere** *(Permission: deu.movehere)*
- Teleports the player's selected display entity group to their location

**/mdis move [direction] [distance] [tick-duration]** *(Permission: deu.move)*
- Smoothly teleports the player's selected display entity group in the direction specified, stopping when its reached the distance, and takes the set amount of ticks to complete

**/mdis translate [direction] [distance] [tick-duration]** *(Permission: deu.translate)*
- Uses the translation of display entities to translate each display entity within the group in a direciton, stopping when the distance is reached, taking the set amount of ticks to complete. Interaction entities in the group are smoothly teleported with the same logic

**/mdis save [storage]** *(Permission: deu.save)*
- Save the player's selected display entity group to a storage location

**/mdis delete [tag] [storage]** *(Permission: deu.delete)*
- Delete a display entity group from a storage location

**/mdis spawn [tag] [storage]** *(Permission: deu.spawn)*
- Spawn a display entity group from a storage location

**/mdis despawn** *(Permission: deu.despawn)*
- Despawn the player's selected display entity group

**/mdis list [storage] [page-number]** *(Permission: deu.list)*
- List all of the saved display entity group from a storage location

**/mdis reload** *(Permission: deu.reload)*
- Reloads the plugin's config

**/mdis cycleparts [first | prev | next]** *(Permission: deu.cycleparts)*
- Cycle through the parts the player's selected display entity group

**/mdis setparttag [part-tag]** *(Permission: deu.setparttag)*
- Sets the part tag of a player's selected part

**/mdis getparttag** *(Permission: deu.getparttag)*
- Gets the part tag of a player's selected part

**/mdis selectparts [part-tag]** *(Permission: deu.selectparts)*
- Creates a part selection for a player, selecting only the parts in a player's selected display entity group with the part tag specified

**/mdis highlightparts** *(Permission: deu.highlight)*
- Highlights all the parts in a player's part selection

**/mdis translateparts [direction]** *(Permission: deu.translate)*
- Uses the translation of display entities to translate each display entity in a player's part selection in a direciton, stopping when the distance is reached, taking the set amount of ticks to complete. Interaction entities in the group are smoothly teleported with the same logic

## **How do I access the API?**

*Maven*
```
<dependency>
  <groupId>com.pzdonny</groupId>
  <artifactId>displayentityutils</artifactId>
  <version>VERSION</version>
  <scope>provided</scope>
</dependency>
```
