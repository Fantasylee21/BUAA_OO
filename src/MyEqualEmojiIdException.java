import com.oocourse.spec3.exceptions.EqualEmojiIdException;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyEqualEmojiIdException(int id) {
        counter.addEei(id);
        this.id = id;
    }

    public void print() {
        System.out.println("eei-" + counter.getEei() + ", "
                + id + "-" + counter.getEeiCnt(id));
    }
}
