import com.oocourse.elevator3.TimableOutput;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        RequestTable requestTable = new RequestTable();
        InputThread inputThread = new InputThread(requestTable);
        inputThread.start();
    }
}