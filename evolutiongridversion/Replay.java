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
import static evolutiongridversion.EvolutionGridVersion.BACKGROUND;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import static evolutiongridversion.EvolutionGridVersion.SIZE;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
public class Replay extends JFrame implements ActionListener{
    
    private JPanel animalPanel;
    private JPanel[] replayTiles = new JPanel[SIZE*SIZE];
    private int generation = -1;
    private JLabel genNumber;
    private Timer replayTimer;
    private ObjectInputStream input;
    private int previousReplayLength = 0;
    
    public static final int TIMER_BREAK = 1000;
    
    
    public Replay(){
        super("Evolution Replay");
        setSize(EvolutionGridVersion.WIDTH, EvolutionGridVersion.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(EvolutionGridVersion.BACKGROUND);
        
        animalPanel = new JPanel();
        animalPanel.setBackground(EvolutionGridVersion.BACKGROUND);
        animalPanel.setLayout(new GridLayout(EvolutionGridVersion.SIZE, 
                                            EvolutionGridVersion.SIZE));
        for (int i=0; i < EvolutionGridVersion.SIZE*EvolutionGridVersion.SIZE; i++){
            replayTiles[i] = new JPanel();
            replayTiles[i].setBackground(EvolutionGridVersion.BACKGROUND);
            animalPanel.add(replayTiles[i]);
        }
        add(animalPanel, BorderLayout.CENTER);
        
        //add button panel with background color
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(EvolutionGridVersion.BACKGROUND);
        buttonPanel.setLayout(new FlowLayout());
        
        JButton start = new JButton("start");
        start.setActionCommand("run replay");
        start.addActionListener(this);
        buttonPanel.add(start);
        
        JButton pause = new JButton("pause");
        pause.setActionCommand("pause replay");
        pause.addActionListener(this);
        buttonPanel.add(pause);
        
        JButton exit = new JButton("exit");
        exit.addActionListener(this);
        buttonPanel.add(exit);
        
        JPanel generations = new JPanel();
        generations.setLayout(new BorderLayout());
        JLabel generationLabel = new JLabel("GENERATION:");
        generations.add(generationLabel, BorderLayout.NORTH);
        genNumber = new JLabel(Integer.toString(generation));
        genNumber.setHorizontalAlignment(JLabel.CENTER);
        generations.add(genNumber, BorderLayout.CENTER);
        generations.setBackground(EvolutionGridVersion.GENERATION);
        buttonPanel.add(generations);
       
        //attach button panel
        add(buttonPanel, BorderLayout.SOUTH);

    }
    public void actionPerformed(ActionEvent e){
        //when step, evaluate method, when run, evaluate method with timer
        String button = e.getActionCommand();
        switch (button){
            //start replay starts replay timer
            case "run replay":if (replayTimer != null) replayTimer.cancel();
                              replayTimer = new Timer();
                              replayTimer.schedule(new TimerTask(){
                                  public void run(){
                                      replay();
                                  }
                              }, 0, TIMER_BREAK);  
                               break;
            //pause replay cancels replay timer
            case "pause replay": if (replayTimer != null) replayTimer.cancel();
                                 break;
            //exit closes JFrame
            case "exit": dispose();
        }
    }
    public void replay(){
        //runs through and displays each generation
        if (input == null){
            try{
                input = new ObjectInputStream(new FileInputStream("evolution.bin"));
            } catch (FileNotFoundException e){
                System.out.println("Input file not found.");
            } catch (IOException e){
                System.out.println("Error in retrieving input file.");
            }
        }
        try{
                int[] next = (int[])input.readObject();
                generation++;
                genNumber.setText(Integer.toString(generation));
                if(next.length< previousReplayLength){
                    for (int i = 0; i < previousReplayLength; i++){
                        if(i<next.length){
                            int b = next[i]%256;
                            int g = (next[i]/256)%256;
                            int r = next[i]/256/256;
                            replayTiles[i].setBackground(new Color(r,g,b));
                        } else {
                            replayTiles[i].setBackground(BACKGROUND);
                        }
                    }
                } else {
                    for (int i = 0; i < next.length; i++){
                            int b = next[i]%256;
                            int g = (next[i]/256)%256;
                            int r = next[i]/256/256;
                            replayTiles[i].setBackground(new Color(r,g,b));
                    }
                }
                System.out.println(next[1]);
                previousReplayLength = next.length;
                
        } catch (EOFException e){
            System.out.println("End of replay.");
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.out.println("Error in reading file.");
        } catch (ClassNotFoundException e){
            System.out.println("Array class not found error.");
        }
    }
}
