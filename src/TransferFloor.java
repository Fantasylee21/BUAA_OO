public class TransferFloor {
    private int floor;
    private boolean isFull;
    private int currentFloorOfA;
    private int currentFloorOfB;

    public TransferFloor(int floor) {
        this.floor = floor;
        this.isFull = false;
    }

    public int getFloor() {
        return floor;
    }

    public int getCurrentFloorOfA() {
        return currentFloorOfA;
    }

    public int getCurrentFloorOfB() {
        return currentFloorOfB;
    }

    public void setCurrentFloorOfA(int currentFloorOfA) {
        this.currentFloorOfA = currentFloorOfA;
    }

    public void setCurrentFloorOfB(int currentFloorOfB) {
        this.currentFloorOfB = currentFloorOfB;
    }

    public synchronized boolean isFull() {
        notifyAll();
        return isFull;
    }

    public synchronized void setFull(boolean full) {
        isFull = full;
        notifyAll();
    }

    public synchronized void setFloor(int floor) {
        this.floor = floor;
    }

    public synchronized void try2Move() {
        while (isFull) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setFull(true);
        notifyAll();
    }

}