package laboratorio5.threads;

import laboratorio5.Data;

import java.sql.SQLException;

public abstract class CustomThread extends Thread{

    public String name;
    public Data data;
    public int iterations;

    public CustomThread(int pMode, String pName, int pIterations) {
        name = pName;
        data = new Data(pMode == Data.LOCKING, name);
        iterations = pIterations;
    }

    @Override
    public void run() {
        try {
            data.syncronize();
            for (int i = 0; i < iterations; i++) {
                boolean res = myProcedure();
                while (!res) {
                    res = myProcedure();
                }
            }
            data.finish();
        } catch (SQLException e) {
            System.out.println("Error en la transación " + name);
            System.out.println(e.getMessage());
        }
    }

    public boolean myProcedure() {
        return data.procedureTest1();
    }
}
