package com.rayferric.dungeonfinder.cli;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.rayferric.dungeonfinder.DungeonConfiguration;
import com.rayferric.dungeonfinder.DungeonFinder;
import com.rayferric.dungeonfinder.Spawner;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;

public class DungeonFinderCLI {
    public static void main(String[] args) {
        Options options = new Options();

        Option helpOption = new Option("h", "help", false, "display help message");
        Option versionOption = new Option("v", "version", false, "print application name and version info");
        Option worldDirectoryOption = new Option("w", "world-directory", true, "path to the input world's directory");
        Option minXOption = new Option(null, "min-x", true, "most negative region's X position");
        Option maxXOption = new Option(null, "max-x", true, "most positive region's X position");
        Option minZOption = new Option(null, "min-z", true, "most negative region's Z position");
        Option maxZOption = new Option(null, "max-z", true, "most positive region's Z position");
        Option minConfigSizeOption = new Option("c", "min-config-size", true,
                "minimum number of spawners per dungeon configuration (default: 3)");
        Option maxDistOption = new Option("d", "max-dist", true,
                "maximum distance from center of configuration to a single spawner (default: 16)");
        Option numThreadsOption = new Option("t", "num-threads", true,
                "number of threads used to process regions (default: 8, keep in mind that processing speed heavily depends on drive performance, this exact value gives off best benchmark performance overall on an HDD, higher values may theoretically help on SSDs and faster drives)");
        Option reportDelayOption = new Option("r", "report-delay", true,
                "delay between individual progress reports in milliseconds (default: 10000)");

        worldDirectoryOption.setRequired(true);
        minXOption.setRequired(true);
        maxXOption.setRequired(true);
        minZOption.setRequired(true);
        maxZOption.setRequired(true);

        options.addOption(helpOption);
        options.addOption(versionOption);
        options.addOption(worldDirectoryOption);
        options.addOption(minXOption);
        options.addOption(maxXOption);
        options.addOption(minZOption);
        options.addOption(maxZOption);
        options.addOption(minConfigSizeOption);
        options.addOption(maxDistOption);
        options.addOption(numThreadsOption);
        options.addOption(reportDelayOption);

        parseHelpAndVersionOptions(args.clone(), options);

        CommandLine cmd = null;
        try {
            cmd = (new DefaultParser()).parse(options, args);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            (new HelpFormatter()).printHelp("dungeon-finder", options);
            System.exit(1);
        }

        String worldDirectory = cmd.getOptionValue("world-directory");
        int minX = Integer.parseInt(cmd.getOptionValue("min-x"));
        int maxX = Integer.parseInt(cmd.getOptionValue("max-x"));
        int minZ = Integer.parseInt(cmd.getOptionValue("min-z"));
        int maxZ = Integer.parseInt(cmd.getOptionValue("max-z"));
        int minConfigSize =
                cmd.hasOption("min-config-size") ? Integer.parseInt(cmd.getOptionValue("min-config-size")) : 3;
        int maxDist = cmd.hasOption("max-dist") ? Integer.parseInt(cmd.getOptionValue("max-dist")) : 16;
        int numThreads = cmd.hasOption("num-threads") ? Integer.parseInt(cmd.getOptionValue("num-threads")) : 8;
        int reportDelay = cmd.hasOption("report-delay") ? Integer.parseInt(cmd.getOptionValue("report-delay")) : 10000;

        DungeonFinder dungeonFinder = new DungeonFinder();
        dungeonFinder.onStart(() -> {
            int numRegions = (maxX - minX + 1) * (maxZ - minZ + 1);
            System.out.printf("Processing %s region%s on %s thread%s...\n", numRegions, numRegions == 1 ? "" : "s",
                    numThreads, numThreads == 1 ? "" : "s");
        });
        dungeonFinder.onFilter((numFound, timeElapsed) -> System.out.printf(
                "Found %s dungeons. (%s s)\nStarted proximity filtering...\n", numFound, timeElapsed / 1000));
        dungeonFinder.onReport((numComplete, numTotal, timeElapsed) -> {
            double timeRemaining = 0;
            if(numComplete != 0)
                timeRemaining = (double)(timeElapsed * numTotal / numComplete - timeElapsed) / 1000.0;
            String remainingTimeStr = numComplete == 0 ? "?" : Long.toString((long)timeRemaining);

            double progress = (double)numComplete / numTotal * 100.0;

            System.out.printf("%s %% - ETA %s s\n", (long)progress, remainingTimeStr);
        });

        List<DungeonConfiguration> dungeonConfigs = null;
        try {
            dungeonConfigs = dungeonFinder
                    .run(worldDirectory, minX, maxX, minZ, maxZ, minConfigSize, maxDist, numThreads, reportDelay);
        } catch(IOException e) {
            System.err.println("Failed to open world directory.");
            e.printStackTrace();
            System.exit(1);
        }


        int numFound = dungeonConfigs.size();
        System.out.printf("Found %s dungeon configuration%s with size of at least %s%s\n", numFound,
                numFound == 1 ? "" : "s", minConfigSize, numFound == 0 ? "." : ":");
        for(DungeonConfiguration config : dungeonConfigs) {
            List<Spawner> spawners = config.getSpawners();
            Point3d center = config.getCenter();
            long x = (long)Math.floor(center.getCoord(0));
            long y = (long)Math.floor(center.getCoord(1));
            long z = (long)Math.floor(center.getCoord(2));
            int numSpawners = spawners.size();
            System.out.printf("(%s, %s, %s) ", x, y, z);
            for(int i = 0; i < numSpawners; i++) {
                Spawner spawner = spawners.get(i);
                if(i != 0)
                    System.out.print(" + ");
                System.out.print(spawner.getType().toString());
            }
            System.out.print('\n');
        }
    }

    private static void parseHelpAndVersionOptions(String[] args, Options baseOptions) {
        Options options = new Options();

        for(Option option : baseOptions.getOptions()) {
            Option opt = (Option)option.clone();
            opt.setRequired(false);
            options.addOption(opt);
        }

        CommandLine cmd = null;
        try {
            cmd = (new DefaultParser()).parse(options, args);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            (new HelpFormatter()).printHelp("dungeon-finder", options);
            System.exit(1);
        }

        if(cmd.hasOption("help")) {
            (new HelpFormatter()).printHelp("dungeon-finder", options);
            System.exit(0);
        }

        if(cmd.hasOption("version")) {
            System.out.printf("Dungeon Finder %s\n", VERSION);
            System.exit(0);
        }
    }

    private final static String VERSION = "1.2.2";
}