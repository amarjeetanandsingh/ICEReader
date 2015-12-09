package org.icepdf.ri.common;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.text.*;
import java.sql.*;
import java.util.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class DictionaryPane extends JFrame implements ActionListener {

    protected static int windowCount = 0;
    protected JButton btnClose = new JButton("Close");
    protected JButton btnSearch = new JButton("Search");
    protected JTextField searchWordTextField = new JTextField(10);
    protected JTextPane resultTextPane = new JTextPane();
    protected JScrollPane scrollPane = new JScrollPane(resultTextPane);

    final int DEFAULT_FONT_SIZE = 13;
    final int WINDOW_WIDTH = 300;
    final int WINDOW_HEIGHT = 300;
    
    public DictionaryPane() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0,getWidth(),getHeight(),15,15));
            }
        });

        setUndecorated(true);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // set the starting location of DictionaryPane relative to mouse_current_position.
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int x = (int) mouseLocation.getX();
        int y = (int) mouseLocation.getY();
        int locationX = 0, locationY = 0; // Dictionary Pane starting (x y).

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(x < 150){
            locationX = 0;
        }else if( x > screenSize.width -300){
            locationX = screenSize.width - 300;
        }else{
            locationX = x -150;
        }

        if(y < 325){
            locationY = 0;
        }else if(y > screenSize.height -325 ){
            locationY = screenSize.height -375;
        }else{
            locationY = y - 325;
        }
        setLocation(locationX, locationY);

        getContentPane().add(scrollPane);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(searchWordTextField);

        btnSearch.addActionListener(this);
        bottomPanel.add(btnSearch);

        btnClose.addActionListener(this);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // starting DictionaryPane windows on the event-dispatching thread
    public static void start(){
        if(++windowCount == 1){    
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    DictionaryPane dictionaryPane = new DictionaryPane();
                    dictionaryPane.setVisible(true);
                    dictionaryPane.setMeaningToJTextPane();
                }
            });
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnClose){        
            windowCount--;
            this.dispose();
        }else if(event.getSource() == btnSearch) {
            resultTextPane.setText("");
            setMeaningToJTextPane();
        }else{

        }
    }


    public void setMeaningToJTextPane(){
         setMeaningToJTextPane(searchWordTextField.getText());
    }

    public void setMeaningToJTextPane(String word){
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./wordnet","sa","");

            StyledDocument doc = (StyledDocument)resultTextPane.getDocument();
            Style style = doc.addStyle("h1", null);

            StringBuilder meaning = new StringBuilder(getGloss(conn,word));
            String [] lines = meaning.toString().split("\n");
            
            for (String line : lines) {
                if(line.indexOf("; \"") == -1){ // if ; doesn't exist in this meaning => no example.
                    doc.insertString(doc.getLength(),"\n"+line, setStyleMeaning(style));
                }else{
                    String lineBreak [] = line.split(";");
                    boolean isFirst = true;

                    for (String block : lineBreak) {
                        //display meaning before first ';'
                        if(isFirst){
                            doc.insertString(doc.getLength(), "\n"+block , setStyleMeaning(style));
                            isFirst = false;
                        }else{
                            // display example after ';'
                            doc.insertString(doc.getLength(), "\n"+block , setStyleExample(style));
                        }
                    }
                }
            }

            conn.close();
        } catch (Exception e) {
            resultTextPane.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    // create style to be added to document for the meanings.
    private Style setStyleMeaning(Style style) {
        StyleConstants.setFontSize(style, DEFAULT_FONT_SIZE);
        StyleConstants.setBold(style, Boolean.FALSE);
        StyleConstants.setItalic(style, Boolean.FALSE);
        StyleConstants.setLeftIndent(style, (int)(0.15*WINDOW_WIDTH));
        StyleConstants.setForeground(style, Color.BLACK);
        return style;
    }

    // create style to be added to document for the example after ; in line.
    private Style setStyleExample(Style style) {
        StyleConstants.setFontSize(style, DEFAULT_FONT_SIZE);
        StyleConstants.setBold(style, Boolean.FALSE);
        StyleConstants.setItalic(style, Boolean.TRUE);
        StyleConstants.setLeftIndent(style, (int)(0.15*WINDOW_WIDTH));
        StyleConstants.setForeground(style, Color.gray );
        return style;
    }

    // fetch gloss for the given word from database.
    public static String getGloss(Connection conn, String word) throws SQLException{

        StringBuilder result = new StringBuilder("");
        long startTime = System.currentTimeMillis();

        ArrayList<String> synset_id_list = new ArrayList<String>(); 
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("select synset_id from wn_synset where word='"+word+"'");
        while(rs.next())
            synset_id_list.add(rs.getString(1));

        for (String synset_id : synset_id_list) {
            rs = conn.createStatement().executeQuery(
                    "select gloss from wn_gloss where synset_id='"+synset_id+"'");
            while(rs.next())
                result.append(rs.getString(1)+"\n");
        }
        long stopTime = System.currentTimeMillis();
        return result.toString()+"\n Time Elapsed : "+(stopTime-startTime);
    }

}
