import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {
    private int id;
    private HashMap<Integer, MyPerson> persons;
    private long ageSum;

    public MyTag(int id) {
        this.id = id;
        this.persons = new HashMap<>();
        this.ageSum = 0;
    }

    public int getId() {
        return id;
    }

    public HashMap<Integer, MyPerson> getPersons() {
        return persons;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            return (((Tag) obj).getId() == id);
        } else {
            return false;
        }
    }

    public void addPerson(Person person) {
        MyPerson myPerson = (MyPerson) person;
        this.ageSum += myPerson.getAge();
        this.persons.put(person.getId(), myPerson);
    }

    public boolean hasPerson(Person person) {
        return persons.containsKey(person.getId());
    }

    public int getValueSum() {
        int sum = 0;
        for (int id1 : persons.keySet()) {
            MyPerson person1 = persons.get(id1);
            for (int id2 : person1.getAcquaintance().keySet()) {
                if (persons.containsKey(id2)) {
                    sum += person1.queryValue(persons.get(id2));
                }
            }
        }
        return sum;
    }

    public int getAgeMean() {
        if (persons.isEmpty()) {
            return 0;
        }
        return (int) (ageSum / persons.size());
    }

    public int getAgeVar() {
        if (persons.isEmpty() || persons.size() == 1) {
            return 0;
        }
        int average = getAgeMean();
        long temp = 0;
        for (int id : persons.keySet()) {
            temp += ((long) (persons.get(id).getAge() - average)
                    * (long) (persons.get(id).getAge() - average));
        }
        return (int) (temp / persons.size());
    }

    public void delPerson(Person person) {
        MyPerson myPerson = (MyPerson) person;
        this.ageSum -= myPerson.getAge();
        this.persons.remove(myPerson.getId());
    }

    public int getSize() {
        return persons.size();
    }

    public void addSocialValue(int value) {
        for (int id : persons.keySet()) {
            persons.get(id).addSocialValue(value);
        }
    }

}
