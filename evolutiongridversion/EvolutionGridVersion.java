/*
 * Aditi Talati - started 4 April 2018
 * Evolution project - create a GUI with colored squares whose color is
 *                     naturally selected to test for patterns over time
 * Version 2.0 - used a grid to store the colored panels since I can't figure
 *               out how to delete panels outside the constructor
 */

//MOVE REPLAY TO NEW CLASS
package evolutiongridversion;

/**
 *
 * @author Aditi
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.util.Random;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.EOFException;
import javax.swing.SwingWorker;

public class EvolutionGridVersion extends JFrame implements ActionListener{
    public static final int SIZE = 100;
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final Color BACKGROUND = Color.BLACK;
    //probability of death constant = 1/DEATH
    public static final int DEATH = 22;
    //reproductive randomness constant
    public static final int BIRTH_RANDOMNESS = 5;
    //probability of birth failure constant = 1/FAILURE
    public final int BIRTH_FAILURE = 90;
    public static final int TIMER_BREAK = 2000;
    public static final Color GENERATION = new Color(232,241,242);
    private static int RED_GOAL = 129;
    private static int GREEN_GOAL = 67;
    private static int BLUE_GOAL = 183;
    //probability of mutation constant = 1/MUTATION
    public static final int MUTATION= 256;
    public static final int CHANGE_WHEN_MUTATED = 12;
    //probability of epidemic constant = 1/EPIDEMIC
    public static final int EPIDEMIC = 100;
    public static final int ADAPT = 100;
    public static final int ADAPT_CHANGE = 20;
    public static final int BIRTH_CHANCE = 60;
    public static final int BIRTH_AGE = 2;
    public static final int REPLAY_TIMER_BREAK = 1000;
    
    private boolean keepReplay = false;
    private int previousReplayLength = 0;
    private Timer replayTimer;
    private ObjectInputStream input = null;
    private JPanel[] replayTiles = new JPanel[SIZE*SIZE];
    private JPanel goalColor;
    private boolean done = true;
    private JLabel genNumber;
    private JLabel genReplay;
    private int generationsReplay = -1;
    private Timer timer;
    private ArrayList<JPanel> unused = new ArrayList<>(SIZE*SIZE/2);
    private int generation = 0;
    private ObjectOutputStream output = null;
    private Random rand = new Random();
    private JPanel animalPanel;
    private ArrayList<Animal> organisms = new ArrayList<>(SIZE*SIZE/2);
    
    public final int BIRTH_CONSTANT = rand.nextInt(128);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EvolutionGridVersion gui = new EvolutionGridVersion();
        gui.setVisible(true);
    }
    public EvolutionGridVersion(){
        //creates frame
        super("Evolution Simulator");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowClose());
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        
        try{
            output = new ObjectOutputStream(new FileOutputStream("evolution.bin"));
        } catch (IOException e){
            System.out.println("Output file not found.");
            System.exit(0);
        }
        try{
            input = new ObjectInputStream(new FileInputStream("evolution.bin"));
        } catch (FileNotFoundException e){
            System.out.println("Input file not found.");
        } catch (IOException e){
            System.out.println("Error in retrieving input file.");
        }
        
        //create main grid
        animalPanel = new JPanel();
        animalPanel.setBackground(BACKGROUND);
        animalPanel.setLayout(new GridLayout(SIZE, SIZE));
        for (int i=0; i < SIZE; i++){
            for (int j=0; j < SIZE; j++){
                //creates animals
                if (i <= SIZE/2){
                    JPanel animal = new JPanel();
                    organisms.add(new Animal(rand.nextInt(256),rand.nextInt(256),
                                             rand.nextInt(256),rand.nextInt(256),
                                             rand.nextInt(256),rand.nextInt(256),
                                             animal));
                    animalPanel.add(animal);
                } else {
                    JPanel empty = new JPanel();
                    empty.setBackground(BACKGROUND);
                    unused.add(empty);
                    animalPanel.add(empty);
                }
            }
        }
        add(animalPanel, BorderLayout.CENTER);
        //add button panel with background color
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.setLayout(new FlowLayout());
        //add step, run, pause buttons with actionlistener this 
        
        goalColor = new JPanel();
        JLabel goalLabel = new JLabel("current\ngoal");
        goalColor.add(goalLabel);
        goalColor.setBackground(new Color(RED_GOAL, GREEN_GOAL, BLUE_GOAL));
        buttonPanel.add(goalColor);
        
        JButton step = new JButton("step");
        step.addActionListener(this);
        buttonPanel.add(step);
        
        JButton run = new JButton("run");
        run.addActionListener(this);
        buttonPanel.add(run);
        
        /*JButton pause = new JButton("pause");
        pause.addActionListener(this);
        buttonPanel.add(pause);*/
        
        JButton replay = new JButton("replay");
        replay.addActionListener(this);
        buttonPanel.add(replay);
        
        JPanel generations = new JPanel();
        generations.setLayout(new BorderLayout());
        JLabel generationLabel = new JLabel("GENERATION:");
        generations.add(generationLabel, BorderLayout.NORTH);
        genNumber = new JLabel(Integer.toString(generation));
        genNumber.setHorizontalAlignment(JLabel.CENTER);
        generations.add(genNumber, BorderLayout.CENTER);
        generations.setBackground(GENERATION);
        buttonPanel.add(generations);
       
        //attach button panel
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    
    //actionlistener for the buttons
    public void actionPerformed(ActionEvent e){
        //when step, evaluate method, when run, evaluate method with timer
        String button = e.getActionCommand();
        switch (button){
            case "step" ://System.out.println("\"step\" clicked.");
                          generation();
                          break;
            case "run" :  if (timer != null) timer.cancel();
                          timer = new Timer();
                          timer.schedule(new TimerTask() {
                              public void run() {
                                  if (done) generation();
                                  else System.out.println("not ready");
                              }
                          }, 0, TIMER_BREAK);
                          ((JButton)e.getSource()).setText("pause");
                          ((JButton)e.getSource()).setActionCommand("pause");
                          break;
            case "pause": if (timer != null) timer.cancel();
                          ((JButton)e.getSource()).setText("run");
                          ((JButton)e.getSource()).setActionCommand("run");
                          break;
            case "replay": //pauses and then generates new JFrame to replay
                           if (timer != null) timer.cancel();
                           Replay replayFrame = new Replay();
                           replayFrame.setVisible(true);
                           break;
                           //sets generations replay to 0
        }
    }
    
    
    public void generation(){
        done = false;
    //generation method prints to document and adds 1 to age
        try{
            int[] organismArray = new int[organisms.size()];
            for(int i = 0; i < organisms.size(); i++){
                Animal a = organisms.get(i);
                organismArray[i]=(a.r*256*256+a.g*256+a.b);
                a.age++;
            }
            output.writeObject(organismArray);
            output.flush();
        }catch (IOException e){
            System.out.println("exception in recording data");
        }
        generation++;
        //System.out.println(generation + " done: " + done);
        genNumber.setText(new Integer(generation).toString());
        
        //ADAPT - changes survival conditions
        if (generation%ADAPT == 0){
            int changedColor = rand.nextInt(3);
            int goalChange = rand.nextInt(ADAPT_CHANGE);
            switch(changedColor){
                case 0: RED_GOAL = (RED_GOAL + goalChange)%256; break;
                case 1: BLUE_GOAL = (BLUE_GOAL + goalChange)%256; break;
                case 2: GREEN_GOAL = (GREEN_GOAL + goalChange)%256; break;
            }
            goalColor.setBackground(new Color(RED_GOAL, GREEN_GOAL, BLUE_GOAL));
        }
        //then selects animals to die (.remove() should work) 
        if (unused.size() == 0 || rand.nextInt()%EPIDEMIC == 0){
            //EPIDEMIC
            int index = rand.nextInt(organisms.size());
            Animal a = organisms.get(index);
            for (int i = 0; i<organisms.size();i++){
                Animal b = organisms.get(i);
                if(Math.abs(b.b - a.b) < 8){
                    organisms.remove(i);
                    i--;
                    b.panel.setBackground(BACKGROUND);
                    unused.add(b.panel);
                }
            }
        }
        for (int i = 0; i<organisms.size(); i++){
            if(!survival(organisms.get(i))){
                //remove panel not working
                //animalPanel.remove(organisms.get(i).panel);
                Animal a = organisms.remove(i);
                a.panel.setBackground(BACKGROUND);
                unused.add(a.panel);
                i--;
            }
        }
        //add(animalPanel, BorderLayout.CENTER);
    //and selects animals to reproduce (adds to separate arraylist until after)
        ArrayList<Animal> babies = new ArrayList<Animal>(organisms.size()/2);
        for (int i=0; i<organisms.size()-1; i++){
            //criteria for reproduction
            if(((organisms.get(i).blue1 + organisms.get(i).red2)%256)
                    *organisms.get(i).age > 256 || 
                    (organisms.get(i).age > BIRTH_AGE
                    && rand.nextInt(BIRTH_CHANCE) == 0)){
                
                //find best mate
                Animal mate = organisms.get(i+1);
                int mateValue = 50;
                for(int j = i +2; j<Math.min(i+SIZE,organisms.size()); j++){
                     if (Math.abs(organisms.get(j).g - organisms.get(i).g)
                             < mateValue 
                             && Math.abs(organisms.get(j).g 
                                     - organisms.get(i).g) >= 10){
                         mate = organisms.get(j);
                         mateValue = 
                                 Math.abs(organisms.get(j).g - organisms.get(i).g);
                         //add some element of randomness to selection
                     } else if (rand.nextInt()%BIRTH_RANDOMNESS==0){
                         mate = organisms.get(j);
                         break;
                     }
                }
                
                //reproduce
                boolean birth = true;
                for (int j = 0; j < Math.sqrt(Math.abs((mate.green1)%256-BIRTH_CONSTANT)); j++){
                        if(rand.nextInt()%BIRTH_FAILURE == 0) birth = false;
                }
                if (birth)
                    babies.add(reproduce(organisms.get(i),mate));
            }
        }
        for(Animal a: babies){
            if (a!=null) organisms.add(a);
        }
        done = true;
        //System.out.println(generation +" done: " + done);
    }
    //decide whether an animal dies
    public boolean survival(Animal a){
        boolean red = true;
        boolean blue = true;
        boolean green = true;
        for(int i = 0; i<a.age*Math.log(Math.abs(RED_GOAL-a.r)); i++){
            if(rand.nextInt()%DEATH == 0){
                red = false;
                break;
            }
        }
        for(int i = 0; i<a.age*
                       Math.log(Math.abs((a.green1+a.green2)%256-GREEN_GOAL)); i++){
            if(rand.nextInt()%DEATH == 0){
                green = false;
                break;
            }
        }
        for(int i = 0; i<a.age*
                        Math.log(Math.abs((a.blue1+a.blue2)%256-BLUE_GOAL)); i++){
            if(rand.nextInt()%DEATH == 0){
                blue = false;
                break;
            }
        }
        boolean life = (red && green) || (red && blue) || (green&&blue);
        //if (!life) System.out.println("dies");
        return life;
    }
    
    //"reproduce" method takes two organisms and produces an offspring
    public Animal reproduce(Animal a, Animal b){
        if (unused.size()>0)
        return new Animal(select(a.red1, a.red2), select(b.red1, b.red2),
                         select(a.green1, a.green2), select(b.green1, b.green2),
                         select(a.blue1, a.blue2), select(b.blue1, b.blue2),
                         unused.remove(0));
        else return null;
        //does not add the animal to the array
    }
    public int select(int a, int b){
        if (rand.nextInt()%MUTATION == 0){
            a+=CHANGE_WHEN_MUTATED;
            b+=CHANGE_WHEN_MUTATED;
        }
        if (rand.nextInt()%2 == 0) return a;
        else return b;
    }
    
    //animal inner class has two variables for each color var and age
    class Animal{
        private int red1;
        private int red2;
        private int green1;
        private int green2;
        private int blue1;
        private int blue2;
        private JPanel panel;
        private int r;
        private int g;
        private int b;
        int age = 0;
        public Animal(int r1, int r2, int g1, int g2, int b1, int b2, JPanel p){
            red1 = r1;
            red2 = r2;
            r = (red1+red2)%256;
            green1 = g1;
            green2 = g2;
            g = (green1 + green2)%256;
            blue1 = b1;
            blue2 = b2;
            b = (blue1+blue2)%256;
            panel = p;
            Color c = new Color((red1+red2)%256, (green1+green2)%256,
                                                  (blue1+blue2)%256);
            panel.setBackground(c);
        }
        public boolean equals(Object o){
            if (o instanceof Animal){
                Animal a = (Animal)o;
                return (a.panel == panel);
            } else return false;
        }
    }
}
