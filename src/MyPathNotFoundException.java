import com.oocourse.spec3.exceptions.PathNotFoundException;

public class MyPathNotFoundException extends PathNotFoundException {
    private static final Counter counter = new Counter();
    private final int id1;
    private final int id2;

    public MyPathNotFoundException(int id1, int id2) {
        counter.addPnf(id1, id2);
        this.id1 = id1;
        this.id2 = id2;
    }

    public void print() {
        int i1;
        int i2;
        if (id1 < id2) {
            i1 = id1;
            i2 = id2;
        } else {
            i1 = id2;
            i2 = id1;
        }
        System.out.println("pnf-" + counter.getPnf() + ", " + i1 + "-" +
                counter.getPnfCnt(i1) + ", " + i2 + "-" + counter.getPnfCnt(i2));
    }
}
