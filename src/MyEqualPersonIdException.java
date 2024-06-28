import com.oocourse.spec3.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyEqualPersonIdException(int id) {
        counter.addEpi(id);
        this.id = id;
    }

    public void print() {
        System.out.println("epi-" + counter.getEpi() + ", "
                + id + "-" + counter.getEpiCnt(id));
    }
}
