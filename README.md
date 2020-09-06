[![](logo.png)](https://youtu.be/BeG5FqTpl9U)

# Dungeon Finder

👹 A command line tool that finds multi-dungeon configurations in a Minecraft world save.

[![](https://img.shields.io/github/license/rayferric/dungeon-finder?style=for-the-badge)](LICENSE)
[![](https://img.shields.io/github/v/release/rayferric/dungeon-finder?style=for-the-badge)](https://github.com/rayferric/dungeon-finder/releases)

## Features

### Find Dungeon Configurations of Any Size

- 📐 Welzl's Algorithm Integration
- 💻 Multithreading Support
- ⚙️ Custom Region Ranges

## Getting Started

### Installing

- Download the latest **[Dungeon Finder binary](https://github.com/rayferric/dungeon-finder/releases)**.

## Development

### Prerequisites

- 🔗 **[Git](https://git-scm.com)** Version Control System
- ☕ **[Java SE Development Kit 14](https://www.azul.com/downloads/zulu-community/?version=java-14&package=jdk)**
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

### Running

```bash
java -jar dungeon-finder-{version}-all.jar
```

## About

### Authors

- Ray Ferric (**[rayferric](https://github.com/rayferric)**)

### License

This project is licensed under the MIT License. See the **[LICENSE](LICENSE)** file for details.