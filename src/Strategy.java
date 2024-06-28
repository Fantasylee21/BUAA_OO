import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class Strategy {
    private final RequestTable requestTable;

    public Strategy(RequestTable requestTable) {
        this.requestTable = requestTable;
    }

    public Direction getDirection(PersonRequest personRequest) {
        if (personRequest.getFromFloor() < personRequest.getToFloor()) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public boolean need2OpenForOut(int currentFloor,
                                   HashMap<Integer, ArrayList<PersonRequest>> targetMap,
                                   Constant constant, int currentNum) {
        if (constant.getMaxFloor() < 11) { //下方电梯
            if (currentFloor == constant.getMaxFloor() && currentNum != 0) {
                for (int i = currentFloor; i <= 11; i++) {
                    if (targetMap.containsKey(i)) {
                        return true;
                    }
                }
            }
        } else if (constant.getMinFloor() > 1) { //上方电梯
            if (currentFloor == constant.getMinFloor() && currentNum != 0) {
                for (int i = currentFloor; i >= 1; i--) {
                    if (targetMap.containsKey(i)) {
                        return true;
                    }
                }
            }
        }
        return targetMap.containsKey(currentFloor);
    }

    public boolean need2OpenForIn(int currentFloor, Direction direction, int currentNum,
                                  Constant constant, TransferFloor transferFloor) {
        if (requestTable.getPersonRequestMap().containsKey(currentFloor) &&
                currentNum < constant.getMaxNum()) {
            //ConcurrentModificationException并发修改异常
            for (PersonRequest personRequest :
                    requestTable.getPersonRequestMap().get(currentFloor)) {
                if (getDirection(personRequest) == direction) {
                    if (transferFloor.getFloor() == currentFloor) {
                        //上去的人不进入下方电梯，下来的人不进入下方电梯
                        if (getDirection(personRequest) == Direction.UP &&
                                constant.getMaxFloor() == currentFloor) {
                            return false;
                        } else if (getDirection(personRequest) == Direction.DOWN &&
                                constant.getMinFloor() == currentFloor) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean need2Reverse(int currentFloor, Direction direction,
                                HashMap<Integer, ArrayList<PersonRequest>> targetMap,
                                Constant constant) {
        if (currentFloor == constant.getMinFloor() && direction.equals(Direction.DOWN)) {
            return true;
        }
        if (currentFloor == constant.getMaxFloor() && direction.equals(Direction.UP)) {
            return true;
        }
        if (direction.equals(Direction.UP)) {
            for (int i = currentFloor + 1; i <= constant.getMaxFloor(); i++) {
                if (requestTable.getPersonRequestMap().containsKey(i) ||
                        targetMap.containsKey(i)) {
                    return false;
                }
            }
            for (int i = currentFloor; i >= constant.getMinFloor(); i--) {
                if (requestTable.getPersonRequestMap().containsKey(i)) {
                    return true;
                }
            }
        }
        if (direction.equals(Direction.DOWN)) {
            for (int i = currentFloor - 1; i >= constant.getMinFloor(); i--) {
                if (requestTable.getPersonRequestMap().containsKey(i) ||
                        targetMap.containsKey(i)) {
                    return false;
                }
            }
            for (int i = currentFloor; i <= constant.getMaxFloor(); i++) {
                if (requestTable.getPersonRequestMap().containsKey(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Advice getAdvice(int currentFloor, int currentNum, Direction direction,
                            HashMap<Integer, ArrayList<PersonRequest>> targetMap,
                            Constant constant, Dispatcher dispatcher,
                            boolean isDoubleCar, TransferFloor transferFloor) {
        if (requestTable.isReset()) {
            return Advice.RESET;
        }
        if (need2OpenForOut(currentFloor, targetMap, constant, currentNum)
                || need2OpenForIn(currentFloor, direction, currentNum, constant, transferFloor)) {
            return Advice.OPEN;
        }
        if (isDoubleCar) {
            if (transferFloor.isFull() && currentNum != 0) {
                if (direction.equals(Direction.UP)
                        && currentFloor == transferFloor.getFloor() - 1) {
                    return Advice.STOP;
                }
                if (direction.equals(Direction.DOWN)
                        && currentFloor == transferFloor.getFloor() + 1) {
                    return Advice.STOP;
                }
            }
        }
        if (currentNum != 0) {
            return Advice.MOVE;
        } else {
            if (isDoubleCar && currentFloor == transferFloor.getFloor()) {
                if (direction.equals(Direction.UP) && constant.getMaxFloor() == currentFloor) {
                    return Advice.REVERSE;
                }
                if (direction.equals(Direction.DOWN) && constant.getMinFloor() == currentFloor) {
                    return Advice.REVERSE;
                }
                return Advice.MOVE;
            }
            if (requestTable.isEmpty()) {
                if (dispatcher.missionComplete() && requestTable.isOver()) {
                    return Advice.OVER;
                } else {
                    return Advice.WAIT;
                }
            } else {
                if (need2Reverse(currentFloor, direction, targetMap, constant)) {
                    return Advice.REVERSE;
                } else {
                    return Advice.MOVE;
                }
            }
        }
    }

}
