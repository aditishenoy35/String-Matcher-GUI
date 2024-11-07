package matcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BruteForceMatcherGUI extends JFrame {
    private JTextField filePathField, patternField, replacementField;
    private JTextArea fileContentArea, matchProcessArea;
    private JButton loadButton, searchButton, replaceButton;

    public BruteForceMatcherGUI() {
        setTitle("Brute Force String Matcher");
        setSize(1000, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for file selection and pattern input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        filePathField = new JTextField(30); 
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
        fileContentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fileContentArea.setForeground(Color.BLACK); 
        JScrollPane scrollPane = new JScrollPane(fileContentArea);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for matching process display
        matchProcessArea = new JTextArea(15, 0);
        matchProcessArea.setEditable(false);
        matchProcessArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        matchProcessArea.setForeground(Color.BLACK);
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

            List<Integer> positions = bruteForceStringMatch(text, pattern);

            if (!positions.isEmpty()) {
                for (int position : positions) {
                    matchProcessArea.append("Pattern found at index: " + position + "\n");
                }
                highlightMatches(text, pattern);
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
            List<Integer> positions = bruteForceStringMatch(text, pattern);
            if (!positions.isEmpty()) {
                for (int position : positions) {
                    text = text.substring(0, position) + replacement + text.substring(position + pattern.length());
                }
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

    private List<Integer> bruteForceStringMatch(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && pattern.charAt(j) == text.charAt(i + j)) {
                j++;
            }
            if (j == m) {
                positions.add(i);
            }
        }
        return positions;
    }
    private void highlightMatches(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        matchProcessArea.append("Matching process:\n");
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && pattern.charAt(j) == text.charAt(i + j)) {
                j++;
            }
            matchProcessArea.append("Checking substring: " + text.substring(i, i + m) + "\n");
            if (j == m) {
                matchProcessArea.append("Match found at index: " + i + "\n");
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BruteForceMatcherGUI().setVisible(true);
        });
    }
}
