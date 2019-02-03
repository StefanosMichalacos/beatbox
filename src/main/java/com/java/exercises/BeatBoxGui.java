package com.java.exercises;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;


class BeatBoxGui {
    private JFrame beatBoxWindow, loadFrame;
    private JPanel buttonPanel, labelPanel, centerPanel;
    private JButton startButton, saveButton, loadButton;
    private Dimension upperCheckPanelSize;
    private String[] instrumentList = {" INSTRUMENTS", "   Bass Drum :", "   Closed Hi-Hat :", "   Open Hi-Hat :", "   Acoustic Snare :", "   Crash Cymbal :", "   Hand Clap :", "   High Tom :", "   High Bongo :", "   Maracas :", "   Whistle :", "   Low Conga :", "   Cow Bell :", "   Vibraslap :", "   Low-mid Tom :", "   High Agogo :", "   Open Hi Conga :"};
    private ArrayList<JCheckBox> checkBoxList, savedList;
    private BeatBoxMidi bbm;
    private boolean hasStarted;
    private boolean areChecked;
    private Color backgroundColor = new Color(39, 39, 63);
    private boolean beatIsPlaying;
    private String name;
    private File saveFile;


    BeatBoxGui() {
        beatBoxWindow = new JFrame("lets Make Some Music");
        beatBoxWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setWindowParameters();
        buildButtonPanel();
        beatBoxWindow.getContentPane().add(BorderLayout.EAST, buttonPanel);
        buildLabelPanel();
        beatBoxWindow.getContentPane().add(BorderLayout.WEST, labelPanel);
        buildCheckBoxPanel();
        beatBoxWindow.getContentPane().add(BorderLayout.CENTER, centerPanel);
        setWindowParameters();
    }

    Dimension getUpperCheckPanelSize() {
        double windowHeight = beatBoxWindow.getHeight();
        upperCheckPanelSize = new Dimension(0, (int) (windowHeight / 18));
        return upperCheckPanelSize;
    }

    void setWindowParameters() {
        beatBoxWindow.setPreferredSize(new Dimension(680, 650));
        beatBoxWindow.pack();
        beatBoxWindow.setLocationRelativeTo(null);
        beatBoxWindow.setVisible(true);
        beatBoxWindow.setResizable(false);
    }

    void buildButtonPanel() {
        startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JButton tempoUpButton = new JButton("Tempo Up");
        JButton tempoDownButton = new JButton("Tempo Down");
        saveButton = new JButton("Save Sequence");
        loadButton = new JButton("Load Sequence");
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(130, 130));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        setButtonPreferences(startButton, new Dimension(65, 26), new startButtonListener(), new Dimension(12, 14));
        setButtonPreferences(stopButton, new Dimension(65, 26), new stopButtonListener(), new Dimension(0, 5));
        setButtonPreferences(tempoUpButton, new Dimension(108, 26), new tempoUpButtonListener(), new Dimension(0, 5));
        setButtonPreferences(tempoDownButton, new Dimension(108, 26), new tempoDownButtonListener(), new Dimension(0, 5));
        setButtonPreferences(saveButton, new Dimension(128, 26), new saveButtonListener(), new Dimension(0, 5));
        setButtonPreferences(loadButton, new Dimension(128, 26), new loadButtonListener(), new Dimension(0, 5));
        buttonPanel.setBackground(backgroundColor);
    }

    void setButtonPreferences(JButton button, Dimension buttonDimensions, ActionListener listener, Dimension gapDimensions) {
        button.setMaximumSize(buttonDimensions);
        button.addActionListener(listener);
        buttonPanel.add(Box.createRigidArea(gapDimensions));
        buttonPanel.add(button);
    }

    void buildLabelPanel() {
        labelPanel = new JPanel(); // Here I should make use of a 'for' loop, to import them
        labelPanel.setPreferredSize(new Dimension(130, 400));
        labelPanel.setLayout(new GridLayout(0, 1));
        labelPanel.setBackground(backgroundColor);
        for (int i = 0; i < 17; i++) {
            if (i == 0) {
                JLabel instrumentLabel = new JLabel(instrumentList[i]);
                setlabel(instrumentLabel, 1, 16);
                labelPanel.add(instrumentLabel);
            } else {
                JLabel instrumentLabel = new JLabel(instrumentList[i]);
                setlabel(instrumentLabel, 0, 14);
                labelPanel.add(instrumentLabel);
            }
        }

    }

    void setlabel(JLabel label, int fontStyle, int fontSize) {
        label.setFont(new Font("Dialog", fontStyle, fontSize));
        label.setForeground(Color.white);
    }

    void buildCheckBoxPanel() {
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBackground(backgroundColor);
        checkBoxPanel.setLayout(new GridLayout(0, 16));
        checkBoxList = new ArrayList<JCheckBox>();
        for (int i = 0; i < 256; i++) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(backgroundColor);
            checkBox.setSelected(false);
            checkBoxPanel.add(checkBox);
            checkBoxList.add(checkBox);
            System.out.println();

        }
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(Box.createRigidArea(getUpperCheckPanelSize()));
        centerPanel.add(checkBoxPanel);
        centerPanel.setBackground(backgroundColor);
    }

    class startButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (!beatIsPlaying) {
                scanUserInput();
                if (areChecked) {
                    startButton.setBackground(Color.RED);
                    bbm.startPlaying();
                    hasStarted = true; // If already running prevents start button from being pressed again

                }
            }
        }
    }

    class stopButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (hasStarted) {
                bbm.stopPlaying();
                beatIsPlaying = false;
                startButton.setBackground(new JButton().getBackground());
            }
        }
    }

    class tempoUpButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (hasStarted) {
                bbm.setTempoUp();
            }
        }
    }

    class tempoDownButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (hasStarted) {
                bbm.setTempoDown();
            }
        }
    }

    class saveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (hasStarted && areChecked) {
                String userName = JOptionPane.showInputDialog("Please state your Pc's user name");
                String fileName = setFileName();
                File file = new File("C:\\Users\\" + userName + "\\Documents\\BeatBox Save\\" + fileName);
                file.getParentFile().mkdirs();
                try {
                    FileOutputStream fileStream = new FileOutputStream(file);
                    ObjectOutputStream sequenceStream = new ObjectOutputStream(fileStream);
                    sequenceStream.writeObject(checkBoxList);
                    JOptionPane.showMessageDialog(null, "Your beat file is saved at " + file.getAbsolutePath());
                    sequenceStream.close();

                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Oops something went wrong! Try a different name.\nThe name must follow your OS's restrictions  ");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please restart the program and try again.");
                }
            }
        }
    }

    private String setFileName() {
        name = JOptionPane.showInputDialog("Please type the name of the file");
        if (name.equals("")) {
            name = "unnamed beat";
            return name + ".ser";
        }
        return name + ".ser";
    }

    class loadButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            loadFrame = new JFrame();
            JFileChooser directory =  new JFileChooser();
            directory.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = directory.showOpenDialog(loadFrame);
            if (result == JFileChooser.APPROVE_OPTION){
                saveFile = directory.getSelectedFile();
            }
            try {
                FileInputStream fileStream = new FileInputStream(saveFile);             //qcg1.getFileName() + ".ser"
                ObjectInputStream cardStream = new ObjectInputStream(fileStream);
                Object array = cardStream.readObject();
                savedList = (ArrayList<JCheckBox>) array;
                cardStream.close();
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please make sure that the file is in \n the right directory");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please restart the program and try again.");
            }
            showSelected(savedList);
        }
    }

    void showSelected (ArrayList<JCheckBox> savedList){

        int index = 0;
        for (JCheckBox cell:savedList){
            if (cell.isSelected()){
               index = savedList.indexOf(cell);
               checkBoxList.get(index).setSelected(true);

            }
        }
    }


    public void scanUserInput() {
        try {
            bbm = new BeatBoxMidi();
        } catch (InvalidMidiDataException | MidiUnavailableException ex) {
            JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please restart the program.");
        }
        for (int j = 0; j < 256; j += 16) {
            for (int i = j; i < j + 16; i++) {
                if (checkBoxList.get(i).isSelected()) {
                    bbm.addCheckBoxToTrack(9, j, 100, i);
                    areChecked = true;
                    beatIsPlaying = true;

                }
            }
        }

    }
}
