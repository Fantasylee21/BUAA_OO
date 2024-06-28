import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestTable {
    private int personRequestNum;
    private boolean over;
    private final HashMap<Integer, ArrayList<PersonRequest>> requestMap; //key:fromFloor
    private final ArrayList<Request> requests;
    private int cur;
    private boolean reset;
    private boolean inputEnd;

    public RequestTable() {
        this.personRequestNum = 0;
        this.over = false;
        requestMap = new HashMap<>();
        requests = new ArrayList<>();
        cur = 0;
        reset = false;
        inputEnd = false;
    }

    public synchronized void setOver(boolean over) {
        this.over = over;
        notifyAll();
    }

    public synchronized void setReset(boolean reset) {
        this.reset = reset;
        notifyAll();
    }

    public synchronized void setInputEnd(boolean inputEnd) {
        this.inputEnd = inputEnd;
        notifyAll();
    }

    public synchronized void setPersonRequestNum(int num) {
        this.personRequestNum = num;
        notifyAll();
    }

    public synchronized boolean isOver() {
        notifyAll();
        return over;
    }

    public synchronized boolean isInputEnd() {
        notifyAll();
        return inputEnd;
    }

    public synchronized boolean isReset() {
        notifyAll();
        return reset;
    }

    public synchronized void subPersonRequestNum() {
        notifyAll();
        personRequestNum--;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return personRequestNum == 0;
    }

    public synchronized HashMap<Integer, ArrayList<PersonRequest>> getPersonRequestMap() {
        return requestMap;
    }

    public synchronized void addRequest(Request request) {
        requests.add(request);
        notifyAll();
    }

    public synchronized void addPersonRequest(PersonRequest personRequest, int fromFloor) {
        if (!getPersonRequestMap().containsKey(fromFloor)) {
            getPersonRequestMap().put(fromFloor, new ArrayList<>());
        }
        getPersonRequestMap().get(fromFloor).add(personRequest);
        personRequestNum++;
        notifyAll();
    }

    public synchronized void waitRequest() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized Request getOneRequest() {
        Request request = requests.get(cur);
        cur++;
        return request;
    }

    public synchronized boolean hasNext() {
        notifyAll();
        return cur < requests.size();
    }

}
