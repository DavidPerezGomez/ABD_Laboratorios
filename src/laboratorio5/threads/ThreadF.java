package laboratorio5.threads;

public class ThreadF extends CustomThread {

    public ThreadF(int pMode, int pIterations) {
        super(pMode, "F", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureF();
    }
}
