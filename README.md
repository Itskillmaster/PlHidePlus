<div align="center">
  
  <h1>🛡️ PlHidePlus</h1>
  <p><strong>The ultimate, premium-style plugin hider and command blocker for modern Minecraft servers.</strong></p>

  <p>
    <img src="https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=java" alt="Java 17+" />
    <img src="https://img.shields.io/badge/API-1.13%20to%201.20%2B-blue?style=flat-square" alt="API" />
    <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-lightgrey?style=flat-square" alt="Platform" />
    <img src="https://img.shields.io/badge/Release-v1.0.0-brightgreen?style=flat-square" alt="Version" />
  </p>

</div>

---

## ✨ Features

* 🎨 **Interactive Control Panel:** A fully clickable, modern in-game GUI to manage blocked commands and edit server messages without ever touching the config file.
* 🚫 **Flawless Tab-Complete Blocker:** Completely removes blocked commands from the player's client tab-completion tree. They can't execute it, and they can't even guess it.
* 🌈 **Hex Color & MiniMessage Support:** Say goodbye to boring `&` color codes. Fully supports modern RGB gradients, hex colors, and hover/click events via the Adventure MiniMessage API.
* ⚡ **Live Command Management:** Add, remove, or view blocked commands on the fly using intuitive chat commands or the GUI.
* 🔔 **Smart Update Checker:** Automatically fetches the latest releases from GitHub and notifies server operators upon joining if a new version is available.
* 🛡️ **Bypass Protection:** Server operators (OPs) and players with the right permissions bypass all restrictions smoothly.

## ⚙️ Commands & Permissions

To use these commands or open the GUI, players must be **OP** or have the `plhideplus.use` permission. 
*Aliases: `/plhp`*

| Command | Description |
| :--- | :--- |
| `/plhideplus` | Opens the main interactive Control Panel (GUI). |
| `/plhideplus add <command>` | Adds a new command to the blocklist. |
| `/plhideplus remove <command>` | Removes a command from the blocklist. |
| `/plhideplus reload` | Reloads `config.yml` and updates all cached settings. |
| `/plhideplus info` | Displays plugin version and active statistics. |

> **Note:** Do not include the forward slash `/` when adding or removing commands. (Example: `/plhideplus add version`).

## 🚀 Installation

1. Download the latest `PlHidePlus.jar` from the [Releases](../../releases) tab.
2. Drop the file into your server's `plugins` folder.
3. Restart your server to generate the default configuration.
4. Join the game and type `/plhideplus` to start customizing!

## 📝 Requirements

* **Java:** 17 or higher.
* **Server Software:** Paper, Purpur, or Spigot.
* **Minecraft Version:** 1.13.x up to the latest 1.20.x+.

---
<div align="center">
  Made with ❤️ by <a href="https://github.com/Itskillmaster">Itskillmaster</a>
</div>
