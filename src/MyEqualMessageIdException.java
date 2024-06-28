import com.oocourse.spec3.exceptions.EqualMessageIdException;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyEqualMessageIdException(int id) {
        counter.addEmi(id);
        this.id = id;
    }

    public void print() {
        System.out.println("emi-" + counter.getEmi() + ", "
                + id + "-" + counter.getEmiCnt(id));
    }
}
