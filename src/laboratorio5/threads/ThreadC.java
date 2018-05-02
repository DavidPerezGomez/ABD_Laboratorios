package laboratorio5.threads;

public class ThreadC extends CustomThread {

    public ThreadC(int pMode, int pIterations) {
        super(pMode, "C", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureC();
    }
}
