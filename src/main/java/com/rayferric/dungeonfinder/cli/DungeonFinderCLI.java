package com.rayferric.dungeonfinder.cli;

import com.rayferric.dungeonfinder.DungeonConfiguration;
import com.rayferric.dungeonfinder.DungeonFinder;
import com.rayferric.dungeonfinder.util.BlockPos;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
        Option minGroupSizeOption = new Option("c", "min-config-size", true, "minimum number of spawners per dungeon configuration (default: 3)");
        Option numThreadsOption = new Option("t", "num-threads", true, "number of threads used to process regions (default: 8, keep in mind that processing speed heavily depends on drive performance, this exact value gives off best benchmark performance overall on an HDD, higher values may theoretically help on SSDs and faster drives)");
        Option reportDelayOption = new Option("r", "report-delay", true, "delay between individual progress reports in milliseconds (default: 10000)");

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
        options.addOption(minGroupSizeOption);
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
        int minConfigSize = cmd.hasOption("min-config-size") ? Integer.parseInt(cmd.getOptionValue("min-config-size")) : 3;
        int numThreads = cmd.hasOption("num-threads") ? Integer.parseInt(cmd.getOptionValue("num-threads")) : 8;
        int reportDelay = cmd.hasOption("report-delay") ? Integer.parseInt(cmd.getOptionValue("report-delay")) : 10000;

        DungeonFinder dungeonFinder = new DungeonFinder();
        dungeonFinder.onStart(() -> System.out.println(String.format("Processing %d regions on %d threads...", (maxX - minX + 1) * (maxZ - minZ + 1), numThreads)));
        dungeonFinder.onFilter((numFound, timeElapsed) -> System.out.println(String.format("Found %d dungeons. (%d s)\nStarted proximity filtering...", numFound, timeElapsed / 1000)));
        dungeonFinder.onReport((numComplete, numTotal, timeElapsed) -> {
            double timeRemaining = 0;
            if(numComplete != 0)
                timeRemaining = (double)(timeElapsed * numTotal / numComplete - timeElapsed) / 1000.0;
            String remainingTimeStr = numComplete == 0 ? "?" : Long.toString((long)timeRemaining);

            double progress = (double)numComplete / numTotal * 100.0;

            System.out.println(String.format("%d %% - ETA %s s", (long)progress, remainingTimeStr));
        });

        List<DungeonConfiguration> dungeonConfigs = null;
        try {
            dungeonConfigs = dungeonFinder.run(worldDirectory, minX, maxX, minZ, maxZ, minConfigSize, numThreads, reportDelay);
        } catch(IOException e) {
            System.err.println("Failed to open world directory.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(String.format("Found %d dungeon configuration%s with size of at least %d:", dungeonConfigs.size(), dungeonConfigs.size() == 1 ? "" : "s", minConfigSize));
        for(DungeonConfiguration config : dungeonConfigs) {
            BlockPos center = config.getCenter();
            int spawnerCount = config.getDungeons().size();
            System.out.println(String.format("%d %d %d (%s spawner%s)", center.getX(), center.getY(), center.getZ(), spawnerCount, spawnerCount == 1 ? "" : "s"));
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
            System.out.println(String.format("Dungeon Finder %s", VERSION));
            System.exit(0);
        }
    }

    private final static String VERSION = "1.0.1";
}