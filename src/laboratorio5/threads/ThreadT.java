package laboratorio5.threads;

import laboratorio5.Data;

public class ThreadT extends Thread {

    private String name;
    private Data data;

    public ThreadT(boolean pMode) {
        name = "A";
        data = new Data(pMode);
    }

    public void run() {
//        TODO completar
    }
}
