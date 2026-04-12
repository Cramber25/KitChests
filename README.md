# Kit Chests

This is a super simple plugin which adds per-player kit chests to your minecraft server.

## Features:
- Create/Remove kit chests for your players
- Support for custom items using plugins like CraftEngine/ItemsAdder
- Each player can take items from the kit chest only once
- Modify plugin messages

## Commands:
- /kitchest create (`kitchests.admin`)
- /kitchest remove (`kitchests.admin`)

## Config:
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
