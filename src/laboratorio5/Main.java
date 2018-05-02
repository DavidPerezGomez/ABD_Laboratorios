package laboratorio5;

import laboratorio5.threads.*;

public class Main {

    static final int mode = Data.LOCKING;
    public static final int NUMBER_OF_ITERATIONS = 100;
    public static final int NUMBER_OF_THREADS = 6;

    // INSTRUCCIONES DE USO:
    // 1. uno de los clientes inicializa las variables e inicia sus 3 hilos
    // 2. el otro cliente inicia sus 3 hilos SIN inicializar las variables
    // 3. ...
    // 4. profit

    public static void main(String[] args) {
        Data mainData = new Data(mode == Data.LOCKING, "main");
//        ThreadA threadA = new ThreadA(mode, NUMBER_OF_ITERATIONS);
//        ThreadB threadB = new ThreadB(mode, NUMBER_OF_ITERATIONS);
//        ThreadC threadC = new ThreadC(mode, NUMBER_OF_ITERATIONS);
        ThreadD threadD = new ThreadD(mode, NUMBER_OF_ITERATIONS);
        ThreadE threadE = new ThreadE(mode, NUMBER_OF_ITERATIONS);
        ThreadF threadF = new ThreadF(mode, NUMBER_OF_ITERATIONS);


//        mainData.initializeVariables();
        mainData.showInitialValues();

//      new Thread(threadA).start();
//      new Thread(threadB).start();
//      new Thread(threadC).start();
        new Thread(threadD).start();
        new Thread(threadE).start();
        new Thread(threadF).start();

        mainData.showFinalValues();
    }

}
