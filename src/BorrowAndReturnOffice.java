import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;

public class BorrowAndReturnOffice {
    private HashMap<LibraryBookId, Integer> books;

    public BorrowAndReturnOffice() {
        books = new HashMap<>();
    }

    public void addBook(LibraryBookId bookId) {
        if (books.containsKey(bookId)) {
            books.put(bookId, books.get(bookId) + 1);
        } else {
            books.put(bookId, 1);
        }
    }

    public void removeBook(LibraryBookId bookId) {
        if (books.containsKey(bookId)) {
            if (books.get(bookId) == 1) {
                books.remove(bookId);
            } else {
                books.put(bookId, books.get(bookId) - 1);
            }
        }
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    public LibraryBookId getBook() {
        LibraryBookId bookId = books.keySet().iterator().next();
        removeBook(bookId);
        return bookId;
    }

    public boolean hasBook(LibraryBookId bookId) {
        return books.containsKey(bookId);
    }
}
