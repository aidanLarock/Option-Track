package a3;

/**
 * @author        Aidan Larock 
 * @studentNumber #6186076
 * @assignment    3
 * 
 * Main
 * The main class of project A3. This class sets up a resizing grid
 * GUI with controls to display HillClimb solution on the screen.
 * It then creates 4 HillClimb threads and tests and paints each 
 * threads HillClimb sequence if it is the current best using proper 
 * mutual exclusion.
 */

/* Imports */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;


/* class Main extrends JFrame */
/**
 * HillClimb climb
 * maxWide/Tall, minWide/Tall : maximum and minimum # of dots
 * cTall/Wide : current dot height and width
 * paths: total number of paths based on (dots-1)
 * start: start/stop (reset) threads
 * dotT/W: array of dot places
 * Atomic bestScore: current best score
 * Atomic newX/Y, oldX/Y: points to draw lines from old to new
 * Atomic linesT/W: lines to draw from old(linesW,linesT) to new(linesW,linesT)
 * 
 * also includes Panels and Buttons
 */
public class Main extends JFrame {  
    HillClimb climb;
    
    int maxWide = 16, maxTall = 10, minWide = 2, minTall = 2;
    int cTall, cWide;
    int paths;
    boolean start;
    int[] dotW = {40,70,100,130,160,190,220,250,280,310,340,370,400,430,460,490},
    dotT = {20,60,100,140,180,220,260,300,340,380};
    
    AtomicInteger bestScore,newX,newY,oldX,oldY;
    AtomicIntegerArray linesT, linesW;
    
    /* Panels and Buttons */
    MyPanel panel;
    JPanel btnPanel;
    JPanel graph;
    JPanel sidePanel;
    JButton leftBtn;
    JButton rightBtn;
    JButton startBtn;
    JButton stopBtn;
    JButton upBtn;
    JButton downBtn;
    
    /* Object lock */
    Object lock=new Object();
    
    /**
     *  Main
     *  sets JFrame name, bestScore, newX/y, oldX/Y
     *  sets cWide/cTall, start to false
     * 
     *  calls method: gui to set up GUI
     *  calls method: dots to draw dots
     *  calls method: climb if start is true
     */
    public Main() {
        super("Traveling Salesman Problem");
        bestScore = new AtomicInteger(0);
        newX = new AtomicInteger(0);
        newY = new AtomicInteger(0);
        oldX = new AtomicInteger(0);
        oldY = new AtomicInteger(0);
        cWide = 7;
        cTall = 7;
        start = false;
        gui();
        dots();
        if(start == true){
            climb();
            bestScore.set(0);
        }
        if(start == false){
            bestScore.set(0); // reset best score
        }
    }
    
    /**
     *  gui: called by Main
     *  creates and displays the GUI on the screen
     *  along with allowing the user to set the number of dots
     *  and calling dots to draw the dots onto the screen
     */
    private void gui() {
    
        graph = new JPanel();
        sidePanel = new JPanel();
        btnPanel = new JPanel();

        upBtn = new JButton("▲");
        upBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    if(cTall>minTall){
                        cTall = cTall - 1;
                        startBtn.setEnabled(true);
                        stopBtn.setEnabled(false);
                        start=false;
                        dots();
                    }
                }
            }
        );
        downBtn = new JButton("▼");
        downBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    if(cTall<maxTall){
                        cTall = cTall + 1;
                        startBtn.setEnabled(true);
                        stopBtn.setEnabled(false);
                        start=false;
                        dots();
                    }
                }
            }
        );
        leftBtn = new JButton("◄");
        leftBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    if(cWide>minWide){
                        cWide = cWide - 1;
                        startBtn.setEnabled(true);
                        stopBtn.setEnabled(false);
                        start=false;
                        dots();
                    }
                }
            }
        );
        rightBtn = new JButton("►");
        rightBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    if(cWide<maxWide){
                        cWide = cWide + 1;
                        startBtn.setEnabled(true);
                        stopBtn.setEnabled(false);
                        start=false;
                        dots();
                    }
                }
            }
        );
        startBtn = new JButton("Start");
        startBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    start = true;
                    if(start == true){
                        stopBtn.setEnabled(true);
                        startBtn.setEnabled(false);
                        climb();
                    }
                }
            }
        );
        stopBtn = new JButton("Stop");
        stopBtn.addActionListener((actionEvent)-> {
                synchronized(lock) {
                    start = false;
                    if(start == false){
                        startBtn.setEnabled(true);
                        stopBtn.setEnabled(false);
                    }
                }
            }
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(820, 440));
        getContentPane().setLayout(new FlowLayout());

        graph.setBackground(Color.WHITE);
        graph.setPreferredSize(new Dimension(600, 400));

        GroupLayout graphLayout = new GroupLayout(graph);
        graph.setLayout(graphLayout);
        getContentPane().add(graph);

        sidePanel.setBackground(Color.LIGHT_GRAY);
        sidePanel.setPreferredSize(new Dimension(200, 400));
        sidePanel.setLayout(new GridBagLayout());

        btnPanel.setBackground(Color.LIGHT_GRAY);
        btnPanel.setPreferredSize(new Dimension(190, 300));

        /* Buttons */
        // Up
        upBtn.setPreferredSize(new Dimension(73, 85));
        upBtn.setCursor(new Cursor(HAND_CURSOR));
        upBtn.setBackground(Color.WHITE);
        upBtn.setFocusPainted(false);
        btnPanel.add(upBtn);

        //Down
        downBtn.setPreferredSize(new Dimension(73, 85));
        downBtn.setCursor(new Cursor(HAND_CURSOR));
        downBtn.setBackground(Color.WHITE);
        downBtn.setFocusPainted(false);
        btnPanel.add(downBtn);

        // Left
        leftBtn.setPreferredSize(new Dimension(73, 85));
        leftBtn.setCursor(new Cursor(HAND_CURSOR));
        leftBtn.setBackground(Color.WHITE);
        leftBtn.setFocusPainted(false);
        btnPanel.add(leftBtn);

        // Right
        rightBtn.setPreferredSize(new Dimension(73, 85));
        rightBtn.setCursor(new Cursor(HAND_CURSOR));
        rightBtn.setBackground(Color.WHITE);
        rightBtn.setFocusPainted(false);
        btnPanel.add(rightBtn);

        // Start
        startBtn.setPreferredSize(new Dimension(73, 85));
        startBtn.setCursor(new Cursor(HAND_CURSOR));
        startBtn.setBackground(Color.WHITE);
        startBtn.setFocusPainted(false);
        btnPanel.add(startBtn);

        // Stop
        stopBtn.setPreferredSize(new Dimension(73, 85));
        stopBtn.setCursor(new Cursor(HAND_CURSOR));
        stopBtn.setBackground(Color.WHITE);
        stopBtn.setFocusPainted(false);
        btnPanel.add(stopBtn);

        // Side Panel
        sidePanel.add(btnPanel, new GridBagConstraints());
        getContentPane().add(sidePanel);
        panel=new MyPanel(this);
        graph.add(panel);

        setResizable(false); 
        pack();
  } 
    
    /**
     *  dots: called by main, gui
     *  calls paint to draw dots 
     *  also resets the lines arrays to clear the panel
     */
    private void dots(){
        /* reseting lines */
        for(int i=0;i<paths;i++){
            linesT.set(i, panel.yOld);
            linesW.set(i, panel.xOld);
        }  
        panel.repaint(); // repaint
    }
    
    /**
     *  climb: called by main
     *  sets path to the total number of potential paths 
     *  sets the linesT and W to paths+1 (+1 as it needs to start at the corner)
     *  sets bestScore to 0
     *  creates 4 threads (t1,t2,t3,t4) all running HillClimb
     *  tests each thread synchronously
     */
    private void climb(){
        paths = (cTall * cWide) - 1;
        linesT = new AtomicIntegerArray(paths+1);
        linesW = new AtomicIntegerArray(paths+1);
        bestScore.set(0);
        
        /* Thread 1 */
        Thread t1 = new Thread(){
            @Override
            public void run(){
                HillClimb climb = new HillClimb(paths, cTall, cWide);
                while(start == true){
                    climb.begin(bestScore);
                    test(climb.thisScore, climb); 
                }
            }    
        };
        
        /* Thread 2 */
        Thread t2 = new Thread(){
            @Override
            public void run(){
                HillClimb climb = new HillClimb(paths, cTall, cWide);
                while(start == true){
                    climb.begin(bestScore);
                    test(climb.thisScore, climb); 
                }
            }    
        };
        
        /* Thread 3 */
        Thread t3 = new Thread(){
            @Override
            public void run(){
                HillClimb climb = new HillClimb(paths, cTall, cWide);
                while(start == true){
                    climb.begin(bestScore);
                    test(climb.thisScore, climb);  
                }
            }    
        };
        
        /* Thread 4 */
        Thread t4 = new Thread(){
            @Override
            public void run(){
                HillClimb climb = new HillClimb(paths, cTall, cWide);
                while(start == true){
                    climb.begin(bestScore);
                    test(climb.thisScore, climb);  
                }
            }    
        };
        
        /* Starting the threads if start = true */
        if(start == true){
            t1.start();
            t2.start();
            t3.start();
            t4.start();
         }
 }
    
    /**
     *  synchronized test: called by climb threads
     *  tests if the threads score is greater than the bestScore
     *  if it is:
     *      sets bestScore to score
     *      sets the points to daw the lines from using climb.sent
     *      calls to repaint
     *  if not:
     *      nothing happens
     *      thread, keeps working
     */
    private synchronized void test(int score,HillClimb climb){
        if(score>bestScore.get()){
            /* setting scores and restting drawing points */
            bestScore.getAndSet(score);
            newX.set(42);
            newY.set(23);
            oldX.set(42);
            oldY.set(23);
            linesW.set(0, 42);
            linesT.set(0, 23);
            for(int i=0;i<paths;i++){
                linesT.set(i+1, 0);
                linesW.set(i+1, 0);
            }
            
            /* setting new drawing points */            
            for(int i=0;i<paths;i++){
                int direction = climb.sent[i];
                switch(direction) {
                    case 1:
                        newY.set(oldY.get()+40);
                        break;
                    case -1:
                        newY.set(oldY.get()-40);
                        break;
                    case 2:
                        newX.set(oldX.get()+30);
                        break;
                    case -2:
                        newX.set(oldX.get()-30);
                        break;
                } 
                linesT.set(i+1, newY.get());
                linesW.set(i+1, newX.get());
                oldX.set(newX.get());
                oldY.set(newY.get());
            }
            /* repaint */
            panel.repaint();
        }
    }
    
    /**
     *  main method
     *  sets GUI visible 
     */
    public static void main(String[] args) {
        new Main().setVisible(true);
    } 
    
}



/**
 * @author        Aidan Larock 
 * @studentNumber #6186076
 * @assignment    3
 * 
 * MyPanel
 * this class draws the dots and lines onto a jPanel 
 * in the GUI
 */

/* class MyPanel extrends JPanel */
/**
 * Main pan
 * yOld, xOld, yNew, xNew : from Old to New points to draw line
 */
class MyPanel extends JPanel {
    Main pan;
    int yOld,xOld,yNew,xNew;
    MyPanel(Main pan) {     
        setPreferredSize(new Dimension(600,420));
        setSize(new Dimension(600,420));
        this.pan=pan;
    }
  
    /**
     * paintComponent: Called by test, dots, gui
     * paints the dots and lines specified by Main
     * Synchronized so other threads don't call paint 
     * when paintComponent is working on 1 solution.
     * 
     * pan.dot[] : Main specified dots to draw
     * pan.lines[] : Main specified lines to draw
     */
    @Override
    public synchronized void paintComponent(Graphics g) { 
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight() );
        int width = pan.cWide;
        int height = pan.cTall;
        // Clears and gets boundries 
        g.setColor(Color.BLACK);
        
        /* Draws Dots */
        for(int i = 0; i<width; i++){
            int x = pan.dotW[i];
            for(int j = 0; j<height;j++){
                int y = pan.dotT[j];
                g.drawOval(x, y, 5, 5);
            }
        }
        
        /* Draws Lines */
        for(int i=1;i<pan.paths+1;i++){
            yOld = pan.linesT.get(i-1);
            xOld = pan.linesW.get(i-1);
            yNew = pan.linesT.get(i);
            xNew = pan.linesW.get(i);
            g.drawLine(xOld, yOld, xNew, yNew);
        }
    }
}