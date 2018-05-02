package laboratorio5.threads;

public class ThreadB extends CustomThread {

    public ThreadB(int pMode, int pIterations) {
        super(pMode, "B", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureB();
    }
}
