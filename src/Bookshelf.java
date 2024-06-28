import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;
import java.util.Map;

public class Bookshelf {
    private HashMap<LibraryBookId, Integer> books;

    public Bookshelf() {
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

    public void init(Map<LibraryBookId, Integer> map) {
        for (LibraryBookId bookId : map.keySet()) {
            books.put(bookId, map.get(bookId));
        }
    }

    public boolean hasBook(LibraryBookId bookId) {
        return books.containsKey(bookId);
    }

    public int getBookCount(LibraryBookId bookId) {
        return books.getOrDefault(bookId, 0);
    }
}
