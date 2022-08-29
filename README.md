# GEM (GameEventsManager)

Welcome to GEM, a basic manager for game events with it's source code published. When available, you can download the built plugin as _GameEventsManager.jar_ and then load it as any other plugin.

# Usage

Please note that this plugin is concepted for a challange-like usage under friends on a rather private server. It is not ought to be used for a wide/public spectre of players.

When first loaded, the plugin will take longer to load (depending on memory allocated to the server) since it will create worlds for the specific events. More on that in the respective sections. These worlds will then be used as long as possible. If you play an event 2999 times, the world's space will be at it's limit. Please also note, that the world's memory size increases as you play more often. At some point, the plugin might automatically recreate the world to save disk space. If you are in need of more, you can safely delete the world on your own and then restart the server. 

Once loaded, you can then use the following commands to control the plugin:
- /event [event]
- /check [manager]

To stop an ongoing event, use: _/event [event] stop [seconds]_

As of now, a secure shutdown is not guaranteed when stopping the server (or crashing/restarting) while an event is running. This means that the spawn location will no be set properly on the start of a new event. If this is the case, please proceed to delete the respective world.

# Events

**RNG-Survival**

This game requires about 25 to 30 minutes to play. Basically, it is a normal survival competition where the target is to be the last one alive, but just with less skill required. This is due to some events (like breaking blocks or even walking) triggering a roll to happen which then results in different outcomes based on the number you roll. 20 is the best and 1 the worst. 

Phases: 
1. starting

   Time: 20 seconds | Players are notified that the game is starting soon. Once the timer expires, all players get teleported to the game world
2. active

   Time: up to 20 minutes | Players are playing survival in a 20.000 x 20.000 blocks area, once a player dies, he is out!
3. pre-deathmatch

   Time: 2 minutes | In this phase, players are granted immunity but they are also teleported back to spawn with a reduced 500x500 blocks game area. This phase is    intended to be used for last minute crafting, farming or inventory sorting.
4. deathmatch

   Time: up to 5 minutes | Players can no longer trigger rolls or break any blocks etc. The only option left is to fight as the border shrinks towards the center again. Last one standing wins!
5. end

   Time: 20 seconds | What the title says...
