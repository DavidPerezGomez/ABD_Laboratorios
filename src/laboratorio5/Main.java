package laboratorio5;

public class Main {

    static final int mode = Data.LOCKING;

    public static void main(String[] args) {
        boolean locking = false;
        Data mainData = new Data(locking);
    }

}
