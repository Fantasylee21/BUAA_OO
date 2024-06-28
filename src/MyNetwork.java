import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EqualTagIdException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.TagIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.NoticeMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MyNetwork implements Network {
    private final HashMap<Integer, MyPerson> persons;
    private final HashMap<Integer, MyMessage> messages;
    private final HashMap<Integer, Integer> emojiIdList;
    private final DisjointSetUnion dsu;
    private int tripleSum;

    public MyNetwork() {
        persons = new HashMap<>();
        dsu = new DisjointSetUnion();
        tripleSum = 0;
        messages = new HashMap<>();
        emojiIdList = new HashMap<>();
    }

    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    public Person getPerson(int id) {
        if (containsPerson(id)) {
            return persons.get(id);
        }
        return null;
    }

    public void addPerson(Person person) throws EqualPersonIdException {
        if (containsPerson(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        } else {
            MyPerson myPerson = (MyPerson) person;
            persons.put(person.getId(), myPerson);
            dsu.add(person.getId());
        }
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (persons.containsKey(id1) && persons.containsKey(id2)) {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            if (!person1.isLinked(person2)) {
                person1.addRelation(person2, value);
                person2.addRelation(person1, value);
                dsu.merge(id1, id2);
                for (int id : person1.getAcquaintance().keySet()) {
                    if (isLinked(id, id2) && id != id2 && id != id1 && id1 != id2) {
                        tripleSum++;
                    }
                }
            } else {
                throw new MyEqualRelationException(id1, id2);
            }
        } else {
            throw persons.containsKey(id1) ? new MyPersonIdNotFoundException(id2) :
                    new MyPersonIdNotFoundException(id1);
        }
    }

    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (persons.containsKey(id1) && persons.containsKey(id2)) {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            if (id1 != id2) {
                if (person1.isLinked(person2)) {
                    if (person1.queryValue(person2) + value > 0) {
                        int initialValue = person1.queryValue(person2);
                        person1.modifyRelation(person2, initialValue + value);
                        person2.modifyRelation(person1, initialValue + value);
                    } else {
                        person1.removePersonFromTag(person2);
                        person2.removePersonFromTag(person1);
                        person1.removeRelation(person2);
                        person2.removeRelation(person1);
                        dsu.setNeed2Rebuild(true);
                        for (int id : person1.getAcquaintance().keySet()) {
                            if (isLinked(id, id2) && id != id2 && id != id1) {
                                tripleSum--;
                            }
                        }
                    }
                } else {
                    throw new MyRelationNotFoundException(id1, id2);
                }
            } else {
                throw new MyEqualPersonIdException(id1);
            }
        } else {
            throw persons.containsKey(id1) ? new MyPersonIdNotFoundException(id2) :
                    new MyPersonIdNotFoundException(id1);
        }
    }

    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        if (persons.containsKey(id1) && persons.containsKey(id2)) {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            if (person1.isLinked(person2)) {
                return person1.queryValue(person2);
            } else {
                throw new MyRelationNotFoundException(id1, id2);
            }
        } else {
            throw persons.containsKey(id1) ? new MyPersonIdNotFoundException(id2) :
                    new MyPersonIdNotFoundException(id1);
        }
    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (persons.containsKey(id1) && persons.containsKey(id2)) {
            if (dsu.getNeed2Rebuild()) {
                HashMap<Integer, Boolean> visited = new HashMap<>();
                for (int id : persons.keySet()) {
                    visited.put(id, false);
                }
                return dfsPath(id1, id2, visited);
            } else {
                return dsu.isCircle(id1, id2);
            }
        } else {
            throw persons.containsKey(id1) ? new MyPersonIdNotFoundException(id2) :
                    new MyPersonIdNotFoundException(id1);
        }
    }

    public int queryBlockSum() {
        dsu.rebuild(persons);
        return dsu.getCnt();
    }

    public int queryTripleSum() {
        return tripleSum;
    }

    public boolean dfsPath(int id1, int id2, HashMap<Integer, Boolean> visited) {
        boolean flag = false;
        if (id1 == id2) {
            return true;
        }
        visited.put(id1, true);
        MyPerson person1 = persons.get(id1);
        for (int id : person1.getAcquaintance().keySet()) {
            if (!visited.get(id)) {
                visited.put(id, true);
                boolean temp = dfsPath(id, id2, visited);
                if (temp) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public boolean isLinked(int id1, int id2) {
        return persons.get(id1).isLinked(persons.get(id2));
    }

    public void addTag(int personId, Tag tag) throws
            PersonIdNotFoundException, EqualTagIdException {
        if (persons.containsKey(personId)) {
            if (!persons.get(personId).containsTag(tag.getId())) {
                persons.get(personId).addTag(tag);
            } else {
                throw new MyEqualTagIdException(tag.getId());
            }
        } else {
            throw new MyPersonIdNotFoundException(personId);
        }
    }

    public void addPersonToTag(int personId1, int personId2, int tagId) throws
        PersonIdNotFoundException, RelationNotFoundException,
        TagIdNotFoundException, EqualPersonIdException {
        if (persons.containsKey(personId1) && persons.containsKey(personId2)) {
            MyPerson person1 = (MyPerson) getPerson(personId1);
            MyPerson person2 = (MyPerson) getPerson(personId2);
            if (personId1 != personId2) {
                if (person1.isLinked(person2)) {
                    if (person2.containsTag(tagId)) {
                        if (!person2.getTag(tagId).hasPerson(person1) &&
                                person2.getTag(tagId).getSize() <= 1111) {
                            person2.getTag(tagId).addPerson(person1);
                        } else if (person2.getTag(tagId).hasPerson(person1)) {
                            throw new MyEqualPersonIdException(personId1);
                        }
                    } else {
                        throw new MyTagIdNotFoundException(tagId);
                    }
                } else {
                    throw new MyRelationNotFoundException(personId1, personId2);
                }
            } else {
                throw new MyEqualPersonIdException(personId1);
            }
        } else {
            throw persons.containsKey(personId1) ? new MyPersonIdNotFoundException(personId2) :
                    new MyPersonIdNotFoundException(personId1);
        }
    }

    public int queryTagValueSum(int personId, int tagId) throws
            PersonIdNotFoundException,TagIdNotFoundException {
        if (persons.containsKey(personId)) {
            if (persons.get(personId).containsTag(tagId)) {
                return persons.get(personId).getTag(tagId).getValueSum();
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        } else {
            throw new MyPersonIdNotFoundException(personId);
        }
    }

    public int queryTagAgeVar(int personId, int tagId) throws
            PersonIdNotFoundException,TagIdNotFoundException {
        if (persons.containsKey(personId)) {
            if (persons.get(personId).containsTag(tagId)) {
                return persons.get(personId).getTag(tagId).getAgeVar();
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        } else {
            throw new MyPersonIdNotFoundException(personId);
        }
    }

    public void delPersonFromTag(int personId1, int personId2,int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (persons.containsKey(personId1) && persons.containsKey(personId2)) {
            MyPerson person1 = (MyPerson) getPerson(personId1);
            MyPerson person2 = (MyPerson) getPerson(personId2);
            if (person2.containsTag(tagId)) {
                if (person2.getTag(tagId).hasPerson(person1)) {
                    person2.getTag(tagId).delPerson(person1);
                } else {
                    throw new MyPersonIdNotFoundException(personId1);
                }
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        } else {
            throw persons.containsKey(personId1) ? new MyPersonIdNotFoundException(personId2) :
                    new MyPersonIdNotFoundException(personId1);
        }
    }

    public void delTag(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (persons.containsKey(personId)) {
            if (persons.get(personId).containsTag(tagId)) {
                persons.get(personId).delTag(tagId);
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        } else {
            throw new MyPersonIdNotFoundException(personId);
        }
    }

    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (persons.containsKey(id)) {
            MyPerson person = persons.get(id);
            if (!person.getAcquaintance().isEmpty()) {
                return person.queryBestAcquaintance();
            } else {
                throw new MyAcquaintanceNotFoundException(id);
            }
        } else {
            throw new MyPersonIdNotFoundException(id);
        }
    }

    public int queryCoupleSum() {
        int sum = 0;
        HashSet<Integer> visited = new HashSet<>(persons.keySet());
        for (int id : persons.keySet()) {
            int ba = persons.get(id).queryBestAcquaintance();
            if (!persons.get(id).getAcquaintance().isEmpty() && persons.containsKey(ba)
                    && persons.get(ba).queryBestAcquaintance() == id && visited.contains(ba)) {
                sum++;
            }
            visited.remove(id);
        }
        return sum;
    }

    public int queryShortestPath(int id1, int id2) throws
            PersonIdNotFoundException, PathNotFoundException {
        if (persons.containsKey(id1) && persons.containsKey(id2)) {
            int res = persons.get(id1).bfs(id1, id2, persons);
            if (res == -1) {
                throw new MyPathNotFoundException(id1, id2);
            } else {
                return res;
            }
        } else {
            throw persons.containsKey(id1) ? new MyPersonIdNotFoundException(id2) :
                    new MyPersonIdNotFoundException(id1);
        }
    }

    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    public void addMessage(Message message) throws EqualMessageIdException,
            EmojiIdNotFoundException, EqualPersonIdException {
        if (!messages.containsKey(message.getId())) {
            if (message instanceof EmojiMessage &&
                    !emojiIdList.containsKey(((EmojiMessage) message).getEmojiId())) {
                throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
            }
            if (message.getType() == 0 && message.getPerson1().equals(message.getPerson2())) {
                throw new MyEqualPersonIdException(message.getPerson1().getId());
            }
            messages.put(message.getId(), (MyMessage) message);
        } else {
            throw  new MyEqualMessageIdException(message.getId());
        }
    }

    public Message getMessage(int id) {
        return containsMessage(id) ? messages.get(id) : null;
    }

    public void sendMessage(int id) throws RelationNotFoundException,
            MessageIdNotFoundException, TagIdNotFoundException {
        if (messages.containsKey(id)) {
            MyMessage ms = messages.get(id);
            if (ms.getType() == 0 && !isLinked(ms.getPerson1().getId(), ms.getPerson2().getId())) {
                throw new MyRelationNotFoundException(ms.getPerson1().getId(),
                        ms.getPerson2().getId());
            }
            if (ms.getType() == 1 &&
                    !getMessage(id).getPerson1().containsTag(getMessage(id).getTag().getId())) {
                throw new MyTagIdNotFoundException(ms.getTag().getId());
            }
            if (ms.getType() == 0) {
                ms.getPerson1().addSocialValue(ms.getSocialValue());
                ms.getPerson2().addSocialValue(ms.getSocialValue());
                ms.send1(ms, emojiIdList);
                ((MyPerson) ms.getPerson2()).addMessage(ms);
            } else if (ms.getType() == 1) {
                ms.getPerson1().addSocialValue(ms.getSocialValue());
                ((MyTag) ms.getTag()).addSocialValue(ms.getSocialValue());
                ms.send2(ms, emojiIdList);
            }
            messages.remove(id);
        } else {
            throw new MyMessageIdNotFoundException(id);
        }
    }

    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (persons.containsKey(id)) {
            return persons.get(id).getSocialValue();
        }
        throw new MyPersonIdNotFoundException(id);
    }

    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (persons.containsKey(id)) {
            return persons.get(id).getReceivedMessages();
        }
        throw new MyPersonIdNotFoundException(id);
    }

    public boolean containsEmojiId(int id) {
        return emojiIdList.containsKey(id);
    }

    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (!emojiIdList.containsKey(id)) {
            emojiIdList.put(id, 0);
        } else {
            throw new MyEqualEmojiIdException(id);
        }
    }

    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (persons.containsKey(id)) {
            return persons.get(id).getMoney();
        }
        throw new MyPersonIdNotFoundException(id);
    }

    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (emojiIdList.containsKey(id)) {
            return emojiIdList.get(id);
        }
        throw new MyEmojiIdNotFoundException(id);
    }

    public int deleteColdEmoji(int limit) {
        ArrayList<Integer> deleteList = new ArrayList<>();
        for (int id : emojiIdList.keySet()) {
            if (emojiIdList.get(id) < limit) {
                deleteList.add(id);
            }
        }
        for (int id : deleteList) {
            emojiIdList.remove(id);
        }
        Iterator<Map.Entry<Integer, MyMessage>> iterator = messages.entrySet().iterator();
        while (iterator.hasNext()) {
            MyMessage message = iterator.next().getValue();
            if (message instanceof EmojiMessage &&
                    deleteList.contains(((EmojiMessage) message).getEmojiId())) {
                iterator.remove();
            }
        }
        return emojiIdList.size();
    }

    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (persons.containsKey(personId)) {
            Iterator<Message> iterator = persons.get(personId).getMessages().iterator();
            while (iterator.hasNext()) {
                MyMessage message = (MyMessage) iterator.next();
                if (message instanceof NoticeMessage) {
                    iterator.remove();
                }
            }
        } else {
            throw new MyPersonIdNotFoundException(personId);
        }
    }

    public int[] getEmojiIdList() {
        int[] res = new int[emojiIdList.size()];
        int cnt = 0;
        for (int id : emojiIdList.keySet()) {
            res[cnt] = id;
            cnt++;
        }
        return res;
    }

    public int[] getEmojiHeatList() {
        int[] res = new int[emojiIdList.size()];
        int cnt = 0;
        for (int id : emojiIdList.keySet()) {
            res[cnt] = emojiIdList.get(id);
            cnt++;
        }
        return res;
    }

    public Message[] getMessages() {
        Message[] res = new Message[messages.size()];
        int cnt = 0;
        for (int id : messages.keySet()) {
            res[cnt] = messages.get(id);
            cnt++;
        }
        return res;
    }
}