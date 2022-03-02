package a3;

/**
 * @author        Aidan Larock 
 * @studentNumber #6186076
 * @assignment    3
 * 
 * HillClimb
 * an optimization method that uses the hill climber optimization.
 * It creates a random string of length paths based on the amount of paths
 * determined by the user in the Main class. It randomly changes
 * one position to a random path direction. Finally it tests each sequence 
 * to determine if it has more paths completed than the current best sequence
 * and reverts if it isn't, and updates the best path if it is.
 */

/* Imports */
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* class HillClimb */
/**
 * graph/pan: Main and MyPanel 
 * score: current score of sequence
 * paths: number of total paths (#dots-1)
 * height/width: maximum height and width
 * thisScore: bestScore 
 * sequence/bestSequence: Current Sequence and best Sequence directions
 * sent: Current sequence sent to Main to update 
 * collision: Collision array to test if points have been visited
 */
public class HillClimb {
    MyPanel pan;
    Main graph;   
    int score,paths,height,width,thisScore;
    String[] sequence, bestSequence; 
    int[] sent;
    boolean[][] collision;
  
    /**
     * @param num : number of paths 
     * @param tall : maximum height
     * @param wide : maximum width
     */
    public HillClimb(int num, int tall, int wide) {
        paths = num;
        height = tall;
        width = wide;
        sequence = new String[paths];
        bestSequence = new String[paths];
        sent = new int[paths];
        collision = new boolean[16][17];
        randomSeq(paths);
        score = 0;
    }
    
    /**
     * begin: called by Main
     * starts the testing, updating and movement
     * for the hill climber optimization
     * 
     * @param best : current best score
     */
    public void begin(AtomicInteger best){
        thisScore = best.get(); // update best score
        resetCollision();       // reset the collision array
        test();                 // test if current sequence is the best
        delay(10);              // delay for easier viewing
        move(paths);            // change random path to random direction
    }

    /**
     * test: called by begin
     * calculates the score of the 
     * current sequence, updates bestSequence 
     * if sequence is better
     */
    private void test(){
        score = 0;
        int x = 0;
        int y = 0;  
        collision[0][0] = true;
        // reset variables to 0
        
        /*
         * Testing bounds and collision
         * Adding 1 to score if path isn't out of bounds
         * or visiting already visited point
         */   
        for(int i=0;i<paths;i++){
            if(sequence[i].equals("N")){    // North
                y = y-1;
                if(y<0){        // Bounds
                    break;
                } 
                if(collision[x][y] == true){
                    break;      // collision
                }else{
                collision[x][y] = true;
                score = score + 1 ; // score Increment
                }
            }
            if(sequence[i].equals("S")){    // South
                y = y+1;
                if(y>=height){  // Bounds
                    break;
                } 
                if(collision[x][y] == true){ 
                    break;      // collision
                }else{
                collision[x][y] = true;
                score = score + 1 ; // score Increment
                }
            }
            if(sequence[i].equals("W")){    // West
                x = x-1;
                if(x<0){        // Bounds
                    break;
                } 
                if(collision[x][y] == true){
                    break;      // collision
                }else{
                collision[x][y] = true;
                score = score + 1 ; // score Increment
                }
            }
            if(sequence[i].equals("E")){    // East
                x = x+1;
                if(x>=width){   // Bounds
                    break;
                } 
                if(collision[x][y] == true){
                    break;      // collision
                }else{
                collision[x][y] = true;
                score = score + 1 ; // score Increment
                }
            }
        }
        /* If the score of sequence is greater, send data to sent[] */
        if(score>thisScore){
            thisScore = score;  // update bestScore
            for(int i=0;i<score;i++){
                switch(sequence[i]) {
                    case "N":  
                        sent[i] = -1;
                        break;
                    case "S":
                        sent[i] = 1; 
                        break;
                    case "W":
                        sent[i] = -2; 
                        break;
                    case "E":
                        sent[i] = 2; 
                        break;
                }
            }
            System.arraycopy(sequence, 0, bestSequence, 0, paths); // Update bestSequence
        }else{
            System.arraycopy(bestSequence, 0, sequence, 0, paths); // Revert to bestSequence
        }
    }
  
    /**
     * ResetCollision: Called by begin
     * resets the collision array
     * for the next test
     */
    private void resetCollision(){
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                collision[i][j] = false;
            }
        }
    }
    
    /**
     * delay: called by begin
     * creates a small delay for easier
     * viewing of threads
     * 
     * @param time : time to delay in milliseconds
     */
    private void delay(long time) {
        try {Thread.sleep(time);}catch (InterruptedException ie){}
    }
 
    /**
     * move: called by begin
     * randomly selects one spot in the sequence
     * and sets that spot to a random direction
     * Uses Random
     * 
     * @param num : number of spots in sequence
     */
    public void move(int num){
        Random randomGen = new Random(); // Random 
        int randomSpot = randomGen.nextInt(num) + 0; // random spot in sequence
        int randomDirec = randomGen.nextInt(4) + 1; // random direction in sequence
        switch(randomDirec) {
            case 1:
                sequence[randomSpot] = "N";
                break;
            case 2:
                sequence[randomSpot] = "S";
                break;
            case 3:
                sequence[randomSpot] = "W";
                break;
            case 4:
                sequence[randomSpot] = "E";
                break;
        }
    }
    
    /**
     * randomSeq: called by HillClimb
     * creates a random sequence of length num
     * 
     * @param num : number of paths 
     */
    private void randomSeq(int num){
        Random randomGenerator = new Random();
        for(int i=0;i<num;i++){
            int randomInt = randomGenerator.nextInt(4) + 1;
            switch(randomInt) {
            case 1:
                sequence[i] = "N";
                bestSequence[i] ="N";
                break;
            case 2:
                sequence[i] = "S";
                bestSequence[i] ="S";
                break;
            case 3:
                sequence[i] = "W";
                bestSequence[i] ="W";
                break;
            case 4:
                sequence[i] = "E";
                bestSequence[i] ="E";
                break;
            }
        }
    }
}
