import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;

public class AppointmentOffice {
    private final HashMap<LibraryBookId, HashMap<String, Integer>> appointedBooks;

    public AppointmentOffice() {
        appointedBooks = new HashMap<>();
    }

    public void addBook(LibraryBookId bookId, String userId) {
        if (appointedBooks.containsKey(bookId)) {
            appointedBooks.get(bookId).put(userId, 5);
        } else {
            HashMap<String, Integer> map = new HashMap<>();
            map.put(userId, 5);
            appointedBooks.put(bookId, map);
        }
    }

    public void removeBook(LibraryBookId bookId, String userId) {
        if (appointedBooks.containsKey(bookId)) {
            appointedBooks.get(bookId).remove(userId);
            if (appointedBooks.get(bookId).isEmpty()) {
                appointedBooks.remove(bookId);
            }
        }
    }

    public HashMap<LibraryBookId, HashMap<String, Integer>> getAppointedBooks() {
        return this.appointedBooks;
    }

    public boolean hasTypeB(String userId) {
        for (LibraryBookId bookId : appointedBooks.keySet()) {
            if (bookId.isTypeB() && appointedBooks.get(bookId).containsKey(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBook(LibraryBookId bookId, String userId) {
        for (LibraryBookId id : appointedBooks.keySet()) {
            if (id.equals(bookId) && appointedBooks.get(id).containsKey(userId)) {
                return true;
            }
        }
        return false;
    }

}
