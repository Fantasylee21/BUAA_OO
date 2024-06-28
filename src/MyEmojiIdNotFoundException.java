import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private static final Counter counter = new Counter();
    private final int id;

    public MyEmojiIdNotFoundException(int id) {
        counter.addEinf(id);
        this.id = id;
    }

    public void print() {
        System.out.println("einf-" + counter.getEinf() + ", "
                + id + "-" + counter.getEinfCnt(id));
    }
}
