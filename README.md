# KitChests

KitChests lets you place physical loot chests in your world that act as per-player starter kits or rewards.

Instead of having players type `/kit`, they just open a chest at spawn. The catch is that the loot is instanced per-player: if Player A empties the chest, it stays fully stocked for Player B who hasn't opened it yet. Each player can only take the items once.

## Features
* **Per-Player Instances:** Players can only loot the chest once. Once they take the items, the chest appears empty to them forever, but stays full for everyone else.
* **Easy In-Game Setup:** No need to configure kits in a file. Just put the items you want into a normal chest, look at it, and type `/kitchest create`.
* **Custom Item Support:** It saves exactly what is in the chest, meaning it supports items with custom NBT data from plugins like ItemsAdder or CraftEngine.
* **Grief Protection:** KitChests cannot be broken or blown up.

## Commands & Permissions
* `/kitchest create` - Look at a filled chest and run this to convert it into a KitChest. *(Permission: `kitchests.admin`)*
* `/kitchest remove` - Look at a KitChest to revert it back to a normal chest and wipe its data. *(Permission: `kitchests.admin`)*

## Configuration (config.yml)
```yaml
messages:
  create_success: "<green>KitChest created successfully!</green>"
  remove_success: "<green>KitChest removed.</green>"
  not_looking_at_chest: "<red>You must be looking at a chest.</red>"
  already_kitchest: "<red>This is already a KitChest.</red>"
  not_a_kitchest: "<red>This is not a KitChest.</red>"
  no_permission: "<red>You do not have permission.</red>"
  invalid_usage: "<red>Usage: /kitchest <create|remove></red>"
  cannot_break: "<red>You cannot break a KitChest! Use /kitchest remove first.</red>"
```
