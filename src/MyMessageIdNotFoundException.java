import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyMessageIdNotFoundException(int id) {
        counter.addMinf(id);
        this.id = id;
    }

    public void print() {
        System.out.println("minf-" + counter.getMinf() + ", "
                + id + "-" + counter.getMinfCnt(id));
    }
}
