import com.oocourse.spec3.main.Person;

import java.util.HashMap;

public class DisjointSetUnion {
    private HashMap<Integer, Integer> fa;
    private HashMap<Integer, Integer> rank;
    private boolean need2Rebuild;

    private int cnt;

    public DisjointSetUnion() {
        fa = new HashMap<>();
        rank = new HashMap<>();
        need2Rebuild = true;
        cnt = 0;
    }

    public int getCnt() {
        return cnt;
    }

    public boolean getNeed2Rebuild() {
        return need2Rebuild;
    }

    public void setNeed2Rebuild(boolean need2Rebuild) {
        this.need2Rebuild = need2Rebuild;
    }

    public void add(int id) {
        if (!fa.containsKey(id)) {
            fa.put(id, id);
            rank.put(id, 0);
            cnt++;
        }
    }

    public int find(int id) {
        int temp = id;
        while (temp != fa.get(temp)) {
            temp = fa.get(temp);
        }
        int now = id;
        while (now != temp) {
            int next = fa.get(now);
            fa.put(now, temp);
            now = next;
        }
        return temp;
    }

    public void merge(int id1, int id2) {
        int fa1 = find(id1);
        int fa2 = find(id2);
        if (fa1 == fa2) {
            return;
        }
        if (rank.get(fa1) > rank.get(fa2)) {
            fa.put(fa1, fa2);
        } else if (rank.get(fa1) < rank.get(fa2)) {
            fa.put(fa2, fa1);
        } else {
            fa.put(fa1, fa2);
            rank.put(fa2, rank.get(fa2) + 1);
        }
        cnt--;
    }

    public boolean isCircle(int id1, int id2) {
        return find(id1) == find(id2);
    }

    public void rebuild(HashMap<Integer, MyPerson> persons) {
        if (need2Rebuild) {
            fa.clear();
            rank.clear();
            cnt = 0;
            for (MyPerson person : persons.values()) {
                add(person.getId());
            }
            for (MyPerson person : persons.values()) {
                for (Person acquaintance : person.getAcquaintance().values()) {
                    merge(person.getId(), acquaintance.getId());
                }
            }
            need2Rebuild = false;
        }
    }
}
