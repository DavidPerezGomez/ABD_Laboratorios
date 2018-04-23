package laboratorio5;

import laboratorio5.threads.*;

public class Main {

    static final int mode = Data.LOCKING;
    public static final int NUMBER_OF_ITERATIONS = 100;
    public static final int NUMBER_OF_THREADS = 6;

    public static void main(String[] args) {
        Data mainData = new Data(mode == Data.LOCKING, "main");
        ThreadA threadA = new ThreadA(mode);
        ThreadB threadB = new ThreadB(mode);
        ThreadC threadC = new ThreadC(mode);
        ThreadD threadD = new ThreadD(mode);
        ThreadE threadE = new ThreadE(mode);
        ThreadF threadF = new ThreadF(mode);

        mainData.initializeVariables();
        mainData.showInitialValues();

        new Thread(threadA).start();
        new Thread(threadB).start();
        new Thread(threadC).start();
        new Thread(threadD).start();
        new Thread(threadE).start();
        new Thread(threadF).start();

        mainData.showFinalValues();
    }

}
