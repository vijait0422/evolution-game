/*
 * Aditi Talati - started 4 April 2018
 * Evolution project - create a GUI with colored squares whose color is
 *                     naturally selected to test for patterns over time
 * Version 2.0 - used a grid to store the colored panels since I can't figure
 *               out how to delete panels outside the constructor
 */
package evolutiongridversion;

/**
 *
 * @author Aditi
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class WindowClose extends JFrame implements WindowListener, 
                                                                ActionListener{
    public static final int WIDTH = 200;
    public static final int HEIGHT = 100;
    public static final Color TEXT = new Color(231,230,245);
    
    public void windowClosing(WindowEvent e){
        this.setVisible(true);
    }
    public void windowOpened(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "yes": System.exit(0);
                        break;
            case "no": dispose();
                       break;
        }
    }
    public WindowClose(){
        super();
        setSize(WIDTH,HEIGHT);
        setBackground(EvolutionGridVersion.BACKGROUND);
        setLayout(new BorderLayout());
        
        JLabel confirmLabel = new JLabel("Are you sure you want to exit?");
        confirmLabel.setBackground(EvolutionGridVersion.BACKGROUND);
        add(confirmLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(EvolutionGridVersion.BACKGROUND);
        buttonPanel.setLayout(new FlowLayout());
        
        JButton yes = new JButton("yes");
        yes.addActionListener(this);
        buttonPanel.add(yes);
        
        JButton no = new JButton("no");
        no.addActionListener(this);
        buttonPanel.add(no);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
