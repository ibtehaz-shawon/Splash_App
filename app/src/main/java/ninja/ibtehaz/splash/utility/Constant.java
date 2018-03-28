package ninja.ibtehaz.splash.utility;

/**
 * Created by ibtehaz on 5/16/17.
 */

public class Constant {

    private static Constant instance = null;
    private static int runningDownload;

    public Constant() {
        // Exists only to defeat instantiation.
    }

    /**
     * singleTon creation
     * @return
     */
    public static Constant getInstance() {
        if (instance == null) {
            instance = new Constant();
        }
        return instance;
    }

    public static void setRunningDownload(int counter) {
        runningDownload = counter;
    }


    public static int getRunningDownload() {
        return runningDownload;
    }

}
