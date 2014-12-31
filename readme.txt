COMPILING AND RUNNING THE CS151 GAME PROJECT
--------------------------------------------

1. Getting the project to work

The code requires Java 7 to compile.  If you are using an earlier version of
Java, you must modify the code that requires Java 7 (switch on String, and
diamond operator constructors).

The main() method is in GameMain.java.

The program expects the two helper files "item-list.txt" and "monster-list.txt"
to be in the same directory as GameMain.  If they aren't, modify the filename
constants in GameMain.java.

For compilation, both Eclipse and direct command-line compilation were used.

This code was tested extensively on Windows 7, but none of us have reliable
access to a different hardware environment; a bug fix that was applied to
GameMain might not work reliably elsewhere.  If this turns out to be a problem,
delete lines 25 to 37 in GameMain, and edit line 23 to your desired name.

2. Actually playing the game

This isn't an action game; the game takes no actions unless you do so.

The display should be reasonably self-explanatory, but looks like this:

Top left: help (this includes all the commands summarized below).
Top center: map
Top right: your character information (name, health, etc.)
Bottom: game messages not in one of the other windows

You are represented by the "@", which is always in the middle of the map.
Movement is done with the arrow keys, and you can move diagonally on the number
keypad with Home/End/PgUp/PgDn.

Monsters are represented on the map by letters of the alphabet (the current
build only includes enough to prove that the code is extensible; you can modify
this by editing monster-list.txt).  Moving into a monster will attempt an
attack against it.  They will attempt to do the same to you (there is an AI,
but it's very simple).

You don't start out with any items, but can find a few scattered about the
dungeon.  You can pick them up by walking on top of them and using the ","
command.  Currently, only weapons ")" and armor "[" are supported, of only
enough types to prove that the code is extensible; you can modify this as well
by editing items-list.txt.  You have to actually equip them to make use of
them, which can be done with the "E" command.  You can also drop items back
onto the floor; the game will tell you if you already have a particular weapon
or armor.

The game supports saving and reloading your game.  To save your game, type "S"
and then "@" to confirm the command; this will exit the game automatically.
Loading a game must be done when first starting up the game; this will be
done automatically if the save file from a previous session is present and you
enter the same name you used before.

You can also pick up gold "$" scattered throughout the dungeon.  This is
currently the closest thing to score once the game ends.  Other than by
getting killed or quitting, you can also exit the dungeon by finding stairs
on each level.  They are represented by ">", and you can go down them by
typing ">".  Once you go downstairs three times, the game will end in victory.

3. Getting the unit tests to work

The unit tests are contained in the following source files, located in the
default package:

CreatureHeroMonsterTest.java
ItemArmorWeaponTest.java
LevelTest.java
TileTest.java

These require the JUnit 4 library to work.