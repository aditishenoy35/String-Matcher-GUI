package matcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HorspoolMatcherGUI extends JFrame {
    private JTextField filePathField, patternField, replacementField;
    private JTextArea fileContentArea, matchProcessArea;
    private JButton loadButton, searchButton, replaceButton;

    public HorspoolMatcherGUI() {
        setTitle("Horspool String Matcher");
        setSize(1000, 800); // Increased size of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for file selection and pattern input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Added spacing
        filePathField = new JTextField(30); // Increased width of text fields
        loadButton = new JButton("Load File");
        patternField = new JTextField(20);
        replacementField = new JTextField(20);
        searchButton = new JButton("Search");
        replaceButton = new JButton("Replace");

        topPanel.add(new JLabel("File Path:"));
        topPanel.add(filePathField);
        topPanel.add(loadButton);
        topPanel.add(new JLabel("Pattern:"));
        topPanel.add(patternField);
        topPanel.add(new JLabel("Replacement:"));
        topPanel.add(replacementField);
        topPanel.add(searchButton);
        topPanel.add(replaceButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for file content display
        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);
        fileContentArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Increased font size
        fileContentArea.setForeground(Color.BLACK); // Set text color to black
        JScrollPane scrollPane = new JScrollPane(fileContentArea);
        scrollPane.setPreferredSize(new Dimension(1000, 500)); // Set preferred size
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for matching process display
        matchProcessArea = new JTextArea(15, 0);
        matchProcessArea.setEditable(false);
        matchProcessArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Increased font size
        matchProcessArea.setForeground(Color.BLACK); // Set text color to black
        JScrollPane matchScrollPane = new JScrollPane(matchProcessArea);
        add(matchScrollPane, BorderLayout.SOUTH);

        // Add action listeners
        loadButton.addActionListener(new LoadButtonListener());
        searchButton.addActionListener(new SearchButtonListener());
        replaceButton.addActionListener(new ReplaceButtonListener());
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath = filePathField.getText();
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                fileContentArea.setText(content);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading file");
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            matchProcessArea.setText(""); // Clear previous results
            String text = fileContentArea.getText();
            String pattern = patternField.getText();

            int position = horspoolStringMatch(text, pattern);

            if (position != -1) {
                matchProcessArea.append("Pattern found at index: " + position + "\n");
                displayHorspoolShiftTable(pattern);
                highlightMatches(text, pattern, position);
            } else {
                matchProcessArea.append("Pattern not found\n");
            }
        }
    }
    private class ReplaceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            matchProcessArea.setText(""); // Clear previous results
            String text = fileContentArea.getText();
            String pattern = patternField.getText();
            String replacement = replacementField.getText();
            int position = horspoolStringMatch(text, pattern);
            if (position != -1) {
                text = text.substring(0, position) + replacement + text.substring(position + pattern.length());
                fileContentArea.setText(text);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePathField.getText()))) {
                    writer.write(text);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error writing to file");
                }
                matchProcessArea.append("Pattern replaced and file updated\n");
            } else {
                matchProcessArea.append("Pattern not found, nothing to replace\n");
            }
        }
    }

    private int[] generateHorspoolShiftTable(String pattern) {
        int m = pattern.length();
        int[] shiftTable = new int[256]; // ASCII characters range
        for (int i = 0; i < 256; i++) {
            shiftTable[i] = m; // Default shift value
        }
        for (int j = 0; j < m - 1; j++) {
            shiftTable[pattern.charAt(j)] = m - 1 - j;
        }
        return shiftTable;
    }

    private void displayHorspoolShiftTable(String pattern) {
        int[] shiftTable = generateHorspoolShiftTable(pattern);
        matchProcessArea.append("Horspool Shift Table:\n");
        for (int i = 0; i < 256; i++) {
            if (shiftTable[i] != pattern.length()) {
                matchProcessArea.append("Character '" + (char) i + "': " + shiftTable[i] + "\n");
            }
        }
    }

    private int horspoolStringMatch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] shiftTable = generateHorspoolShiftTable(pattern);
        int i = m - 1;
        while (i <= n - 1) {
            int k = 0;
            while (k <= m - 1 && pattern.charAt(m - 1 - k) == text.charAt(i - k)) {
                k++;
            }
            matchProcessArea.append("Checking substring: " + text.substring(Math.max(i - m + 1, 0), i + 1) + "\n");
            if (k == m) {
                return i - m + 1; // Match found
            } else {
                i += shiftTable[text.charAt(i)];
            }
        }
        return -1; // No match found
    }

    private void highlightMatches(String text, String pattern, int position) {
        int m = pattern.length();
        matchProcessArea.append("Matching process:\n");
        matchProcessArea.append("Checking substring: " + text.substring(position, position + m) + "\n");
        matchProcessArea.append("Match found at index: " + position + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HorspoolMatcherGUI().setVisible(true);
        });
    }
}