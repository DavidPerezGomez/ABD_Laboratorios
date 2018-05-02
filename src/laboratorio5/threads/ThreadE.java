package laboratorio5.threads;

public class ThreadE extends CustomThread {

    public ThreadE(int pMode, int pIterations) {
        super(pMode, "E", pIterations);
    }

    @Override
    public boolean myProcedure() {
        return data.procedureE();
    }
}
