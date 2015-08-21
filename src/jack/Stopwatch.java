/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jack;

/**
 *
 * @author Calvin
 */
public class Stopwatch {
    public int totalSeconds;
    
    public  Stopwatch(int initialTime){
        totalSeconds = initialTime;
    }
    public void incrementTime(int seconds){
        totalSeconds += seconds;
    }
    public String getTime(){
        int seconds = totalSeconds % 60;
        int minutes = (int)((double)totalSeconds/60.0);
        int tensOfSeconds = (int)(seconds / 10.0);
        seconds = seconds % 10;
        return minutes + ":" + tensOfSeconds + "" + seconds;
    }
}
