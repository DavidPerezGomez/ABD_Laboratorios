package laboratorio5.threads;

public class ThreadD extends CustomThread {

    public ThreadD(int pMode) {
        super(pMode, "D");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureD();
    }
}
