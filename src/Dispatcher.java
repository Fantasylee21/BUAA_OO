import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.TimableOutput;
import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;

import java.util.ArrayList;

public class Dispatcher extends Thread {
    //调度器
    private ArrayList<Elevator> elevators;
    private final RequestTable requestTable;
    private ArrayList<RequestTable> requestTables;

    public Dispatcher(RequestTable requestTable) {
        this.requestTable = requestTable;
        this.elevators = new ArrayList<>();
        this.requestTables = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            RequestTable requestTable1 = new RequestTable();
            requestTables.add(requestTable1);
            Elevator elevator = new Elevator(String.valueOf(i), requestTable1,
                    requestTable, this, new TransferFloor(0));
            elevators.add(elevator);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (requestTable.isOver()) {
                //System.out.println("Dispatcher End");
                for (Elevator elevator : elevators) {
                    elevator.getRequestTable().setOver(true);
                    if (elevator.isDoubleCar()) {
                        if (!elevator.isLee()) {
                            elevator.rest(1200);
                        }
                        elevator.getDownElevator().getRequestTable().setOver(true);
                        elevator.getUpElevator().getRequestTable().setOver(true);
                    }
                }
                break;
            } else if (!requestTable.hasNext()) {
                requestTable.waitRequest();
            } else {
                allocate();
            }
            if (missionComplete()) {
                requestTable.setOver(true);
            }
        }
    }

    public void startElevator() {
        for (Elevator elevator : elevators) {
            elevator.start();
        }
    }

    public void allocate() {
        Request request = requestTable.getOneRequest();
        if (request instanceof PersonRequest) {
            PersonRequest personRequest = (PersonRequest) request;
            int elevatorId = (int)(Math.random() * 6) + 1;

            if (elevators.get(elevatorId - 1).getRequestTable().isReset()) {
                elevators.get(elevatorId - 1).addWait2Receive(personRequest);
            } else {
                elevators.get(elevatorId - 1).receive(personRequest);
            }

        } else if (request instanceof NormalResetRequest) {
            NormalResetRequest normalResetRequest = (NormalResetRequest) request;
            int elevatorId = normalResetRequest.getElevatorId();
            elevators.get(elevatorId - 1).setResetRequest(normalResetRequest);
            elevators.get(elevatorId - 1).getRequestTable().setReset(true);
        } else if (request instanceof DoubleCarResetRequest) {
            DoubleCarResetRequest doubleCarResetRequest = (DoubleCarResetRequest) request;
            int elevatorId = doubleCarResetRequest.getElevatorId();
            elevators.get(elevatorId - 1).setResetRequest(doubleCarResetRequest);
            elevators.get(elevatorId - 1).getRequestTable().setReset(true);
        }
    }

    public boolean missionComplete() {
        if (requestTable.isInputEnd() && !requestTable.hasNext()) {
            for (RequestTable requestTable : requestTables) {
                if (!requestTable.isEmpty() || requestTable.isReset()) {
                    return false;
                }
            }
            for (Elevator elevator : elevators) {
                if (elevator.isDoubleCar() && elevator.isLee()) {
                    if (elevator.getRequestTable().isReset() || elevator.getCurrentNum() != 0) {
                        return false;
                    }
                    if (!elevator.getUpElevator().getRequestTable().isEmpty() ||
                            !elevator.getDownElevator().getRequestTable().isEmpty()) {
                        return false;
                    }
                    if (elevator.getUpElevator().getCurrentNum() != 0 ||
                            elevator.getDownElevator().getCurrentNum() != 0) {
                        return false;
                    }
                } else {
                    if (!elevator.getRequestTable().isEmpty() ||
                            elevator.getRequestTable().isReset() ||
                            elevator.getCurrentNum() != 0) {
                        return false;
                    }
                }
                if (!elevator.getWait2Receive().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void addReceive(int id, PersonRequest personRequest,PersonRequest newPersonRequest,
                           int currentFloor, TransferFloor transferFloor) {
        boolean isDoubleCar = elevators.get(id - 1).isDoubleCar();
        Elevator upElevator = elevators.get(id - 1).getUpElevator();
        Elevator downElevator = elevators.get(id - 1).getDownElevator();
        RequestTable requestTable = elevators.get(id - 1).getRequestTable();
        if (isDoubleCar) {
            if (personRequest.getFromFloor() < transferFloor.getFloor()) {
                RequestTable upRequestTable = upElevator.getRequestTable();
                synchronized (upRequestTable) {
                    upRequestTable.addPersonRequest(newPersonRequest, currentFloor);
                    upRequestTable.notifyAll();
                }
                TimableOutput.println(String.format("RECEIVE-%d-%s",
                        personRequest.getPersonId(), id + "-B"));
            } else if (personRequest.getFromFloor() > transferFloor.getFloor()) {
                RequestTable downRequestTable = downElevator.getRequestTable();
                synchronized (downRequestTable) {
                    downRequestTable.addPersonRequest(newPersonRequest, currentFloor);
                    downRequestTable.notifyAll();
                }
                TimableOutput.println(String.format("RECEIVE-%d-%s",
                        personRequest.getPersonId(), id + "-A"));
            } else {
                if (personRequest.getFromFloor() < personRequest.getToFloor()) {
                    TimableOutput.println(String.format("RECEIVE-%d-%s",
                            personRequest.getPersonId(), id + "-B"));
                    RequestTable upRequestTable = upElevator.getRequestTable();
                    synchronized (upRequestTable) {
                        upRequestTable.addPersonRequest(newPersonRequest, currentFloor);
                        upRequestTable.notifyAll();
                    }
                } else {
                    TimableOutput.println(String.format("RECEIVE-%d-%s",
                            personRequest.getPersonId(), id + "-A"));
                    RequestTable downRequestTable = downElevator.getRequestTable();
                    synchronized (downRequestTable) {
                        downRequestTable.addPersonRequest(newPersonRequest, currentFloor);
                        downRequestTable.notifyAll();
                    }
                }
            }
        } else {
            requestTable.addPersonRequest(newPersonRequest, currentFloor);
        }
    }
}

