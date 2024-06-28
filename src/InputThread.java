import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.Request;

import java.io.IOException;

public class InputThread extends Thread {
    private RequestTable requestTable;
    private Dispatcher dispatcher;

    public InputThread(RequestTable requestTable) {
        this.requestTable = requestTable;
        this.dispatcher = new Dispatcher(requestTable);
    }

    @Override
    public void run() {
        dispatcher.start();
        dispatcher.startElevator();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                requestTable.setInputEnd(true);
                //System.out.println("Input End");
                break;
            } else {
                requestTable.addRequest(request);
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
