import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private HashMap<Integer, MyPerson> acquaintance;
    private HashMap<Integer, Integer> value;
    private HashMap<Integer, MyTag> tag;
    private int bestAcquaintanceId;
    private boolean needUpdate;
    private int money;
    private int socialValue;
    private LinkedList<Message> messages;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new HashMap<>();
        this.value = new HashMap<>();
        this.tag = new HashMap<>();
        this.bestAcquaintanceId = -0x7fffffff;
        this.needUpdate = true;
        this.money = 0;
        this.socialValue = 0;
        this.messages = new LinkedList<>();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public HashMap<Integer, MyPerson> getAcquaintance() {
        return this.acquaintance;
    }

    public HashMap<Integer, Integer> getValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).getId() == id;
        } else {
            return false;
        }
    }

    public boolean strictEquals(Person person) {
        MyPerson myPerson = (MyPerson) person;
        if (person.getId() != id || person.getAge() != age || !person.getName().equals(name)) {
            return false;
        }
        for (int key : acquaintance.keySet()) {
            if (!myPerson.getAcquaintance().containsKey(key) ||
                    !myPerson.getValue().containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLinked(Person person) {
        return this.acquaintance.containsKey(person.getId()) || person.getId() == id;
    }

    public int queryValue(Person person) {
        if (value.containsKey(person.getId())) {
            return this.value.get(person.getId());
        }
        return 0;
    }

    public void addRelation(MyPerson person, int value) {
        if (!needUpdate) {
            int va = this.value.get(bestAcquaintanceId);
            if (va < value || (va == value && bestAcquaintanceId > person.getId())) {
                bestAcquaintanceId = person.getId();
            }
        }
        this.acquaintance.put(person.getId(), person);
        this.value.put(person.getId(), value);
    }

    public void modifyRelation(Person person, int value) {
        if (this.bestAcquaintanceId == person.getId() ||
                value > this.value.get(person.getId())) {
            this.needUpdate = true;
        }
        this.value.put(person.getId(), value);
    }

    public void removeRelation(Person person) {
        if (this.bestAcquaintanceId == person.getId()) {
            this.needUpdate = true;
        }
        this.acquaintance.remove(person.getId());
        this.value.remove(person.getId());
    }

    public boolean containsTag(int id) {
        return tag.containsKey(id);
    }

    public Tag getTag(int id) {
        if (containsTag(id)) {
            return tag.get(id);
        } else {
            return null;
        }
    }

    public void addTag(Tag tag) {
        this.tag.put(tag.getId(), (MyTag) tag);
    }

    public void delTag(int id) {
        this.tag.remove(id);
    }

    public void removePersonFromTag(Person person) {
        for (int key : tag.keySet()) {
            if (tag.get(key).hasPerson(person)) {
                tag.get(key).delPerson(person);
            }
        }
    }

    public int queryBestAcquaintance() {
        if (!needUpdate || value.isEmpty()) {
            return bestAcquaintanceId;
        }
        int max = -0xfffffff;
        int ba = this.bestAcquaintanceId;
        for (int key : value.keySet()) {
            if (value.get(key) > max || (value.get(key) == max && key < ba)) {
                max = value.get(key);
                ba = key;
            }
        }
        this.bestAcquaintanceId = ba;
        needUpdate = false;
        return ba;
    }

    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    public int getSocialValue() {
        return this.socialValue;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Message> getReceivedMessages() {
        List<Message> receivedMessages = new ArrayList<>();
        for (int i = 0;i < messages.size() && i < 5; i++) {
            receivedMessages.add(messages.get(i));
        }
        return receivedMessages;
    }

    public void addMoney(int num) {
        this.money += num;
    }

    public int getMoney() {
        return this.money;
    }

    public void addMessage(Message message) {
        this.messages.addFirst(message);
    }

    public int bfs(int id1, int id2, HashMap<Integer, MyPerson> persons) {
        if (id1 == id2) {
            return 0;
        }
        HashMap<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(id1);
        distance.put(id1, -1);
        while (!queue.isEmpty()) {
            int id = queue.poll();
            MyPerson person = persons.get(id);
            for (int key : person.getAcquaintance().keySet()) {
                if (!distance.containsKey(key)) {
                    distance.put(key, distance.get(id) + 1);
                    queue.add(key);
                }
            }
        }
        return distance.getOrDefault(id2, -1);
    }
}
