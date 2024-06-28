import com.oocourse.library3.LibraryScanner;
import com.oocourse.library3.LibrarySystem;

public class MainClass {
    public static void main(String[] args) {
        LibraryScanner scanner = LibrarySystem.SCANNER;
        Library library = new Library(scanner);
        library.init();
        library.run();
    }
}
