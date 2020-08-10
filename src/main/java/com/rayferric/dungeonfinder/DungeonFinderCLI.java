package com.rayferric.dungeonfinder;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;

public class DungeonFinderCLI {
    public static void main(String[] args) {
        Options options = new Options();

        Option worldDirectoryOption = new Option("w", "world-directory", true, "input world directory");
        Option minXOption = new Option(null, "min-x", true, "most negative region's X position");
        Option maxXOption = new Option(null, "max-x", true, "most positive region's X position");
        Option minZOption = new Option(null, "min-z", true, "most negative region's Z position");
        Option maxZOption = new Option(null, "max-z", true, "most positive region's Z position");
        Option minGroupSizeOption = new Option("c", "min-config-size", true, "minimum number of spawners per dungeon configuration (default: 3)");
        Option numThreadsOption = new Option("t", "num-threads", true, "number of threads used to process regions (default: 4)");
        Option reportDelayOption = new Option("r", "report-delay", true, "delay between individual progress reports in milliseconds (default: 10000)");

        worldDirectoryOption.setRequired(true);
        minXOption.setRequired(true);
        maxXOption.setRequired(true);
        minZOption.setRequired(true);
        maxZOption.setRequired(true);

        options.addOption(worldDirectoryOption);
        options.addOption(minXOption);
        options.addOption(maxXOption);
        options.addOption(minZOption);
        options.addOption(maxZOption);
        options.addOption(minGroupSizeOption);
        options.addOption(numThreadsOption);
        options.addOption(reportDelayOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        String worldFolderPath = cmd.getOptionValue("world-directory");
        int minX = Integer.parseInt(cmd.getOptionValue("min-x"));
        int maxX = Integer.parseInt(cmd.getOptionValue("max-x"));
        int minZ = Integer.parseInt(cmd.getOptionValue("min-z"));
        int maxZ = Integer.parseInt(cmd.getOptionValue("max-z"));
        int minConfigSize = cmd.hasOption("min-config-size") ? Integer.parseInt(cmd.getOptionValue("min-config-size")) : 3;
        int numThreads = cmd.hasOption("num-threads") ? Integer.parseInt(cmd.getOptionValue("num-threads")) : 4;
        int reportDelay = cmd.hasOption("report-delay") ? Integer.parseInt(cmd.getOptionValue("report-delay")) : 10000;

        List<DungeonConfiguration> dungeonConfigs = null;
        try {
            dungeonConfigs = DungeonFinder.run(worldFolderPath, minX, maxX, minZ, maxZ, minConfigSize, numThreads, reportDelay);
        } catch(IOException e) {
            System.err.println("Failed to open world directory.");
            e.printStackTrace();
            System.exit(1);
        } catch(InterruptedException e) {
            System.err.println("Worker thread has been interrupted.");
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
}
