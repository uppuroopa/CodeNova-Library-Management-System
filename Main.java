
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Book> books = new ArrayList<>();
    private static final String FILE_NAME = "books.csv";

    public static void main(String[] args) {
        loadBooks();

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by ID");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Save Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> searchBook();
                case 4 -> issueBook();
                case 5 -> returnBook();
                case 6 -> saveBooks();
                case 7 -> {
                    saveBooks();
                    System.out.println("Thank you for using the Library Management System!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Enter 1 to 7.");
            }
        }
    }

    private static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }

        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static void addBook() {
        System.out.print("Enter book ID: ");
        int id = readInt();

        if (id <= 0 || findBook(id) != null) {
            System.out.println("Invalid or duplicate book ID.");
            return;
        }

        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()
                || title.contains(",") || author.contains(",")) {
            System.out.println("Title and author are required; commas are not allowed.");
            return;
        }

        books.add(new Book(id, title, author, false));
        saveBooks();
        System.out.println("Book added successfully!");
    }

    private static void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        System.out.println("\n===== BOOK LIST =====");
        for (Book book : books) {
            book.display();
        }
    }

    private static void searchBook() {
        System.out.print("Enter book ID to search: ");
        int id = readInt();

        Book book = findBook(id);

        if (book == null) {
            System.out.println("Book not found.");
        } else {
            book.display();
        }
    }

    private static void issueBook() {
        System.out.print("Enter book ID to issue: ");
        int id = readInt();

        Book book = findBook(id);

        if (book == null) {
            System.out.println("Book not found.");
        } else if (book.isIssued()) {
            System.out.println("This book is already issued.");
        } else {
            book.setIssued(true);
            saveBooks();
            System.out.println("Book issued successfully!");
        }
    }

    private static void returnBook() {
        System.out.print("Enter book ID to return: ");
        int id = readInt();

        Book book = findBook(id);

        if (book == null) {
            System.out.println("Book not found.");
        } else if (!book.isIssued()) {
            System.out.println("This book has not been issued.");
        } else {
            book.setIssued(false);
            saveBooks();
            System.out.println("Book returned successfully!");
        }
    }

    private static Book findBook(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }

        return null;
    }

    private static void saveBooks() {
        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FILE_NAME)))) {

            writer.println("ID,Title,Author,Issued");

            for (Book book : books) {
                writer.printf("%d,%s,%s,%b%n",
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.isIssued());
            }

        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    private static void loadBooks() {
        Path path = Paths.get(FILE_NAME);

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 4) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0]);
                    String title = data[1];
                    String author = data[2];
                    boolean issued = Boolean.parseBoolean(data[3]);

                    if (id > 0 && !title.isBlank() && !author.isBlank()
                            && (data[3].equals("true") || data[3].equals("false"))
                            && findBook(id) == null) {
                        books.add(new Book(id, title, author, issued));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping an invalid book record.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }
}
