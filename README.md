[![](logo.png)](https://youtu.be/BeG5FqTpl9U)

# Dungeon Finder

👹 A command line tool that finds multi-dungeon configurations in a Minecraft world save.

Works for all Minecraft versions from 1.2.1 through 26.1.2.

[![](https://img.shields.io/github/license/rayferric/dungeon-finder?style=for-the-badge)](LICENSE)
[![](https://img.shields.io/github/v/release/rayferric/dungeon-finder?style=for-the-badge)](https://github.com/rayferric/dungeon-finder/releases)

## Getting Started

### Installing

- Download the latest **[Dungeon Finder JAR](https://github.com/rayferric/dungeon-finder/releases)**.

### Usage

Find three-spawner configurations in a 16384x16384 area around (0, 0):

```bash
java -jar dungeon-finder.jar -w=".../saves/MyWorld" --min-x=-16 --min-z=-16 --max-x=15 --max-z=15
```

Find double-dungeon XP farms in a 2048x2048 region:

```bash
java -jar dungeon-finder.jar -w=".../saves/MyWorld" --min-x=-2 --min-z=-2 --max-x=1 --max-z=1 -c=2
```

List all spawners in a 1024x1024 square:

```bash
java -jar dungeon-finder.jar -w=".../saves/MyWorld" --min-x=-1 --min-z=-1 --max-x=0 --max-z=0 -c=1
```

Example output:

```bash
Processing 16 regions on 8 threads...
Found 151 dungeons. (10 s)
Started proximity filtering...
Found 5 dungeon configurations with size of at least 2:
(635 49 -198) skeleton + zombie
(309 44 -423) spider + zombie
(-814 47 605) zombie + skeleton
(318 31 596) skeleton + zombie
(860 35 568) zombie + spider
```

## Development

### Prerequisites

- 🔗 **[Git](https://git-scm.com)** Version Control System
- ☕ **[Java SE Development Kit 8](https://www.azul.com/downloads/zulu-community/?version=java-8-lts&package=jdk)**
- 🌏 A **[Pregenerated](https://www.curseforge.com/minecraft/mc-mods/chunkpregenerator)** Minecraft World

### Building

💻 Run the following commands in your terminal:

```bash
git clone https://github.com/rayferric/dungeon-finder.git
cd dungeon-finder
./gradlew shadowJar
```

A standalone output binary will be written to the `./build/libs/` directory.

You can use these to acquire the binary and libraries separately:

```bash
./gradlew build
./gradlew copyDependencies
```

The binary file will be generated in the `./build/libs/` directory.
Any required libraries will be copied to `./lib/`.

## About

### Authors

- Ray Ferric (**[rayferric](https://github.com/rayferric)**)
- im-BowenGu (**[im-BowenGu](https://github.com/im-BowenGu)**)

### License

This project is licensed under the MIT License. See the **[LICENSE](LICENSE)** file for details.