package laboratorio5;

import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

    private Connection conn;
    private Statement st;
    private String threadName;

    private String server = "jdbc:mysql://";
    private String address = "192.168.56.11";
//    private String address = "127.0.0.1";
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
    public static final int EXCLUSIVE_LOCKING = 2 * LOCKING;

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
                    server + address + ":" + port + "/" + database,
                    user, password);
            conn.setAutoCommit(false);
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        threadName = pThreadName;
    }

    private int getValue(int pLockingMode, String pVariable) throws SQLException {
        String mode = "";
        if (pLockingMode == SHARE_LOCKING) {
            mode = "LOCK IN SHARE MODE";
        } else if (pLockingMode == EXCLUSIVE_LOCKING) {
            mode = "FOR UPDATE";
        }
        String msg1 = String.format("%s --> Getting value of %s (%s)", threadName, pVariable, mode);
        System.out.println(msg1);
        String sql = "SELECT " + value_field + " FROM " + table_name + " WHERE " + name_field + "='" + pVariable + "' " + mode;
        ResultSet res = st.executeQuery(sql);
        res.next();
        int result = res.getInt(value_field);
        String msg2 = String.format("%s --> Finished getting value of %s (%s): %d", threadName, pVariable, mode, result);
        System.out.println(msg2);
        return result;
    }

    private boolean setValue(int pLockingMode, String pVariable, int pValue) throws SQLException {
        String msg1 = String.format("%s --> Setting value of %s to %d", threadName, pVariable, pValue);
        System.out.println(msg1);
        String sql = "UPDATE " + table_name + " SET " + value_field + "='" + pValue + "' WHERE " + name_field + "='" + pVariable + "'";
        int rows = st.executeUpdate(sql);
        String msg2 = String.format("%s --> Finished setting value of %s", threadName, pVariable);
        System.out.println(msg2);
        return rows > 0;
    }

    private void commit() throws SQLException {
        String msg1 = String.format("%s --> Starting commit", threadName);
        System.out.println(msg1);
        String sql = "COMMIT";
        st.execute(sql);
        String msg2 = String.format("%s --> Finished commit", threadName);
        System.out.println(msg2);
    }

    private void rollback() {
        try {
            String msg1 = String.format("%s --> Starting rollback", threadName);
            System.out.println(msg1);
            String sql = "ROLLBACK";
            st.execute(sql);
            String msg2 = String.format("%s --> Finished rollback", threadName);
            System.out.println(msg2);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void increaseBarrierValue() {
        try {
            int barrier = getValue(EXCLUSIVE_LOCKING, M);
            setValue(EXCLUSIVE_LOCKING, M, barrier + 1);
            commit();
        } catch (SQLException e) {
            rollback();
        }
    }

    private void decreaseBarrierValue() {
        try {
            int barrier = getValue(EXCLUSIVE_LOCKING, M);
            setValue(EXCLUSIVE_LOCKING, M, barrier - 1);
            commit();
        } catch (SQLException e) {
            rollback();
        }
    }

    private int getBarrierValue() {
        try {
            int m = getValue(SHARE_LOCKING, M);
            commit();
            return m;
        } catch (SQLException e) {
            rollback();
            return -1;
        }
    }

    public void syncronize() {
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
//            setValue(EXCLUSIVE_MODE, M, 0);
            commit();
            return true;
        } catch (SQLException e) {
            rollback();
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
            commit();
            return true;
        } catch (SQLException e) {
            rollback();
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
            int x, y, z, t, expected_t;
            x = getValue(SHARE_MODE, X);
            y = getValue(SHARE_MODE, Y);
            z = getValue(SHARE_MODE, Z);
            t = getValue(SHARE_MODE, T);
            expected_t = getValue(SHARE_MODE, A) +
                         getValue(SHARE_MODE, B) +
                         getValue(SHARE_MODE, C) +
                         getValue(SHARE_MODE, D) +
                         getValue(SHARE_MODE, E) +
                         getValue(SHARE_MODE, F);
            String color;
            String closer = "\033[0m";
            System.out.println("Final value of " + A + ": " + Integer.toString(getValue(SHARE_MODE, A)));
            System.out.println("Final value of " + B + ": " + Integer.toString(getValue(SHARE_MODE, B)));
            System.out.println("Final value of " + C + ": " + Integer.toString(getValue(SHARE_MODE, C)));
            System.out.println("Final value of " + D + ": " + Integer.toString(getValue(SHARE_MODE, D)));
            System.out.println("Final value of " + E + ": " + Integer.toString(getValue(SHARE_MODE, E)));
            System.out.println("Final value of " + F + ": " + Integer.toString(getValue(SHARE_MODE, F)));
            color = (x == 0) ? "\033[32m" : "\033[31m";
            System.out.println(color + "Final value of " + X + ": " + Integer.toString(x) + closer);
            color = (y == 0) ? "\033[32m" : "\033[31m";
            System.out.println(color + "Final value of " + Y + ": " + Integer.toString(y) + closer);
            color = (z == 0) ? "\033[32m" : "\033[31m";
            System.out.println(color + "Final value of " + Z + ": " + Integer.toString(z) + closer);
            color = (t == expected_t) ? "\033[32m" : "\033[31m";
            System.out.println(color + "Final value of " + T + ": " + Integer.toString(t) + closer);


            System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
            System.out.println("Expected final value of " + T + ": " + Integer.toString(expected_t));
            commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean procedureTest1() {
        try {
            int a = getValue(EXCLUSIVE_MODE, A);
            setValue(EXCLUSIVE_MODE, A, a + 1);
            a = getValue(SHARE_MODE, A);
            commit();
            return true;
        } catch (SQLException e) {
            rollback();
            return false;
        }
    }

    public boolean procedureTest2() {
        try {
            int a = getValue(EXCLUSIVE_MODE, A);
            setValue(EXCLUSIVE_MODE, A, a + 2);
            a = getValue(SHARE_MODE, A);
            commit();
            return true;
        } catch (SQLException e) {
            rollback();
            return false;
        }
    }

    public boolean procedureA() {
        try {
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
            rollback();
            return false;
        }
    }

    public boolean procedureB() {
        try {
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
            rollback();
            return false;
        }
    }

    public boolean procedureC() {
        try {
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
            rollback();
            return false;
        }
    }

    public boolean procedureD() {
        try {
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
            rollback();
            return false;
        }
    }

    public boolean procedureE() {
        try {
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
            rollback();
            return false;
        }
    }

    public boolean procedureF() {
        try {
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
            rollback();
            return false;
        }
    }

}
