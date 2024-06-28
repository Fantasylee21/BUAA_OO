import com.oocourse.spec3.exceptions.EqualTagIdException;

public class MyEqualTagIdException extends EqualTagIdException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyEqualTagIdException(int id) {
        counter.addEti(id);
        this.id = id;
    }

    public void print() {
        System.out.println("eti-" + counter.getEti() + ", "
                + id + "-" + counter.getEtiCnt(id));
    }
}
