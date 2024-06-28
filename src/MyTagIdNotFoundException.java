import com.oocourse.spec3.exceptions.TagIdNotFoundException;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyTagIdNotFoundException(int id) {
        counter.addTinf(id);
        this.id = id;
    }

    public void print() {
        System.out.println("tinf-" + counter.getTinf() + ", "
                + id + "-" + counter.getTinfCnt(id));
    }
}
