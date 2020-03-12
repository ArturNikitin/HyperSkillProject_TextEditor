package editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    public TextEditor() {
        super("Text editor");

//        Adding the ScrollArea
        JTextArea area = new JTextArea();
        JScrollPane scroll = new JScrollPane(area);
        area.setName("TextArea");
        add(scroll, BorderLayout.CENTER);
        scroll.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
        scroll.setName("ScrollPane");

//      Panel that has one text filed and two buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

//      TextField
        JTextField fileName = new JTextField();
        fileName.setName("SearchField");
        fileName.setColumns(10);
//        File Chooser
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setName("FileChooser");
        add(jfc);

//      Save button
        JButton saveButton = new JButton(new ImageIcon("E:\\icons\\Save-as-icon.png"));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> {
            new SwingWorker<String, Object>() {
                @Override
                protected String doInBackground() throws Exception {
                    int returnValue = jfc.showSaveDialog(null);

                    File selectedFile = null;
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        selectedFile = jfc.getSelectedFile();

                    }
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                        writer.write(area.getText());
                    } catch (Exception exp) {
                        System.out.println(exp.getMessage() + " Problem with save");

                    }
                    return null;
                }
            }.execute();
        });

//        Load Button

        JButton loadButton = new JButton(new ImageIcon("E:\\icons\\Files-New-File-icon.png"));
        loadButton.setName("OpenButton");

        loadButton.addActionListener(actionEvent -> {
            area.setText("");


                int returnValue = jfc.showOpenDialog(null);

                File selectedFile = null;
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = jfc.getSelectedFile();
                    CurrentFile file = CurrentFile.getCurrentFile();
                    CurrentFile.setFileName(jfc.getSelectedFile().getAbsolutePath());
                }
                File finalSelectedFile = selectedFile;
                new SwingWorker<String, Object>() {

                    @Override
                    protected String doInBackground() throws Exception {
                        byte[] bytes = Files.readAllBytes(Paths.get(finalSelectedFile.toURI()));
                        return new String(bytes);
                    }

                    @Override
                    protected void done() {
                        try {
                            area.append(get());
                        } catch (Exception e){

                        }
                    }
                }.execute();
        });

        //regexp text box
        JCheckBox checkBox = new JCheckBox("Use regex");
        checkBox.setName("UseRegExCheckbox");

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {

               CurrentCheck.getCurrentCheck().setIsChecked(itemEvent.getStateChange() == ItemEvent.SELECTED
                               ? 1 : 2);

            }
        });

        Number number = new Number();

        //button Next Match
        JButton nextSearchButton = new JButton(new ImageIcon("E:\\icons\\Actions-go-next-icon.png"));
        nextSearchButton.setName("NextMatchButton");

        nextSearchButton.addActionListener(actionEvent1 -> {
                    new SwingWorker<String, Object>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            try {
                                int num = number.getNext();
                                int start1 = number.getNumbers().get(num);
                                int i1 = CurrentCheck.getCurrentCheck().isChecked() ? number.getEnds().get(num) : (start1 + fileName.getText().length());
                                System.out.println("num: " + num);
                                area.setCaretPosition(i1);
                                area.select(start1, i1);
                                area.grabFocus();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;

                        }
                    }.execute();
                });
        //button Previous Match
        JButton previousSearchButton = new JButton(new ImageIcon("E:\\icons\\Actions-go-previous-icon.png"));
        previousSearchButton.setName("PreviousMatchButton");

        previousSearchButton.addActionListener(actionEvent1 -> {
            new SwingWorker<String, Object>() {
                @Override
                protected String doInBackground() throws Exception {
                    try {
                        int num = number.getPrevious();
                        int start1 = number.getNumbers().get(num);
                        int end = CurrentCheck.getCurrentCheck().isChecked() ? number.getEnds().get(num) : (start1 + fileName.getText().length());
                        System.out.println("num: " + num);
                        area.setCaretPosition(end);
                        area.select(start1, end);
                        area.grabFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        });


        //button Start Search
        JButton searchButton = new JButton(new ImageIcon("E:\\icons\\Zoom-icon.png"));
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(actionEvent -> {
            new SwingWorker<List<Integer>, Object>() {

                @Override
                protected void done() {
                    try {
                        number.setNumbers(get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected List<Integer> doInBackground() throws Exception {
                    String text = area.getText();
                    List<Integer> startNumbers = new ArrayList<>();
                    List<Integer> endNumbers = new ArrayList<>();

                    if (!CurrentCheck.getCurrentCheck().isChecked()) {

                        startNumbers.clear();
                        String matchingString = fileName.getText();
                        System.out.println(matchingString);
                        int start = 0;

                        while (text.indexOf(matchingString, start) >= 0) {
                            startNumbers.add(text.indexOf(matchingString, start));
                            start = text.indexOf(matchingString, start) + matchingString.length();
                        }

//                        Number number = new Number(startNumbers.size() - 1);
                        number.setMax(startNumbers.size()-1);
                        number.setI(0);


                        if (!startNumbers.isEmpty()) {
                            int i = text.indexOf(matchingString) + matchingString.length();
                            area.setCaretPosition(i);
                            area.select(text.indexOf(matchingString), i);
                            area.grabFocus();
                        }

                            /*nextSearchButton.addActionListener(actionEvent1 -> {
                                new SwingWorker<String, Object>(){
                                    @Override
                                    protected String doInBackground() throws Exception {
                                        if (!CurrentCheck.getCurrentCheck().isChecked()) {
                                            try {
                                                int start1 = startNumbers.get(number.getNext());
                                                int i1 = start1 + matchingString.length();
                                                area.setCaretPosition(i1);
                                                area.select(start1, i1);
                                                area.grabFocus();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return null;
                                    }
                                }.execute();


                            });


                            previousSearchButton.addActionListener(actionEvent1 -> {
                                new SwingWorker<String, Object>() {
                                    @Override
                                    protected String doInBackground() throws Exception {
                                        if (!CurrentCheck.getCurrentCheck().isChecked()) {
                                            int start1 = startNumbers.get(number.getPrevious());
                                            int i1 = start1 + matchingString.length();
                                            area.setCaretPosition(i1);
                                            area.select(start1, i1);
                                            area.grabFocus();
                                        }
                                        return null;
                                    }
                                }.execute();
                            });*/

                        } else {

                            String string = fileName.getText();
                            Pattern pattern = Pattern.compile(string);
                            Matcher matcher = pattern.matcher(text);
                            startNumbers.clear();
                            endNumbers.clear();

                            while (matcher.find()) {
                                startNumbers.add(matcher.start());
                                endNumbers.add(matcher.end());
                            }
//
                            number.setMax(startNumbers.size()-1);
                            number.setI(0);

                            if (!startNumbers.isEmpty()) {
                                area.setCaretPosition(startNumbers.get(0) + endNumbers.get(0));
                                area.select(startNumbers.get(0),endNumbers.get(0));
                                area.grabFocus();
                            }

                            number.setEnds(endNumbers);
                            /*nextSearchButton.addActionListener(actionEvent1 -> {
                                new SwingWorker<String,Object>(){
                                    @Override
                                    protected String doInBackground() throws Exception {
                                        if (CurrentCheck.getCurrentCheck().isChecked()) {
                                            try {
                                                int start1 = matches.get(number2.getNext());
                                                int end1 = start1 + string.length();
                                                area.setCaretPosition(end1);
                                                System.out.println(start1 + " " + end1);
                                                area.select(start1, end1);
                                                area.grabFocus();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return null;
                                    }
                                }.execute();
                            });
                            previousSearchButton.addActionListener(actionEvent1 -> {
                                new SwingWorker<String,Object>(){
                                    @Override
                                    protected String doInBackground() throws Exception {
                                        if (CurrentCheck.getCurrentCheck().isChecked()) {

                                            int start1 = matches.get(number2.getPrevious());
                                            int i1 = start1 + string.length();
                                            area.setCaretPosition(i1);
                                            area.select(start1, i1);
                                            area.grabFocus();
                                        }
                                        return null;
                                    }
                                }.execute();
                            });*/

                        }
                    return startNumbers;
                }
            }.execute();
        });


        panel.add(loadButton);
        panel.add(saveButton);
        panel.add(fileName);
        panel.add(searchButton);
        panel.add(nextSearchButton);
        panel.add(previousSearchButton);
        panel.add(checkBox);
        panel.setLayout(new FlowLayout());

        add(panel, BorderLayout.NORTH);


//        Menu bars
        JMenuBar mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        JMenu fileMenu = new JMenu("File");
        mainMenu.add(fileMenu);
        fileMenu.setName("MenuFile");

        JMenuItem load = new JMenuItem("Load");
        load.setName("MenuOpen");
        JMenuItem save = new JMenuItem("Save");
        save.setName("MenuSave");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setName("MenuExit");

        fileMenu.add(load);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(exit);



//        load
        load.addActionListener(actionEvent -> {
            area.setText("");


            int returnValue = jfc.showOpenDialog(null);

            File selectedFile = null;
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = jfc.getSelectedFile();
                CurrentFile file = CurrentFile.getCurrentFile();
                CurrentFile.setFileName(jfc.getSelectedFile().getAbsolutePath());
            }
            File finalSelectedFile = selectedFile;
            new SwingWorker<String, Object>() {

                @Override
                protected String doInBackground() throws Exception {
                    byte[] bytes = Files.readAllBytes(Paths.get(finalSelectedFile.toURI()));
                    return new String(bytes);
                }

                @Override
                protected void done() {
                    try {
                        area.append(get());
                    } catch (Exception e){

                    }
                }
            }.execute();
        });
//        save
        save.addActionListener(actionEvent -> {
            new SwingWorker<String, Object>() {
                @Override
                protected String doInBackground() throws Exception {int returnValue = jfc.showSaveDialog(null);

                    File selectedFile = null;
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        selectedFile = jfc.getSelectedFile();

                    }
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                        writer.write(area.getText());
                    } catch (Exception exp) {
                        System.out.println(exp.getMessage() + " Problem with save");

                    }
                    return null;
                }
            }.execute();
        });
//        exit
        exit.addActionListener(actionEvent -> {
            dispose();
        });

        JMenu searchMenu = new JMenu("Search");
        mainMenu.add(searchMenu );
        searchMenu.setName("MenuSearch");

        JMenuItem menuStartSearch = new JMenuItem("Start search");
        menuStartSearch.setName("MenuStartSearch");
        JMenuItem menuPreviousMatch = new JMenuItem("Previous match");
        menuPreviousMatch.setName("MenuPreviousMatch");
        JMenuItem menuNextMatch = new JMenuItem("Next match");
        menuNextMatch.setName("MenuNextMatch");
        JMenuItem menuUseRegExp = new JMenuItem("Use reg exp");
        menuUseRegExp.setName("MenuUseRegExp");


        menuNextMatch.addActionListener(actionEvent1 -> {
            new SwingWorker<String, Object>() {
                @Override
                protected String doInBackground() throws Exception {
                    try {
                        int num = number.getNext();
                        int start1 = number.getNumbers().get(num);
                        int i1 = CurrentCheck.getCurrentCheck().isChecked() ? number.getEnds().get(num) : start1 + fileName.getText().length();
                        area.setCaretPosition(i1);
                        area.select(start1, i1);
                        area.grabFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;

                }
            }.execute();
        });

        menuPreviousMatch.addActionListener(actionEvent1 -> {
            new SwingWorker<String, Object>() {
                @Override
                protected String doInBackground() throws Exception {
                    try {
                        int num = number.getPrevious();
                        int start1 = number.getNumbers().get(num);
                        int i1 = CurrentCheck.getCurrentCheck().isChecked() ? number.getEnds().get(num) : start1 + fileName.getText().length();
                        area.setCaretPosition(i1);
                        area.select(start1, i1);
                        area.grabFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        });


//        Start Search
        menuStartSearch.addActionListener(actionEvent -> {
            new SwingWorker<List<Integer>, Object>() {

                @Override
                protected void done() {
                    try {
                        number.setNumbers(get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected List<Integer> doInBackground() throws Exception {
                    String text = area.getText();
                    List<Integer> startNumbers = new ArrayList<>();
                    List<Integer> endNumbers = new ArrayList<>();

                    if (!CurrentCheck.getCurrentCheck().isChecked()) {

                        startNumbers.clear();
                        String matchingString = fileName.getText();
                        System.out.println(matchingString);
                        int start = 0;

                        while (text.indexOf(matchingString, start) >= 0) {
                            startNumbers.add(text.indexOf(matchingString, start));
                            start = text.indexOf(matchingString, start) + matchingString.length();
                        }

//                        Number number = new Number(startNumbers.size() - 1);
                        number.setMax(startNumbers.size()-1);
                        number.setI(0);


                        if (!startNumbers.isEmpty()) {
                            int i = text.indexOf(matchingString) + matchingString.length();
                            area.setCaretPosition(i);
                            area.select(text.indexOf(matchingString), i);
                            area.grabFocus();
                        }



                    } else {

                        String string = fileName.getText();
                        Pattern pattern = Pattern.compile(string);
                        Matcher matcher = pattern.matcher(text);
                        startNumbers.clear();
                        endNumbers.clear();

                        while (matcher.find()) {
                            startNumbers.add(matcher.start());
                            endNumbers.add(matcher.end());
                        }
//
                        number.setMax(startNumbers.size()-1);
                        number.setI(0);

                        if (!startNumbers.isEmpty()) {
                            area.setCaretPosition(startNumbers.get(0) + endNumbers.get(0));
                            area.select(startNumbers.get(0),endNumbers.get(0));
                            area.grabFocus();
                        }

                        number.setEnds(endNumbers);


                    }
                    return startNumbers;
                }
            }.execute();
        });



        menuUseRegExp.addActionListener(actionEvent -> {
            checkBox.doClick();
        });



        searchMenu.add(menuStartSearch);
        searchMenu.add(menuPreviousMatch);
        searchMenu.add(menuNextMatch);
        searchMenu.add(menuUseRegExp);


        setVisible(true);
    }
}
