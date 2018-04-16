package laboratorio5.threads;

import laboratorio5.Data;

public class ThreadA extends Thread {

    private String name;
    private Data data;

    public ThreadA(boolean pMode) {
        name = "A";
        data = new Data(pMode);
    }

    public void run() {
//        TODO completar
    }
}
