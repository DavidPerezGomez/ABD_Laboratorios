package laboratorio5.threads;

public class ThreadE extends CustomThread {

    public ThreadE(int pMode) {
        super(pMode, "E");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureE();
    }
}
