package laboratorio5.threads;

public class ThreadD extends CustomThread {

    public ThreadD(int pMode, int pIterations) {
        super(pMode, "D", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureD();
    }
}
