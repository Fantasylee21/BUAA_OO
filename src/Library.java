import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryOpenCmd;
import com.oocourse.library3.LibraryCloseCmd;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibraryScanner;
import com.oocourse.library3.annotation.SendMessage;
import com.oocourse.library3.annotation.Trigger;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import static com.oocourse.library3.LibrarySystem.PRINTER;

public class Library {
    private final LibraryScanner scanner;
    private final Bookshelf bs;
    private final BorrowAndReturnOffice bro;
    private final AppointmentOffice ao;
    private final BookDriftCorner bdc;
    private final HashMap<String, User> users;
    private final ArrayList<LibraryReqCmd> orderRequests;
    private final List<LibraryMoveInfo> info;
    private LocalDate date;
    private int restDayCount;
    private final HashMap<LibraryBookId, Integer> donatedBooks;
    private final HashMap<LibraryBookId, String> donatedBookUsers;
    private final HashMap<LibraryBookId, Integer> allBooks;

    public Library(LibraryScanner scanner) {
        this.scanner = scanner;
        this.bs = new Bookshelf();
        this.bro = new BorrowAndReturnOffice();
        this.ao = new AppointmentOffice();
        this.bdc = new BookDriftCorner();
        this.users = new HashMap<>();
        this.orderRequests = new ArrayList<>();
        this.info = new LinkedList<>();
        this.restDayCount = 0;
        this.donatedBooks = new HashMap<>();
        this.donatedBookUsers = new HashMap<>();
        this.allBooks = new HashMap<>();
    }

    @Trigger(from = "InitState", to = { "Bookshelf"})
    public void init() {
        Map<LibraryBookId, Integer> map = scanner.getInventory();
        bs.init(map);
        allBooks.putAll(map);
    }

    @Trigger(from = "BorrowAndReturnOffice", to = { "Bookshelf"})
    public void arrangeReturnedBooks() {
        while (!bro.isEmpty()) {
            LibraryBookId bookId = bro.getBook();
            if (bookId.isTypeAU() || bookId.isTypeBU() || bookId.isTypeCU()) {
                if (donatedBooks.containsKey(bookId)) {
                    info.add(new LibraryMoveInfo(bookId, "bro", "bdc"));
                    bdc.addBook(bookId);
                } else {
                    users.get(donatedBookUsers.get(bookId)).updateCredits(2);
                    info.add(new LibraryMoveInfo(bookId, "bro", "bs"));
                    if (bookId.isTypeBU()) {
                        bookId = new LibraryBookId(LibraryBookId.Type.B, bookId.getUid());
                    } else if (bookId.isTypeCU()) {
                        bookId = new LibraryBookId(LibraryBookId.Type.C, bookId.getUid());
                    }
                    bs.addBook(bookId);
                }
            } else {
                info.add(new LibraryMoveInfo(bookId, "bro", "bs"));
                bs.addBook(bookId);
            }
        }
    }

    @Trigger(from = "Bookshelf", to = { "AppointmentOffice"})
    public void arrangeAppointedBooks() {
        Iterator<LibraryReqCmd> iterator = orderRequests.iterator();
        while (iterator.hasNext()) {
            LibraryReqCmd request = iterator.next();
            LibraryBookId bookId = request.getBookId();
            String userId = request.getStudentId();
            if (bs.hasBook(bookId)) {
                if (ao.getAppointedBooks().containsKey(bookId)) {
                    if (ao.getAppointedBooks().get(bookId).containsKey(userId)) {
                        continue;
                    }
                }
                bs.removeBook(bookId);
                ao.addBook(bookId, userId);
                info.add(new LibraryMoveInfo(bookId, "bs", "ao", userId));
                iterator.remove();
            }
        }
    }

    @SendMessage(from = "obj: AppointmentOffice", to = "obj: Bookshelf")
    @Trigger(from = "AppointmentOffice", to = { "Bookshelf"})
    public void arrangeOverdueBooks(int restDayCount) {
        Iterator<Map.Entry<LibraryBookId, HashMap<String, Integer>>> iterator
                = ao.getAppointedBooks().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LibraryBookId, HashMap<String, Integer>> entry = iterator.next();
            LibraryBookId bookId = entry.getKey();
            HashMap<String, Integer> map = entry.getValue();
            Iterator<Map.Entry<String, Integer>> iterator1 = map.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<String, Integer> entry1 = iterator1.next();
                String userId = entry1.getKey();
                int value = entry1.getValue();
                ao.getAppointedBooks().get(bookId).put(userId, value - restDayCount);
                if (value - restDayCount < 0) {
                    users.get(userId).updateCredits(-3);
                    bs.addBook(bookId);
                    iterator1.remove();
                    info.add(new LibraryMoveInfo(bookId, "ao", "bs"));
                }
            }
        }
    }

    public void arrangeBooks() {
        arrangeReturnedBooks();
        arrangeAppointedBooks();
        PRINTER.move(date, info);
        info.clear();
    }

    @Trigger(from = "User", to = { "BorrowAndReturnOffice"})
    public void returnBook(LibraryReqCmd request) {
        if (users.get(request.getStudentId()).isOverdue(request.getBookId(), date)) {
            users.get(request.getStudentId()).subOverdueCount();
            PRINTER.accept(request, "overdue");
        } else {
            users.get(request.getStudentId()).updateCredits(1);
            PRINTER.accept(request, "not overdue");
        }
        users.get(request.getStudentId()).removeBook(request.getBookId());
        bro.addBook(request.getBookId());
        if (donatedBooks.containsKey(request.getBookId())) {
            donatedBooks.put(request.getBookId(), donatedBooks.get(request.getBookId()) - 1);
            if (donatedBooks.get(request.getBookId()) == 0) {
                donatedBooks.remove(request.getBookId());
            }
        }
    }

    @Trigger(from = "BookDriftCorner", to = { "User"})
    @Trigger(from = "Bookshelf", to = { "User"})
    @Trigger(from = "BookDriftCorner", to = { "BorrowAndReturnOffice"})
    @Trigger(from = "Bookshelf", to = { "BorrowAndReturnOffice"})
    public void borrowBook(LibraryReqCmd request) {
        if (donatedBooks.containsKey(request.getBookId())) {
            if (bdc.hasBook(request.getBookId()) && !request.getBookId().isTypeAU()) {
                if ((request.getBookId().isTypeCU() && users.get(request.getStudentId()).
                        hasSameTypeCBook(request.getBookId())) ||
                        users.get(request.getStudentId()).notEnoughCredits()) {
                    bdc.removeBook(request.getBookId());
                    bro.addBook(request.getBookId());
                    PRINTER.reject(request);
                    return;
                }
                if (request.getBookId().isTypeBU() && users.get(request.getStudentId()).
                        hasTypeBU()) {
                    bdc.removeBook(request.getBookId());
                    bro.addBook(request.getBookId());
                    PRINTER.reject(request);
                    return;
                }
                users.get(request.getStudentId()).addBook(request.getBookId(), date);
                bdc.removeBook(request.getBookId());
                PRINTER.accept(request);
            } else {
                PRINTER.reject(request);
            }
        } else {
            if ((!bs.hasBook(request.getBookId())) || request.getBookId().isTypeA()) {
                PRINTER.reject(request);
                return;
            }
            if ((request.getBookId().isTypeB() && users.get(request.getStudentId()).hasTypeB())
                    || users.get(request.getStudentId()).notEnoughCredits()) {
                bs.removeBook(request.getBookId());
                bro.addBook(request.getBookId());
                PRINTER.reject(request);
                return;
            }
            if (request.getBookId().isTypeC()) {
                if (users.get(request.getStudentId()).hasSameTypeCBook(request.getBookId())) {
                    bs.removeBook(request.getBookId());
                    bro.addBook(request.getBookId());
                    PRINTER.reject(request);
                    return;
                }
            }
            users.get(request.getStudentId()).addBook(request.getBookId(), date);
            bs.removeBook(request.getBookId());
            PRINTER.accept(request);
        }
    }

    public void queryBook(LibraryReqCmd request) {
        if (request.getBookId().isTypeAU() || request.getBookId().isTypeBU() ||
                request.getBookId().isTypeCU()) {
            PRINTER.info(date, request.getBookId(), bdc.getBookCount(request.getBookId()));
        } else {
            PRINTER.info(date, request.getBookId(), bs.getBookCount(request.getBookId()));
        }
    }

    public void orderBook(LibraryReqCmd request) {
        if (request.getBookId().isTypeAU() || request.getBookId().isTypeBU() ||
                request.getBookId().isTypeCU()) {
            PRINTER.reject(request);
            return;
        }
        if (users.get(request.getStudentId()).notEnoughCredits()) {
            PRINTER.reject(request);
            return;
        }
        if (request.getBookId().isTypeB()) {
            if (ao.hasTypeB(request.getStudentId())) {
                PRINTER.reject(request);
                return;
            }
            for (LibraryReqCmd req : orderRequests) {
                if (req.getBookId().isTypeB() &&
                        req.getStudentId().equals(request.getStudentId())) {
                    PRINTER.reject(request);
                    return;
                }
            }
        } else if (request.getBookId().isTypeC()) {
            if (ao.hasBook(request.getBookId(), request.getStudentId())) {
                PRINTER.reject(request);
                return;
            }
            for (LibraryReqCmd req : orderRequests) {
                if (req.getBookId().equals(request.getBookId()) &&
                        req.getStudentId().equals(request.getStudentId())) {
                    PRINTER.reject(request);
                    return;
                }
            }
        }
        if ((request.getBookId().isTypeA()) ||
                (request.getBookId().isTypeB() &&
                        users.get(request.getStudentId()).hasTypeB()) ||
                (request.getBookId().isTypeC() &&
                        users.get(request.getStudentId()).
                                hasSameTypeCBook(request.getBookId()))) {
            PRINTER.reject(request);
            return;
        }
        orderRequests.add(request);
        PRINTER.accept(request);
    }

    @Trigger(from = "AppointmentOffice", to = { "User"})
    public void pickBook(LibraryReqCmd request) {
        if (!ao.getAppointedBooks().containsKey(request.getBookId())) {
            PRINTER.reject(request);
            return;
        }
        if ((request.getBookId().isTypeA()) ||
                (request.getBookId().isTypeB() &&
                        users.get(request.getStudentId()).hasTypeB()) ||
                (request.getBookId().isTypeC() &&
                        users.get(request.getStudentId()).
                                hasSameTypeCBook(request.getBookId()))) {
            PRINTER.reject(request);
            return;
        }
        if ((request.getBookId().isTypeAU()) ||
                (request.getBookId().isTypeBU() &&
                        users.get(request.getStudentId()).hasTypeBU()) ||
                (request.getBookId().isTypeCU() &&
                        users.get(request.getStudentId()).
                                hasSameTypeCBook(request.getBookId()))) {
            PRINTER.reject(request);
            return;
        }
        HashMap<String, Integer> map = ao.getAppointedBooks().get(request.getBookId());
        if (!map.containsKey(request.getStudentId())) {
            PRINTER.reject(request);
            return;
        }
        int min = 6;
        for (String userId : map.keySet()) {
            if (userId.equals(request.getStudentId())) {
                if (map.get(userId) >= 0 && map.get(userId) < min) {
                    min = map.get(userId);
                }
            }
        }
        if (min == 6) {
            PRINTER.reject(request);
            return;
        }
        PRINTER.accept(request);
        ao.removeBook(request.getBookId(), request.getStudentId());
        users.get(request.getStudentId()).addBook(request.getBookId(), date);
    }

    @Trigger(from = "InitState", to = { "BookDriftCorner"})
    public void donateBook(LibraryReqCmd request) {
        if (allBooks.containsKey(request.getBookId())) {
            PRINTER.reject(request);
            return;
        }
        users.get(request.getStudentId()).updateCredits(2);
        allBooks.put(request.getBookId(), 1);
        donatedBooks.put(request.getBookId(), 2);
        donatedBookUsers.put(request.getBookId(), request.getStudentId());
        bdc.addBook(request.getBookId());
        PRINTER.accept(request);
    }

    public void renewedBook(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        if (users.get(request.getStudentId()).notEnoughCredits()) {
            PRINTER.reject(request);
            return;
        }
        if (bookId.isTypeAU() || bookId.isTypeBU() || bookId.isTypeCU()) {
            PRINTER.reject(request);
            return;
        }
        if (!bs.hasBook(bookId)) {
            if (ao.getAppointedBooks().containsKey(bookId) &&
                    !ao.getAppointedBooks().get(bookId).isEmpty()) {
                PRINTER.reject(request);
                return;
            }
            for (LibraryReqCmd req : orderRequests) {
                if (req.getBookId().equals(bookId)) {
                    PRINTER.reject(request);
                    return;
                }
            }
        }
        if (users.get(request.getStudentId()).allowRenew(bookId, date)) {
            users.get(request.getStudentId()).renewedBook(bookId);
            PRINTER.accept(request);
        } else {
            PRINTER.reject(request);
        }
    }

    public void solveRequest(LibraryReqCmd request) {
        switch (request.getType()) {
            case RETURNED:
                returnBook(request);
                break;
            case BORROWED:
                borrowBook(request);
                break;
            case QUERIED:
                queryBook(request);
                break;
            case ORDERED:
                orderBook(request);
                break;
            case PICKED:
                pickBook(request);
                break;
            case RENEWED:
                renewedBook(request);
                break;
            case DONATED:
                donateBook(request);
                break;
            default:
                break;
        }
    }

    public void run() {
        while (true) {
            LibraryCommand command = scanner.nextCommand();
            if (command == null) {
                break;
            }
            if (date == null) {
                date = command.getDate();
            }
            if (command instanceof LibraryOpenCmd) {
                while (date.isBefore(command.getDate())) {
                    restDayCount++;
                    date = date.plusDays(1);
                }
                if (restDayCount > 0) {
                    arrangeOverdueBooks(restDayCount);
                    restDayCount = 0;
                }
                PRINTER.move(date, info);
                info.clear();
                for (User user : users.values()) {
                    int old = user.getOverdueCount();
                    int now = user.updateOverdueCount(date);
                    user.updateCredits((now - old) * -2);
                }
            } else if (command instanceof LibraryCloseCmd) {
                for (User user : users.values()) {
                    int old = user.getOverdueCount();
                    int now = user.updateOverdueCount(date.plusDays(1));
                    user.updateCredits((now - old) * -2);
                }
                arrangeBooks();
            } else if (command instanceof LibraryQcsCmd) {
                LibraryQcsCmd request = (LibraryQcsCmd) command;
                if (!users.containsKey(request.getStudentId())) {
                    users.put(request.getStudentId(), new User(request.getStudentId()));
                }
                int credits = users.containsKey(((LibraryQcsCmd) command).getStudentId()) ?
                        users.get(((LibraryQcsCmd) command).getStudentId()).getCredits() : 0;
                PRINTER.info(date, ((LibraryQcsCmd) command).getStudentId(), credits);
            } else {
                LibraryReqCmd req = (LibraryReqCmd) command;
                if (!users.containsKey(req.getStudentId())) {
                    users.put(req.getStudentId(), new User(req.getStudentId()));
                }
                solveRequest(req);
            }
        }
    }
}
