package org.icepdf.ri.common;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.sql.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class DictionaryPane extends JFrame implements ActionListener {
    
    private static int windowCount = 0;
    private JButton btnClosePane = new JButton("Close");
    private JTextArea textArea = new JTextArea(5, 10);
    private JScrollPane scrollPane = new JScrollPane(textArea);

    public DictionaryPane() {
        
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    setShape(new RoundRectangle2D.Double(0, 0,getWidth(),getHeight(),15,15));
                }
            });

            setUndecorated(true);
            setSize(300,300);
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
            
            btnClosePane.addActionListener(this);
            add(scrollPane, BorderLayout.CENTER);

            
            JPanel bottomPanel = new JPanel(new Flowlayout());
            bottomPanel.add(btnClosePane);
            add(bottomPanel, BorderLayout.SOUTH);
    }

    // starting DictionaryPane windows on the event-dispatching thread
    public static void start(){
        if(++windowCount == 1){    
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                DictionaryPane dictionaryPane = new DictionaryPane();
                dictionaryPane.setVisible(true);
                dictionaryPane.setMeaningToTextBox();
              }
            });
        }
    }

    public void actionPerformed(ActionEvent event) {
        
        if (event.getSource() == btnClosePane)
        {        
            windowCount--;
            this.dispose();
        }else{
            
        }

    }


    public void setMeaningToTextBox(){
        try {
            textArea.append("Hi\n");
            long startTime = System.currentTimeMillis();
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:./wordnet","sa","");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wn_see_also");
            int counter=0;
            while(rs.next()){
                counter++;
                // System.out.println(rs.getString(1)+" "+rs.getString(2));
            }
            long stopTime = System.currentTimeMillis();
            textArea.append("\n\nCounter is "+counter);  
            textArea.append("\nExecution time is : "+(stopTime-startTime) +"ms");
            
            conn.close();
            
        } catch (Exception e) {
            textArea.append(e.getMessage());
            e.printStackTrace();
        }
    }

}
