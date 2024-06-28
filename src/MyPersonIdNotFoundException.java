import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyPersonIdNotFoundException(int id) {
        counter.addPinf(id);
        this.id = id;
    }

    public void print() {
        System.out.println("pinf-" + counter.getPinf() +
                ", " + id + "-" + counter.getPinfCnt(id));
    }
}
