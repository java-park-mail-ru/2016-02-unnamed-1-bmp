package utils;

public class TimeHelper {

    public static void sleep(int stepTime){
        try{
            Thread.sleep(stepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}