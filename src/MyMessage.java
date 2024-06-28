import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Tag;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.EmojiMessage;

import java.util.HashMap;

public class MyMessage implements Message {
    private int id;
    private int socialValue;
    private int type;
    private Person person1;
    private Person person2;
    private Tag tag;

    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Person messagePerson2) {
        id = messageId;
        socialValue = messageSocialValue;
        person1 = messagePerson1;
        person2 = messagePerson2;
        type = 0;
        tag = null;
    }

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1, Tag messageTag) {
        id = messageId;
        socialValue = messageSocialValue;
        person1 = messagePerson1;
        person2 = null;
        type = 1;
        tag = messageTag;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getSocialValue() {
        return socialValue;
    }

    public Person getPerson1() {
        return person1;
    }

    public Person getPerson2() {
        return person2;
    }

    public Tag getTag() {
        return this.tag;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            return ((Message) obj).getId() == id;
        } else {
            return false;
        }
    }

    public void send1(MyMessage ms, HashMap<Integer, Integer> emojiIdList) {
        if (ms instanceof RedEnvelopeMessage) {
            ms.getPerson1().addMoney(-(((RedEnvelopeMessage) ms).getMoney()));
            ms.getPerson2().addMoney(((RedEnvelopeMessage) ms).getMoney());
        } else if (ms instanceof EmojiMessage) {
            emojiIdList.put(((EmojiMessage) ms).getEmojiId(),
                    emojiIdList.get(((EmojiMessage) ms).getEmojiId()) + 1);
        }
    }

    public void send2(MyMessage ms, HashMap<Integer, Integer> emojiIdList) {
        if (ms instanceof RedEnvelopeMessage) {
            int size = ((MyTag) ms.getTag()).getPersons().size();
            if (size == 0) {
                return;
            }
            int money = ((RedEnvelopeMessage) ms).getMoney() / size;
            ms.getPerson1().addMoney(-money * size);
            for (MyPerson p : ((MyTag) ms.getTag()).getPersons().values()) {
                p.addMoney(money);
            }
        } else if (ms instanceof EmojiMessage) {

            emojiIdList.put(((EmojiMessage) ms).getEmojiId(),
                    emojiIdList.get(((EmojiMessage) ms).getEmojiId()) + 1);
        }
    }
}
