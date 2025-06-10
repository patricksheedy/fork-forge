package forge.view;

import forge.deck.Deck;
import forge.deck.DeckImportController;
import forge.deck.DeckRecognizer.Token;
import forge.deck.io.DeckSerializer;
import forge.gui.interfaces.ICheckBox;
import forge.gui.interfaces.IComboBox;
import forge.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Command-line deck converter utility.
 * Converts various deck formats to Forge's DCK format.
 */
public class DeckConverter {
    
    /**
     * Convert a deck file to DCK format.
     * 
     * @param inputFile  the input deck file
     * @param outputFile the output DCK file
     * @return true if conversion was successful, false otherwise
     */
    public static boolean convertDeck(String inputFile, String outputFile) {
        return convertDeck(new File(inputFile), new File(outputFile));
    }
    
    /**
     * Convert a deck file to DCK format.
     * 
     * @param inputFile  the input deck file
     * @param outputFile the output DCK file
     * @return true if conversion was successful, false otherwise
     */
    public static boolean convertDeck(File inputFile, File outputFile) {
        try {
            // Read the input file
            if (!inputFile.exists()) {
                System.err.println("Error: Input file does not exist: " + inputFile.getAbsolutePath());
                return false;
            }
            
            System.out.println("Reading deck from: " + inputFile.getAbsolutePath());
            String inputContent = FileUtil.readFileToString(inputFile);
            
            if (inputContent == null || inputContent.trim().isEmpty()) {
                System.err.println("Error: Input file is empty or could not be read");
                return false;
            }
            
            // Create a simple deck import controller for command-line use
            DeckImportController controller = new DeckImportController(
                new SimpleCheckBox(false), // dateTimeCheck
                new SimpleComboBox<String>(), // monthDropdown
                new SimpleComboBox<Integer>(), // yearDropdown
                false // currentDeckNotEmpty
            );
            
            // Parse the input deck
            System.out.println("Parsing deck content...");
            List<Token> tokens = controller.parseInput(inputContent);
            
            if (tokens == null || tokens.isEmpty()) {
                System.err.println("Error: Could not parse deck or deck is empty");
                return false;
            }
            
            System.out.println("Found " + tokens.size() + " tokens");
            
            // Convert tokens to deck
            Deck deck = controller.accept();
            
            if (deck == null) {
                System.err.println("Error: Could not create deck from parsed tokens");
                return false;
            }
            
            // Set default name if deck doesn't have one
            if (deck.getName() == null || deck.getName().trim().isEmpty()) {
                String baseName = inputFile.getName();
                if (baseName.contains(".")) {
                    baseName = baseName.substring(0, baseName.lastIndexOf('.'));
                }
                deck.setName(baseName);
            }
            
            System.out.println("Successfully parsed deck: " + deck.getName());
            
            // Write the deck to output file
            System.out.println("Writing deck to: " + outputFile.getAbsolutePath());
            DeckSerializer.writeDeck(deck, outputFile);
            
            System.out.println("Conversion completed successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
      /**
     * Simple implementation of ICheckBox for command-line use
     */
    private static class SimpleCheckBox implements ICheckBox {
        private boolean selected;
        
        public SimpleCheckBox(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public boolean isSelected() {
            return selected;
        }
        
        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public String getToolTipText() {
            return null;
        }
        
        @Override
        public void setToolTipText(String text) {
            // No-op for command line
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            // No-op for command line
        }
        
        @Override
        public boolean isVisible() {
            return true;
        }
        
        @Override
        public void setVisible(boolean visible) {
            // No-op for command line
        }
    }
    
    /**
     * Simple implementation of IComboBox for command-line use
     */
    private static class SimpleComboBox<T> implements IComboBox<T> {
        private T selectedItem;
        private int selectedIndex = 0;
        
        @Override
        public T getSelectedItem() {
            return selectedItem;
        }
        
        @Override
        public void setSelectedItem(T item) {
            this.selectedItem = item;
        }
        
        @Override
        public int getSelectedIndex() {
            return selectedIndex;
        }
        
        @Override
        public void setSelectedIndex(int index) {
            this.selectedIndex = index;
        }
        
        @Override
        public void addItem(T item) {
            // No-op for command line
        }
        
        @Override
        public void removeAllItems() {
            // No-op for command line
        }
        
        @Override
        public String getToolTipText() {
            return null;
        }
        
        @Override
        public void setToolTipText(String text) {
            // No-op for command line
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            // No-op for command line
        }
        
        @Override
        public boolean isVisible() {
            return true;
        }
        
        @Override
        public void setVisible(boolean visible) {
            // No-op for command line
        }
    }
}
