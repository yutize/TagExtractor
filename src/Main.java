import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;

public class Main extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Set<String> stopWords;
    private Map<String, Integer> wordFrequency;

    public Main() {
        setTitle("Tag/Keyword Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton openFileButton = new JButton("Open File");
        JButton extractTagsButton = new JButton("Extract Tags");
        JButton saveTagsButton = new JButton("Save Tags");

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });
        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTags();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFileButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        stopWords = loadStopWords("stopwords.txt");
        wordFrequency = new HashMap<>();
    }

    private void openFile() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    processLine(line);
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.setText("File loaded: " + file.getName());
        }
    }

    private void processLine(String line) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!stopWords.contains(word)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }
    }

    private void extractTags() {
        List<Entry<String, Integer>> sortedTags = new ArrayList<>(wordFrequency.entrySet());
        sortedTags.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder tagsText = new StringBuilder();
        for (Entry<String, Integer> entry : sortedTags) {
            tagsText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        textArea.setText(tagsText.toString());
    }

    private void saveTags() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<String> loadStopWords(String filename) {
        Set<String> stopWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}
