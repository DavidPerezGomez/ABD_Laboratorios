package laboratorio5;

import sun.security.provider.SHA;

import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

    private Connection conn;
    private Statement st;
    private String sentence;
    private String threadName;

    private String server = "jdbc:mysql://";
    private String address = "192.168.56.11";
    private String user = "test";
    private String port = "3306";
    private String password = "";
    private String database = "concurrency_control";
    private String table_name = "variables";
    private String name_field = "name";
    private String value_field = "value";

    public static final int NONLOCKING = 0;
    public static final int LOCKING = 1;
    public static final int SHARE_LOCKING = LOCKING;
    public static final int EXCLUSIVE_LOCKING = 2*LOCKING;

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

    public Data(boolean pLocking, String pThreadName) {
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
			System.exit(1);
        }

        // open connection
        try {
            conn = DriverManager.getConnection(
				server+address+":"+port + "/" + database,
				user,password);
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
			System.exit(1);
        }

        threadName = pThreadName;
    }

    private void setTransaction() throws SQLException {
        String sql = "SET GLOBAL TRANSACTION READ WRITE";
        st.execute(sql);
    }

    private void startTransaction() throws SQLException {
        String msg1 = String.format("%s --> Starting transaction", threadName);
        String msg2 = String.format("%s --> Transaction started", threadName);
        System.out.println(msg1);
        String sql = "START TRANSACTION";
        st.execute(sql);
        sql = "SET autocommit=0;";
        st.execute(sql);
        System.out.println(msg2);
    }

    private int getValue(int pLockingMode, String pVariable) throws SQLException {
        String mode = "";
        if (pLockingMode == SHARE_LOCKING) {
            mode = "LOCK IN SHARE MODE";
        } else if (pLockingMode == EXCLUSIVE_LOCKING) {
            mode = "FOR UPDATE";
        }
        String msg1 = String.format("%s --> Getting value of %s (%s)", threadName, pVariable, mode);
        String msg2 = String.format("%s --> Finished getting value of %s (%s)", threadName, pVariable, mode);
        String sql = "SELECT " + value_field + " FROM " + table_name + " WHERE " + name_field + "='" + pVariable + "' " + mode;
        System.out.println(msg1);
        ResultSet res = st.executeQuery(sql);
        System.out.println(msg2);
        res.next();
        return res.getInt(value_field);
    }

    private boolean setValue(int pLockingMode, String pVariable, int pValue) throws SQLException {
        String msg1 = String.format("%s --> Setting value of %s to %d", threadName, pVariable, pValue);
        String msg2 = String.format("%s --> Finished setting value of %s", threadName, pVariable);
        System.out.println(msg1);
        String sql = "UPDATE " + table_name + " SET " + value_field + "='" + pValue + "' WHERE " + name_field + "='" + pVariable + "'";
        System.out.println(msg2);
        return st.executeUpdate(sql) > 0;
    }

    private void commit() throws SQLException{
        String msg1 = String.format("%s --> Starting commit", threadName);
        String msg2 = String.format("%s --> Finished commit", threadName);
        System.out.println(msg1);
        String sql = "COMMIT";
        System.out.println(msg2);
        st.execute(sql);
    }

    private void rollback() throws SQLException{
        String msg1 = String.format("%s --> Starting rollback", threadName);
        String msg2 = String.format("%s --> Finished rollback", threadName);
        System.out.println(msg1);
        String sql = "ROLLBACK";
        System.out.println(msg2);
        st.execute(sql);
    }

    private void increaseBarrierValue() throws SQLException {
        setTransaction();
        startTransaction();
        int barrier = getValue(EXCLUSIVE_LOCKING, M);
        setValue(EXCLUSIVE_LOCKING, M, barrier + 1);
        commit();
    }

    private void decreaseBarrierValue() throws SQLException {
        setTransaction();
        startTransaction();
        int barrier = getValue(EXCLUSIVE_LOCKING, M);
        setValue(EXCLUSIVE_LOCKING, M, barrier - 1);
        commit();
    }

    private int getBarrierValue() throws SQLException {
        return getValue(SHARE_LOCKING, M);
    }

    public void syncronize() throws SQLException {
        increaseBarrierValue();
        int barrierValue = getBarrierValue();

        while (barrierValue < Main.NUMBER_OF_THREADS) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
                barrierValue = getBarrierValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() throws SQLException {
        decreaseBarrierValue();
    }

    public boolean initializeVariables() {
        try {
            setValue(EXCLUSIVE_MODE, X, 0);
            setValue(EXCLUSIVE_MODE, Y, 0);
            setValue(EXCLUSIVE_MODE, Z, 0);
            setValue(EXCLUSIVE_MODE, T, 0);
            setValue(EXCLUSIVE_MODE, A, 0);
            setValue(EXCLUSIVE_MODE, B, 0);
            setValue(EXCLUSIVE_MODE, C, 0);
            setValue(EXCLUSIVE_MODE, D, 0);
            setValue(EXCLUSIVE_MODE, E, 0);
            setValue(EXCLUSIVE_MODE, F, 0);
            setValue(EXCLUSIVE_MODE, M, 0);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showInitialValues() {
        try {
            System.out.println("Initial value of " + X + ": " + Integer.toString(getValue(SHARE_MODE, X)));
            System.out.println("Initial value of " + Y + ": " + Integer.toString(getValue(SHARE_MODE, Y)));
            System.out.println("Initial value of " + Z + ": " + Integer.toString(getValue(SHARE_MODE, Z)));
            System.out.println("Initial value of " + T + ": " + Integer.toString(getValue(SHARE_MODE, T)));
            System.out.println("Initial value of " + A + ": " + Integer.toString(getValue(SHARE_MODE, A)));
            System.out.println("Initial value of " + B + ": " + Integer.toString(getValue(SHARE_MODE, B)));
            System.out.println("Initial value of " + C + ": " + Integer.toString(getValue(SHARE_MODE, C)));
            System.out.println("Initial value of " + D + ": " + Integer.toString(getValue(SHARE_MODE, D)));
            System.out.println("Initial value of " + E + ": " + Integer.toString(getValue(SHARE_MODE, E)));
            System.out.println("Initial value of " + F + ": " + Integer.toString(getValue(SHARE_MODE, F)));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showFinalValues() {
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            System.out.println("Final value of " + X + ": " + Integer.toString(getValue(SHARE_MODE, X)));
            System.out.println("Final value of " + Y + ": " + Integer.toString(getValue(SHARE_MODE, Y)));
            System.out.println("Final value of " + Z + ": " + Integer.toString(getValue(SHARE_MODE, Z)));
            System.out.println("Final value of " + T + ": " + Integer.toString(getValue(SHARE_MODE, T)));
            System.out.println("Final value of " + A + ": " + Integer.toString(getValue(SHARE_MODE, A)));
            System.out.println("Final value of " + B + ": " + Integer.toString(getValue(SHARE_MODE, B)));
            System.out.println("Final value of " + C + ": " + Integer.toString(getValue(SHARE_MODE, C)));
            System.out.println("Final value of " + D + ": " + Integer.toString(getValue(SHARE_MODE, D)));
            System.out.println("Final value of " + E + ": " + Integer.toString(getValue(SHARE_MODE, E)));
            System.out.println("Final value of " + F + ": " + Integer.toString(getValue(SHARE_MODE, F)));


            System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + T + ": " + Integer.toString(
                            getValue(SHARE_MODE, A) +
                            getValue(SHARE_MODE, B) +
                            getValue(SHARE_MODE, C) +
                            getValue(SHARE_MODE, D) +
                            getValue(SHARE_MODE, E)));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean procedureTest1(){
        try {
            setTransaction();
            startTransaction();
            int a = getValue(EXCLUSIVE_MODE, A);
            setValue(EXCLUSIVE_MODE, A, a + 1);
            a = getValue(SHARE_MODE, A);
            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureTest2(){
        try {
            setTransaction();
            startTransaction();
            int a = getValue(EXCLUSIVE_MODE, A);
            setValue(EXCLUSIVE_MODE, A, a + 2);
            a = getValue(SHARE_MODE, A);
            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureA() {
        try {
            setTransaction();
            startTransaction();

            int x = getValue(EXCLUSIVE_MODE, X);
            x++;
            setValue(EXCLUSIVE_MODE, X, x);
            int t = getValue(EXCLUSIVE_MODE, T);
            int a = getValue(EXCLUSIVE_MODE, A);
            int y = getValue(SHARE_MODE, Y);
            t += y;
            a += y;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, A, a);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureB() {
        try {
            setTransaction();
            startTransaction();

            int y = getValue(EXCLUSIVE_MODE, Y);
            y++;
            setValue(EXCLUSIVE_MODE, Y, y);
            int t = getValue(EXCLUSIVE_MODE, T);
            int b = getValue(EXCLUSIVE_MODE, B);
            int z = getValue(SHARE_MODE, Z);
            t += z;
            b += z;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, B, b);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureC() {
        try {
            setTransaction();
            startTransaction();

            int z = getValue(EXCLUSIVE_MODE, Z);
            z++;
            setValue(EXCLUSIVE_MODE, Z, z);
            int t = getValue(EXCLUSIVE_MODE, T);
            int c = getValue(EXCLUSIVE_MODE, C);
            int x = getValue(SHARE_MODE, X);
            t += x;
            c += x;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, C, c);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureD() {
        try {
            setTransaction();
            startTransaction();

            int t = getValue(EXCLUSIVE_MODE, T);
            int d = getValue(EXCLUSIVE_MODE, D);
            int z = getValue(SHARE_MODE, Z);
            t += z;
            d += z;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, D, d);
            int x = getValue(EXCLUSIVE_MODE, X);
            x -= 1;
            setValue(EXCLUSIVE_MODE, X, x);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureE() {
        try {
            setTransaction();
            startTransaction();

            int t = getValue(EXCLUSIVE_MODE, T);
            int e = getValue(EXCLUSIVE_MODE, E);
            int x = getValue(SHARE_MODE, X);
            t += x;
            e += x;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, E, e);
            int y = getValue(EXCLUSIVE_MODE, Y);
            y -= 1;
            setValue(EXCLUSIVE_MODE, Y, y);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean procedureF() {
        try {
            setTransaction();
            startTransaction();

            int t = getValue(EXCLUSIVE_MODE, T);
            int f = getValue(EXCLUSIVE_MODE, F);
            int y = getValue(SHARE_MODE, Y);
            t += y;
            f += y;
            setValue(EXCLUSIVE_MODE, T, t);
            setValue(EXCLUSIVE_MODE, F, f);
            int z = getValue(EXCLUSIVE_MODE, Z);
            z -= 1;
            setValue(EXCLUSIVE_MODE, Z, z);

            commit();
            return true;
        } catch (SQLException e) {
            try {
                rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
