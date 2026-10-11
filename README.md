# EverythingESP

Minecraft Java Edition **26.2** client-side Fabric mod.

## Features
- Open the configuration menu with the **comma (,) key**.
- Enter a registry ID such as `diamond_ore`, `minecraft:chest`, `diamond`, `zombie`, or `item`.
- Highlight matching blocks in a cube up to 40 blocks from your position, including through walls.
- Highlight dropped item entities when their item type matches a target, and matching loaded entities using the vanilla glowing outline.
- Add multiple targets, clear the list, and enable/disable ESP.
- Track up to 512 blocks at once; block scanning is rate-limited.
- Keep settings in `config/everythingesp.properties`.

## Installation
Requires Minecraft **26.2**, Fabric Loader, Fabric API and Java 25 compatible with the game.
Download the `EverythingESP-26.2` artifact from [GitHub Actions](https://github.com/fixpot47/everything-esp/actions) and extract `everything-esp-1.0.1.jar` to your `mods` folder. Do not install the sources JAR.

## Limitations
Client-side code can only detect loaded, server-sent blocks/entities. Server anti-xray and unloaded chunks cannot be bypassed. Rendering uses a translucent fill rather than a narrow wireframe box. Glow color is controlled by the vanilla game.

## Credits
The through-wall rendering pipeline was adapted from the [Fabric Docs world rendering example](https://docs.fabricmc.net/develop/rendering/world), under CC BY-NC-SA 4.0.

## Support
[Buy us a Cookie](https://www.donationalerts.com/r/fixpot47)

## License
Copyright fixpot47. No general license granted for original code. The adapted rendering example retains its original CC BY-NC-SA 4.0 attribution and terms.
