import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.ResetRequest;
import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.TimableOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Elevator extends Thread {
    private final String id;
    private Strategy strategy;
    private RequestTable requestTable;
    private int currentFloor;
    private int currentNum;
    private Direction direction;
    private HashMap<Integer, ArrayList<PersonRequest>> targetMap; //key:targetFloor
    private final Constant constant;
    private ResetRequest resetRequest;
    private final RequestTable mainRequestTable;
    private final Dispatcher dispatcher;
    private boolean isDoubleCar;
    private final TransferFloor transferFloor;
    private final ArrayList<PersonRequest> wait2Receive;
    private Elevator upElevator;
    private Elevator downElevator;
    private boolean lee;

    public Elevator(String id, RequestTable requestTable, RequestTable mainRequestTable,
                    Dispatcher dispatcher, TransferFloor transferFloor) {
        this.id = id;
        this.requestTable = requestTable;
        this.currentFloor = 1;
        this.currentNum = 0;
        this.direction = Direction.UP;
        this.targetMap = new HashMap<>();
        this.strategy = new Strategy(requestTable);
        this.constant = new Constant();
        this.resetRequest = null;
        this.mainRequestTable = mainRequestTable;
        this.dispatcher = dispatcher;
        this.isDoubleCar = false;
        this.wait2Receive = new ArrayList<>();
        this.transferFloor = transferFloor;
        this.lee = false;
    }

    public RequestTable getRequestTable() {
        return requestTable;
    }

    public String getElevatorId() {
        return id;
    }

    public Elevator getUpElevator() {
        return upElevator;
    }

    public Elevator getDownElevator() {
        return downElevator;
    }

    public Constant getConstant() {
        return constant;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public ArrayList<PersonRequest> getWait2Receive() {
        return wait2Receive;
    }

    public boolean isLee() {
        return lee;
    }

    public boolean isFull() {
        return currentNum >= constant.getMaxNum();
    }

    public boolean isEmpty() {
        return currentNum == 0;
    }

    public boolean isDoubleCar() {
        return isDoubleCar;
    }

    public void setResetRequest(ResetRequest resetRequest) {
        this.resetRequest = resetRequest;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setIsDoubleCar(boolean isDoubleCar) {
        this.isDoubleCar = isDoubleCar;
    }

    public void addWait2Receive(PersonRequest personRequest) {
        wait2Receive.add(personRequest);
    }

    public void Reverse() {
        if (direction.equals(Direction.UP)) {
            direction = Direction.DOWN;
        } else if (direction.equals(Direction.DOWN)) {
            direction = Direction.UP;
        }
    }

    public void move() {
        rest(constant.getMoveTime());
        if (isDoubleCar) {
            if (currentFloor == (transferFloor.getFloor() - 1) && direction.equals(Direction.UP)) {
                transferFloor.try2Move();
                rest(1);
            }
            if (currentFloor == (transferFloor.getFloor() + 1)
                    && direction.equals(Direction.DOWN)) {
                transferFloor.try2Move();
                rest(1);
            }
        }
        if (direction.equals(Direction.UP)) {
            currentFloor++;
        } else if (direction.equals(Direction.DOWN)) {
            currentFloor--;
        }
        if (id.contains("A")) {
            transferFloor.setCurrentFloorOfA(currentFloor);
        } else if (id.contains("B")) {
            transferFloor.setCurrentFloorOfB(currentFloor);
        }
        TimableOutput.println(String.format("ARRIVE-%d-%s",  currentFloor, id));

        if (isDoubleCar) {
            if (transferFloor.getCurrentFloorOfA() != transferFloor.getFloor() &&
                    transferFloor.getCurrentFloorOfB() != transferFloor.getFloor()) {
                transferFloor.setFull(false);
            } else {
                transferFloor.setFull(true);
            }
        } else {
            transferFloor.setFull(false);
        }

    }

    public void openForOut() {
        TimableOutput.println(String.format("OPEN-%d-%s", currentFloor, id));
        rest(constant.getOpenTime());
        if (strategy.need2OpenForOut(currentFloor, targetMap, constant,currentNum)) {
            ArrayList<PersonRequest> personRequests = targetMap.get(currentFloor);
            if (personRequests != null) {
                for (PersonRequest personRequest : personRequests) {
                    TimableOutput.println(String.format("OUT-%d-%d-%s",
                            personRequest.getPersonId(), currentFloor, id));
                }
                currentNum -= personRequests.size();
                targetMap.remove(currentFloor);
            }
            if (constant.getMaxFloor() < 11 && currentFloor == constant.getMaxFloor()
                    && currentNum != 0) {
                getOffAll();
            }
            if (constant.getMinFloor() > 1 && currentFloor == constant.getMinFloor()
                    && currentNum != 0) {
                getOffAll();
            }
        }
    }

    public void inAndClose() {
        if (strategy.need2OpenForIn(currentFloor, direction, currentNum, constant, transferFloor)) {
            ArrayList<PersonRequest> personRequests =
                    requestTable.getPersonRequestMap().get(currentFloor);
            synchronized (requestTable) { //同步
                Iterator<PersonRequest> iterator = personRequests.iterator();
                while (iterator.hasNext()) {
                    if (isFull()) {
                        break;
                    }
                    PersonRequest personRequest = iterator.next();
                    if (getDirection(personRequest) == direction) {
                        if (targetMap.containsKey(personRequest.getToFloor())) {
                            targetMap.get(personRequest.getToFloor()).add(personRequest);
                        } else {
                            ArrayList<PersonRequest> newPersons = new ArrayList<>();
                            newPersons.add(personRequest);
                            targetMap.put(personRequest.getToFloor(), newPersons);
                        }
                        currentNum++;
                        TimableOutput.println(String.format("IN-%d-%d-%s",
                                personRequest.getPersonId(), currentFloor, id));
                        iterator.remove();
                        requestTable.subPersonRequestNum();
                    }
                }
            }
        }
        rest(constant.getCloseTime());
        TimableOutput.println(String.format("CLOSE-%d-%s", currentFloor, id));
    }

    public void open() {
        //实现先下后上
        openForOut();
        inAndClose();
    }

    public void revokeAllocate() {
        synchronized (mainRequestTable) {
            for (int i = constant.getMinFloor(); i <= constant.getMaxFloor(); i++) {
                if (requestTable.getPersonRequestMap().containsKey(i)) {
                    for (PersonRequest personRequest : requestTable.getPersonRequestMap().get(i)) {
                        mainRequestTable.addRequest(personRequest);
                    }
                }
            }
        }
        requestTable.getPersonRequestMap().clear();
        requestTable.setPersonRequestNum(0);
    }

    public void getOffAll() {
        for (ArrayList<PersonRequest> personRequests : targetMap.values()) {
            for (PersonRequest personRequest : personRequests) {
                TimableOutput.println(String.format("OUT-%d-%d-%s",
                        personRequest.getPersonId(), currentFloor, id));
                PersonRequest newPersonRequest = new PersonRequest(currentFloor,
                        personRequest.getToFloor(), personRequest.getPersonId());
                int numId = id.charAt(0) - '0';
                dispatcher.addReceive(numId, personRequest,
                        newPersonRequest, currentFloor, transferFloor);
            }
            currentNum -= personRequests.size();
        }
        targetMap.clear();
        rest(constant.getCloseTime());
    }

    public void flushWait2Receive() {
        for (PersonRequest personRequest : wait2Receive) {
            receive(personRequest);
        }
        wait2Receive.clear();
    }

    public void reset() {
        if (!isEmpty()) {
            openForOut();
            getOffAll();
            TimableOutput.println(String.format("CLOSE-%d-%s", currentFloor, id));
        }

        if (resetRequest instanceof NormalResetRequest) {
            NormalResetRequest normalResetRequest = (NormalResetRequest) resetRequest;
            constant.setMaxNum(normalResetRequest.getCapacity());
            constant.setMoveTime((int)(normalResetRequest.getSpeed() * 1000.0));
        } else if (resetRequest instanceof DoubleCarResetRequest) {
            DoubleCarResetRequest doubleCarResetRequest = (DoubleCarResetRequest) resetRequest;
            constant.setMaxNum(doubleCarResetRequest.getCapacity());
            constant.setMoveTime((int)(doubleCarResetRequest.getSpeed() * 1000.0));
            transferFloor.setFloor(doubleCarResetRequest.getTransferFloor());
            transferFloor.setFull(false);
            setIsDoubleCar(true);
        }

        TimableOutput.println(String.format("RESET_BEGIN-%s", getElevatorId()));
        revokeAllocate();
        rest(1200);
        TimableOutput.println(String.format("RESET_END-%s", getElevatorId()));
        requestTable.setReset(false);
        if (!isDoubleCar) {
            flushWait2Receive();
        }
    }

    public Direction getDirection(PersonRequest personRequest) {
        if (personRequest.getFromFloor() < personRequest.getToFloor()) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public void startDoubleCar() {
        TransferFloor  tf = new TransferFloor(transferFloor.getFloor());
        tf.setFull(false);
        tf.setCurrentFloorOfA(transferFloor.getCurrentFloorOfA() - 1);
        tf.setCurrentFloorOfB(transferFloor.getCurrentFloorOfB() + 1);
        RequestTable requestTable1 = new RequestTable();
        RequestTable requestTable2 = new RequestTable();
        upElevator = new Elevator(id + "-B", requestTable1,
                mainRequestTable, dispatcher, tf);
        downElevator = new Elevator(id + "-A", requestTable2,
                mainRequestTable, dispatcher, tf);
        DoubleCarResetRequest dcr = (DoubleCarResetRequest) resetRequest;
        upElevator.setCurrentFloor(tf.getFloor() + 1);
        upElevator.getConstant().setMinFloor(tf.getFloor());
        upElevator.getConstant().setMoveTime((int)(dcr.getSpeed() * 1000.0));
        upElevator.getConstant().setMaxNum(dcr.getCapacity());
        upElevator.setIsDoubleCar(true);
        downElevator.setCurrentFloor(tf.getFloor() - 1);
        downElevator.getConstant().setMaxFloor(tf.getFloor());
        downElevator.getConstant().setMoveTime((int)(dcr.getSpeed() * 1000.0));
        downElevator.getConstant().setMaxNum(dcr.getCapacity());
        downElevator.setIsDoubleCar(true);
        flushWait2Receive();
        upElevator.start();
        downElevator.start();
        lee = true;
        try {
            upElevator.join();
            downElevator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void rest(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void receive(PersonRequest personRequest) {
        if (isDoubleCar) {
            if (personRequest.getFromFloor() < transferFloor.getFloor()) {
                TimableOutput.println(String.format("RECEIVE-%d-%s",
                        personRequest.getPersonId(), id + "-A"));
                RequestTable requestTable = downElevator.getRequestTable();
                synchronized (requestTable) {
                    requestTable.addPersonRequest(personRequest, personRequest.getFromFloor());
                    requestTable.notifyAll();
                }
            } else if (personRequest.getFromFloor() > transferFloor.getFloor()) {
                TimableOutput.println(String.format("RECEIVE-%d-%s",
                        personRequest.getPersonId(), id + "-B"));
                RequestTable requestTable = upElevator.getRequestTable();
                synchronized (requestTable) {
                    requestTable.addPersonRequest(personRequest, personRequest.getFromFloor());
                    requestTable.notifyAll();
                }
            } else {
                if (getDirection(personRequest).equals(Direction.UP)) {
                    TimableOutput.println(String.format("RECEIVE-%d-%s",
                            personRequest.getPersonId(), id + "-B"));
                    RequestTable requestTable = upElevator.getRequestTable();
                    synchronized (requestTable) {
                        requestTable.addPersonRequest(personRequest, personRequest.getFromFloor());
                        requestTable.notifyAll();
                    }
                } else {
                    TimableOutput.println(String.format("RECEIVE-%d-%s",
                            personRequest.getPersonId(), id + "-A"));
                    RequestTable requestTable = downElevator.getRequestTable();
                    synchronized (requestTable) {
                        requestTable.addPersonRequest(personRequest, personRequest.getFromFloor());
                        requestTable.notifyAll();
                    }
                }
            }
        } else {
            TimableOutput.println(String.format("RECEIVE-%d-%s", personRequest.getPersonId(), id));
            requestTable.addPersonRequest(personRequest, personRequest.getFromFloor());
        }
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(currentFloor, currentNum, direction, targetMap,
                    constant, dispatcher, isDoubleCar, transferFloor);
            if (advice.equals(Advice.OVER)) {
                //System.out.println("Elevator " + id + " Over");
                break;
            } else if (advice.equals(Advice.MOVE)) {
                move();
            } else if (advice.equals(Advice.OPEN)) {
                open();
            } else if (advice.equals(Advice.REVERSE)) {
                Reverse();
            } else if (advice.equals(Advice.WAIT)) {
                requestTable.waitRequest();
            } else if (advice.equals(Advice.STOP)) {
                rest(100);
            } else if (advice.equals(Advice.RESET)) {
                reset();
                if (isDoubleCar) {
                    startDoubleCar();
                    break;
                }
            }
        }
    }

}
