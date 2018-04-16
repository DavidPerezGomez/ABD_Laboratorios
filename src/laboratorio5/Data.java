package laboratorio5;

import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

    private Connection conn;
    private Statement st;
    private String sentence;

    private String server = "jdbc:mysql://";
    private String address = "127.0.0.1";
    private String user = "test";
    private String port = "3306";
    private String password = "";
    private String database = "concurrency_control";
    private String table_name = "variables";
    private String name_field = "name";
    private String value_field = "value";

    static final int NONLOCKING = 0;
    static final int LOCKING = 1;
    static final int SHARE_LOCKING = LOCKING;
    static final int EXCLUSIVE_LOCKING = 2*LOCKING;
    static final int NUMBER_OF_ITERATIONS = 100;
    static final int NUMBER_OF_THREADS = 6;

    static final String X = "X";
    static final String Y = "Y";
    static final String Z = "Z";
    static final String T = "T";
    static final String A = "A";
    static final String B = "B";
    static final String C = "C";
    static final String D = "D";
    static final String E = "E";
    static final String F = "F";
    static final String M = "M";

    private int SHARE_MODE;
    private int EXCLUSIVE_MODE;

    public Data(boolean pLocking) {
        if (!pLocking) {
            SHARE_MODE = NONLOCKING;
            EXCLUSIVE_MODE = NONLOCKING;
        } else {
            SHARE_MODE = SHARE_LOCKING;
            EXCLUSIVE_MODE = EXCLUSIVE_LOCKING;
        }

        // load MySQL driver
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // open connection
        try {
            conn = DriverManager.getConnection(
				server+address+":"+port + "/" + database,
				user,password);
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getValue(int pLockingMode, String pVariable) throws SQLException {
        String sql = "SELECT " + value_field + " FROM " + table_name + " WHERE " + name_field + "==" + pVariable;
//        TODO completar
        return -1;
    }

    public boolean setValue(int pLockingMode, String pVariable, int pValue) throws SQLException {
        String sql = "UPDATE " + table_name + " SET " + value_field + "=" + pValue + " WHERE " + name_field + "==" + pVariable;
//        TODO completar
        return false;
    }

    private void increaseBarrierValue() {
//        TODO completar
    }

    private void decreaseBarrierValue() {
//        TODO completar
    }

    private int getBarrierValue() {
//        TODO completar
        return -1;
    }

    public void syncronize() {
        increaseBarrierValue();
        int barrierValue = getBarrierValue();

        while (barrierValue < Data.NUMBER_OF_THREADS) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
                barrierValue = getBarrierValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        decreaseBarrierValue();
    }

    public boolean showInitialValues() {
        try {
            System.out.println("Initial value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
            System.out.println("Initial value of " + Y + ": " + Integer.toString(getValue(NONLOCKING, Y)));
            System.out.println("Initial value of " + Z + ": " + Integer.toString(getValue(NONLOCKING, Z)));
            System.out.println("Initial value of " + T + ": " + Integer.toString(getValue(NONLOCKING, T)));
            System.out.println("Initial value of " + A + ": " + Integer.toString(getValue(NONLOCKING, A)));
            System.out.println("Initial value of " + B + ": " + Integer.toString(getValue(NONLOCKING, B)));
            System.out.println("Initial value of " + C + ": " + Integer.toString(getValue(NONLOCKING, C)));
            System.out.println("Initial value of " + D + ": " + Integer.toString(getValue(NONLOCKING, D)));
            System.out.println("Initial value of " + E + ": " + Integer.toString(getValue(NONLOCKING, E)));
            System.out.println("Initial value of " + F + ": " + Integer.toString(getValue(NONLOCKING, F)));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showFinalValues() {
        int barrierValue = getBarrierValue();

        while (barrierValue < 1) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
                barrierValue = getBarrierValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (barrierValue > 0) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
                barrierValue = getBarrierValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
            System.out.println("Final value of " + Y + ": " + Integer.toString(getValue(NONLOCKING, Y)));
            System.out.println("Final value of " + Z + ": " + Integer.toString(getValue(NONLOCKING, Z)));
            System.out.println("Final value of " + T + ": " + Integer.toString(getValue(NONLOCKING, T)));
            System.out.println("Final value of " + A + ": " + Integer.toString(getValue(NONLOCKING, A)));
            System.out.println("Final value of " + B + ": " + Integer.toString(getValue(NONLOCKING, B)));
            System.out.println("Final value of " + C + ": " + Integer.toString(getValue(NONLOCKING, C)));
            System.out.println("Final value of " + D + ": " + Integer.toString(getValue(NONLOCKING, D)));
            System.out.println("Final value of " + E + ": " + Integer.toString(getValue(NONLOCKING, E)));
            System.out.println("Final value of " + F + ": " + Integer.toString(getValue(NONLOCKING, F)));


            System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + T + ": " + Integer.toString(
                            getValue(NONLOCKING, A) +
                            getValue(NONLOCKING, B) +
                            getValue(NONLOCKING, C) +
                            getValue(NONLOCKING, D) +
                            getValue(NONLOCKING, E)));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean procedureA(String pName, int pCounter) {
//        TODO completar
        return false;
    }

}