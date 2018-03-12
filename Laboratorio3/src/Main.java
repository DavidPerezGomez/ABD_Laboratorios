import java.io.Console;
import java.util.Random;

public class Main {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        String addr, port, user, pass;
	    if (args.length == 0) {
            addr = "127.0.0.1";
            port = "3306";
            user = "auditorAB";
            pass = "euiti";
        } else {
            addr = "127.0.0.1";
            port = "3306";
            user = "auditorAB";
            pass = "euiti";
        }
        insertAuditAB(addr, port, user, pass);
//        insertAuditCD(addr, port, user, pass);
    }

    private static void insertAuditAB(String pAddr, String pPort, String pUser, String pPass) {
        Data data = null;
        String db = "auditAB";
        try {
            data = new Data(pAddr, pPort, pUser, pPass, db);
        } catch (Exception e) {
            System.out.print("\33[31mError al establecer la conexión\33[0m\n");
            e.printStackTrace();
            System.exit(1);
        }

        String tabla1 = "tableA";
        String tabla2 = "tableB";
        String[] commands = new String[]{
                String.format("INSERT INTO %s VALUES(%s, %s);", tabla1, "%d", "%d"),
                String.format("INSERT INTO %s VALUES(%s, %s);", tabla2, "%d", "%d")};
        int iterations = 14900;
        executeCommands(data, commands, iterations);
    }

    private static void insertAuditCD(String pAddr, String pPort, String pUser, String pPass) {
        Data data = null;
        String db = "auditCD";
        try {
            data = new Data(pAddr, pPort, pUser, pPass, db);
        } catch (Exception e) {
            System.out.print("\33[31mError al establecer la conexión\33[0m\n");
            e.printStackTrace();
            System.exit(1);
        }

        String tabla1 = "tableC";
        String tabla2 = "tableD";
        String[] commands = new String[]{
                String.format("INSERT INTO %s VALUES(%s, %s);", tabla1, "%d", "%d"),
                String.format("INSERT INTO %s VALUES(%s, %s);", tabla2, "%d", "%d")};
        int iterations = 10;
        executeCommands(data, commands, iterations);
    }

    private static void executeCommands(Data pData, String[] pCommands, int pIterations) {
	    int maxCode = Integer.MAX_VALUE/1000-1;
	    int maxValue = Integer.MAX_VALUE/1000-1;
        for (int i = 1; i <= pIterations; i++) {
            for (String command : pCommands) {
                int code = rng(0, maxCode);
                int value = rng(0, maxValue);
                String sql = String.format(command, code, value);
                System.out.println(sql);
                pData.executeUpdate(sql);
            }
            System.out.println(getProgressString(i, pIterations));
        }
    }

    private static String getProgressString(int pCurrent, int pTotal) {
	    StringBuilder progress = new StringBuilder();
	    progress.append("\r");
	    progress.append(pCurrent + "/" + pTotal);
	    int percent = 100*pCurrent/pTotal;
	    progress.append(" (" + percent + "%)");
	    int maxBars = 50;
	    int currBars = maxBars * percent / 100;
	    progress.append(" [");
        for (int i = 0; i < maxBars; i++) {
            if (i < currBars)
                progress.append("█");
            else
                progress.append(" ");
        }
        progress.append("]");
        return progress.toString();
	}

	private static int rng(int min, int max) {
	    return new Random().nextInt(max-min+1)+min;
    }

}
