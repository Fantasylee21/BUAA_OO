import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.util.HashMap;

public class User {
    private final String userId;
    private int typeBCount;
    private int typeBuCount;
    private HashMap<LibraryBookId, LocalDate> books;
    private int credits;
    private int overdueCount;

    public User(String userId) {
        this.userId = userId;
        this.typeBCount = 0;
        this.typeBuCount = 0;
        this.books = new HashMap<>();
        this.credits = 10;
        this.overdueCount = 0;
    }

    public void addBook(LibraryBookId bookId, LocalDate date) {
        if (bookId.isTypeB()) {
            typeBCount++;
            books.put(bookId, date.plusDays(30));
        } else if (bookId.isTypeC()) {
            books.put(bookId, date.plusDays(60));
        } else if (bookId.isTypeBU()) {
            typeBuCount++;
            books.put(bookId, date.plusDays(7));
        } else if (bookId.isTypeCU()) {
            books.put(bookId, date.plusDays(14));
        }
    }

    public void removeBook(LibraryBookId bookId) {
        if (bookId.isTypeB()) {
            typeBCount--;
        }
        if (bookId.isTypeBU()) {
            typeBuCount--;
        }
        books.remove(bookId);
    }

    public boolean hasTypeB() {
        return typeBCount != 0;
    }

    public boolean hasTypeBU() {
        return typeBuCount != 0;
    }

    public boolean hasSameTypeCBook(LibraryBookId bookId) {
        return books.containsKey(bookId);
    }

    public boolean hasBook(LibraryBookId bookId) {
        return books.containsKey(bookId);
    }

    public void renewedBook(LibraryBookId bookId) {
        LocalDate due = books.get(bookId);
        books.put(bookId, due.plusDays(30));
    }

    public boolean isOverdue(LibraryBookId bookId, LocalDate date) {
        return date.isAfter(books.get(bookId));
    }

    public boolean allowRenew(LibraryBookId bookId, LocalDate date) {
        if (!books.containsKey(bookId)) {
            return false;
        }
        LocalDate due = books.get(bookId);
        if (date.isAfter(due)) {
            return false;
        }
        return date.plusDays(5).isAfter(due);
    }

    public int getCredits() {
        return this.credits;
    }

    public void updateCredits(int credits) {
        int newCredits = this.credits + credits;
        this.credits = Math.min(newCredits, 20);
    }

    public boolean notEnoughCredits() {
        return credits < 0;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public int updateOverdueCount(LocalDate date) {
        int cnt = 0;
        for (LibraryBookId bookId : books.keySet()) {
            if (date.isAfter(books.get(bookId))) {
                cnt++;
            }
        }
        overdueCount = cnt;
        return cnt;
    }

    public void subOverdueCount() {
        overdueCount--;
    }
}
