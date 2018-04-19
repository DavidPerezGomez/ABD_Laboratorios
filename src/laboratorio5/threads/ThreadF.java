package laboratorio5.threads;

public class ThreadF extends CustomThread {

    public ThreadF(int pMode) {
        super(pMode, "F");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureF();
    }
}
