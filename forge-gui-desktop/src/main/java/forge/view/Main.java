/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.view;

import forge.GuiDesktop;
import forge.Singletons;
import forge.error.ExceptionHandler;
import forge.gui.GuiBase;
import forge.gui.card.CardReaderExperiments;
import forge.util.BuildInfo;
import io.sentry.Sentry;

/**
 * Main class for Forge's swing application view.
 */
public final class Main {
    /**
     * Main entry point for Forge
     */
    public static void main(final String[] args) {
        Sentry.init(options -> {
            options.setEnableExternalConfiguration(true);
            options.setRelease(BuildInfo.getVersionString());
            options.setEnvironment(System.getProperty("os.name"));
            options.setTag("Java Version", System.getProperty("java.version"));
            options.setShutdownTimeoutMillis(5000);
            // these belong to sentry.properties, but somehow some OS/Zip tool discards it?
            if (options.getDsn() == null || options.getDsn().isEmpty())
                options.setDsn("https://87bc8d329e49441895502737c069067b@sentry.cardforge.org//3");
        }, true);

        // HACK - temporary solution to "Comparison method violates it's general contract!" crash
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        //Turn off the Java 2D system's use of Direct3D to improve rendering speed (particularly when Full Screen)
        System.setProperty("sun.java2d.d3d", "false");

        //Turn on OpenGl acceleration to improve performance
        //System.setProperty("sun.java2d.opengl", "True");

        //setup GUI interface
        GuiBase.setInterface(new GuiDesktop());

        //install our error handler
        ExceptionHandler.registerErrorHandling();

        // Start splash screen first, then data models, then controller.
        if (args.length == 0) {
            Singletons.initializeOnce(true);

            // Controller can now step in and take over.
            Singletons.getControl().initialize();
            return;
        }        // command line startup here
        String mode = args[0].toLowerCase();

        switch (mode) {
            case "sim":
                SimulateMatch.simulate(args);
                break;

            case "parse":
                CardReaderExperiments.parseAllCards(args);
                break;
                
            case "convert":
                convertDeck(args);
                break;

            case "server":
                System.out.println("Dedicated server mode.\nNot implemented.");
                break;

            default:
                System.out.println("Unknown mode.\nKnown modes are 'sim', 'parse', 'convert'");
                break;
        }        System.exit(0);
    }
    
    /**
     * Handle deck conversion command
     */
    private static void convertDeck(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: convert <input_file> <output_file>");
            System.out.println("  input_file:  Path to the deck file to convert (relative or absolute)");
            System.out.println("  output_file: Path for the output DCK file (relative or absolute)");
            System.out.println();
            System.out.println("Example: convert my_deck.txt my_deck.dck");
            System.out.println("Example: convert \"C:\\Users\\Name\\Documents\\deck.dec\" \"C:\\Users\\Name\\Documents\\converted.dck\"");
            return;
        }
        
        // Initialize Forge data model for deck conversion
        try {
            Singletons.initializeOnce(false);
        } catch (Exception e) {
            System.err.println("Failed to initialize Forge: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        String inputFile = args[1];
        String outputFile = args[2];
        
        System.out.println("Forge Deck Converter");
        System.out.println("Input:  " + inputFile);
        System.out.println("Output: " + outputFile);
        System.out.println();
        
        boolean success = DeckConverter.convertDeck(inputFile, outputFile);
        if (!success) {
            System.exit(1);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() throws Throwable {
        try {
            ExceptionHandler.unregisterErrorHandling();
        } finally {
            super.finalize();
        }
    }

    // disallow instantiation
    private Main() {
    }
}
