package laboratorio5.threads;

public class ThreadA extends CustomThread {

    public ThreadA(int pMode) {
        super(pMode, "A");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureA();
    }
}
