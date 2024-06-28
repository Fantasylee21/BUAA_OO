public class Constant {
    private int maxNum = 6;
    private int moveTime = 400;
    private final int openTime = 200;
    private final int closeTime = 200;
    private int maxFloor = 11;
    private int minFloor = 1;

    public int getMaxNum() {
        return maxNum;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public int getOpenTime() {
        return openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }
}
