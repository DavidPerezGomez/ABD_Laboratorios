package laboratorio5.threads;

import laboratorio5.Data;

import java.sql.SQLException;

public abstract class CustomThread extends Thread{

    public String name;
    public Data data;

    public CustomThread(int pMode, String pName) {
        name = pName;
        data = new Data(pMode == Data.LOCKING, name);
    }

    @Override
    public void run() {
        try {
            data.syncronize();
            boolean res = myProcedure();
            while (!res) {
                res = myProcedure();
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
