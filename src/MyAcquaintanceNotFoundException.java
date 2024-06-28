import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyAcquaintanceNotFoundException(int id) {
        counter.addAnf(id);
        this.id = id;
    }

    public void print() {
        System.out.println("anf-" + counter.getAnf() + ", "
                + id + "-" + counter.getAnfCnt(id));
    }
}
