# TownOutlaw
An unofficial Towny add-on plugin that adds risk to being an outlaw of a town.
The two main features are jail on death, and exposed vulnerability.

### Jail on Death
Jail on death is a basic feature where if you are outlawed in a town, and you die within
that town, you will be sent to that town's jail for a configurable amount of time. You
will not be allowed to leave the jail until time is up. 

#### Jail Protection
After your jail time is up, you will be granted protection from jail on death for a
configurable amount of time. Use that time to escape the town or get a quick attack
in.

### Exposed Vulnerability
This feature is very simple. It disables abilities such as flying or Essentials
god within an outlawed town.

### Added Commands
This plugin adds two commands to Towny.

#### Resident Outlaw
The first command is `/resident outlaw` (permission `towny.command.resident.outlaw`) which allows residents
to see which towns they are outlawed in.

#### TownyAdmin ReloadTownOutlaw
The second command is `/townyadmin reloadtownoutlaw` (permission `townoutlaw.reload`) which reloads
the plugin including the configuration.

## Dependencies
Please make sure you have the following installed on your server before installing
this plugin.
* Spigot (or a fork of it) 1.17+: May work on older versions, but not tested.
* Java 16
* [Towny](https://www.spigotmc.org/resources/towny-advanced.72694/) 0.97.1+
* [Essentials](https://www.spigotmc.org/resources/essentialsx.9089/) 2.18+ (Optional): Disable Essentials god-mode in outlawed towns.
* [TempFly](https://www.spigotmc.org/resources/tempfly.54987/) 3.1.0+ (Optional): Disable fly in outlawed-towns.

## Installing
Download the plugin from the [release page](https://github.com/UrbanMC-Devs/TownOutlaw/releases/latest) and install it on the plugins directory
of the server.

## Building
This project is a simple maven project and can be built by executing `mvn clean package`.

**NOTE**: This plugin will not build without the TempFly dependency. Since the TempFly
jar is not hosted on any maven repo, you must first download the TempFly jar and install
it in your local maven repo.
