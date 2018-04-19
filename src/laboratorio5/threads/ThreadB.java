package laboratorio5.threads;

public class ThreadB extends CustomThread {

    public ThreadB(int pMode) {
        super(pMode, "B");
    }

    @Override
    public boolean myProcedure() {
        return data.procedureB();
    }
}
