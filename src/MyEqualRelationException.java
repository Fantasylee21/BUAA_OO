import com.oocourse.spec3.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private static final Counter counter = new Counter();
    private final int id1;
    private final int id2;

    public MyEqualRelationException(int id1, int id2) {
        counter.addEr(id1, id2);
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
        System.out.println("er-" + counter.getEr() + ", " + i1 + "-" +
                counter.getErCnt(i1) + ", " + i2 + "-" + counter.getErCnt(i2));
    }
}
