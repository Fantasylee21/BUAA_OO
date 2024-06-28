import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;

public class BookDriftCorner {
    private final HashMap<LibraryBookId, Integer> books;

    public BookDriftCorner() {
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

    public int getBookCount(LibraryBookId bookId) {
        return books.getOrDefault(bookId, 0);
    }

    public boolean hasBook(LibraryBookId bookId) {
        return books.containsKey(bookId);
    }
}
