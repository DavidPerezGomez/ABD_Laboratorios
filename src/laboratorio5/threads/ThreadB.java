package laboratorio5.threads;

import laboratorio5.Data;

public class ThreadB extends Thread {

    private String name;
    private Data data;

    public ThreadB(boolean pMode) {
        name = "A";
        data = new Data(pMode);
    }

    public void run() {
//        TODO completar
    }
}
