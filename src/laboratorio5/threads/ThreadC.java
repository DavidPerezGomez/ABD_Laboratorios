package laboratorio5.threads;

public class ThreadC extends CustomThread {

    public ThreadC(int pMode) {
        super(pMode, "C");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureC();
    }
}
