package laboratorio5.threads;

public class ThreadA extends CustomThread {

    public ThreadA(int pMode, int pIterations) {
        super(pMode, "A", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureA();
    }
}
