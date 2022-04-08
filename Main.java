import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.sql.Date;

public class Main {
    public static void main(String[] arg) throws SQLException, IOException {
        final String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db2";
        final String dbUsername = "Group2";
        final String dbPassword = "Group2winwinwin";

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            System.out.println("[Error]: Java MySQL DB Driver not found!!");
            System.exit(0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (conn != null) {
            System.out.println("Welcome to Library Inquiry System!");
            start_menu(conn);
        } else {
            System.out.println("[Error]: Failed to make connection!!!");
        }
    }

    public static void start_menu(Connection conn) throws SQLException, IOException {
        while (true) {
            System.out.println("-----Main menu-----");
            System.out.println("What kinds of operations would you like to perform?");
            System.out.println("1. Operations for Administrator");
            System.out.println("2. Operations for Library User");
            System.out.println("3. Operations for librarian");
            System.out.println("4. Exit this program");
            System.out.print("Enter your choice: ");

            // Waiting for user input
            int userInput;
            Scanner input = new Scanner(System.in);
            userInput = input.nextInt();

            switch (userInput) {
                case 1:
                    admin_operation(conn);
                    break;
                case 2:
                    libuser_operation(conn);
                    break;
                case 3:
                    librarian_operation(conn);
                    break;
                case 4:
                    System.out.println("Bye bye!");
                    System.exit(1);
                default:
                    System.out.println("[Error]: Invalid operation, please choose again.\n");
            }
        }
    }

    public static void admin_operation(Connection conn) throws SQLException, IOException {
        while (true) {
            System.out.println("-----Operations for administrator menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Create all tables");
            System.out.println("2. Delete all tables");
            System.out.println("3. Load from datafile");
            System.out.println("4. Show number of records in each table");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter your choice: ");

            // Waiting for administrator input
            int userInput;
            Scanner input = new Scanner(System.in);
            userInput = input.nextInt();

            switch (userInput) {
                case 1:
                    createAllTables(conn);
                    break;
                case 2:
                    deleteAllTables(conn);
                    break;
                case 3:
                    loadDataFile(conn);
                    break;
                case 4:
                    showRecords(conn);
                    break;
                case 5:
                    start_menu(conn);
                    break;
                default:
                    System.out.println("[Error]: Invalid operation, please choose again.\n");
            }
        }
    }

    public static void libuser_operation(Connection conn) throws SQLException, IOException {
        while (true) {
            System.out.println("-----Operations for library user menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Search for Books");
            System.out.println("2. Show load record of a user");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter your choice: ");

            // Waiting for administrator input
            int userInput;
            Scanner input = new Scanner(System.in);
            userInput = input.nextInt();

            switch (userInput) {
                case 1:
                    searchBooks(conn);
                    break;
                case 2:
                    showLoanRecord(conn);
                    break;
                case 3:
                    start_menu(conn);
                    break;
                default:
                    System.out.println("[Error] Invalid operation, please choose again.\n");
            }
        }
    }

    public static void librarian_operation(Connection conn) throws SQLException, IOException {
        while (true) {
            System.out.println("-----Operations for librarian menu-----");
            System.out.println("What kind of operation would you like to perform?");
            System.out.println("1. Book Borrowing");
            System.out.println("2. Book Returning");
            System.out.println("3. List all un-returned book copies which are checked-out with a period");
            System.out.println("4. Return to the main menu");
            System.out.print("Enter Your Choice: ");

            // Waiting for administrator input
            int userInput;
            Scanner input = new Scanner(System.in);
            userInput = input.nextInt();

            switch(userInput){
                case 1:
                    book_borrowing(conn);
                    break;
                case 2:
                    book_returning(conn); 
                    break;
                case 3:
                    list_all_unreturned_books(conn); 
                    break;
                case 4:
                    start_menu(conn);
                default:
                    System.out.println("[Error]: Invalid operation, please choose again.\n");
            }
        }
    }

    // ---------- ADMIN OPERATION ---------- //
    public static void createAllTables(Connection conn) {
        String createUserCategory = "CREATE TABLE user_category (ucid INTEGER NOT NULL, max INTEGER NOT NULL, period INTEGER NOT NULL, PRIMARY KEY (ucid))";
        String createLibraryUser = "CREATE TABLE libuser (libuid CHAR(10) NOT NULL, name VARCHAR(25) NOT NULL, age INTEGER NOT NULL, address VARCHAR(100) NOT NULL, ucid INTEGER NOT NULL, PRIMARY KEY (libuid))";
        String createBookCategory = "CREATE TABLE book_category (bcid INTEGER NOT NULL, bcname VARCHAR(30) NOT NULL, PRIMARY KEY (bcid))";
        String createBook = "CREATE TABLE book (callnum CHAR(8) NOT NULL, title VARCHAR(30) NOT NULL, publish DATE NOT NULL, rating FLOAT, tborrowed INTEGER NOT NULL, bcid INTEGER NOT NULL, PRIMARY KEY (callnum))";
        String createCopy = "CREATE TABLE copy (callnum CHAR(8) NOT NULL, copynum INTEGER NOT NULL, PRIMARY KEY (callnum, copynum))";
        String createBorrow = "CREATE TABLE borrow (libuid CHAR(10) NOT NULL, callnum CHAR(8) NOT NULL, copynum INTEGER NOT NULL, checkout DATE NOT NULL, return_date DATE, PRIMARY KEY (libuid, callnum, copynum, checkout))";
        String createAuthorship = "CREATE TABLE authorship (aname VARCHAR(25) NOT NULL, callnum CHAR(8) NOT NULL, PRIMARY KEY (aname, callnum))";

        try {
            System.out.print("Processing... ");
            PreparedStatement[] stmts_create_table = { 
                conn.prepareStatement(createUserCategory),
                conn.prepareStatement(createLibraryUser), conn.prepareStatement(createBookCategory),
                conn.prepareStatement(createBook), conn.prepareStatement(createCopy),
                conn.prepareStatement(createBorrow), conn.prepareStatement(createAuthorship), 
            };

            for (int i = 0; i < stmts_create_table.length; i++) {
                stmts_create_table[i].executeUpdate();
            }
            System.out.println("Done. Database is initialized.\n");
            admin_operation(conn);
        } catch (Exception ex) {
            System.out.println("[Error]: Database initialization failed.\n");
        }
    }

    public static void deleteAllTables(Connection conn) {
        final String[] tableNames = { "user_category", "libuser", "book_category", "book", "copy", "borrow", "authorship" };

        try {
            System.out.print("Processing... ");
            for (int i = 0; i < tableNames.length; i++) {
                PreparedStatement stmt_delete_table = conn.prepareStatement("DROP TABLE IF EXISTS " + tableNames[i]);
                stmt_delete_table.execute();
            }
            System.out.println("Done. Database is removed.\n");
            admin_operation(conn);
        } catch (Exception ex) {
            System.out.println("[Error]: Failed to remove database.\n");
        }
    }

    public static List<String[]> getDataFromText(String folder, String file) {
        List<String[]> dataArray = new ArrayList<String[]>();
        try {
            // Get path
            File fileTarget = new File(folder + "/" + file);
            BufferedReader reader = new BufferedReader(new FileReader(fileTarget));
            String data = reader.readLine();
            while (data != null) {
                if (data.length() > 0) {
                    // Each line be one row
                    dataArray.add(data.split("\t"));
                }
                data = reader.readLine();
            }
            reader.close();
        } catch (Exception ex) {
            System.out.println("[Error]: Failed to get data from text.\n");
        }
        return dataArray;
    }

    // Load all data into database
    public static void loadDataFile(Connection conn) {
        System.out.print("Type in the Source Data Folder Path: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String folder = reader.readLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.print("Processing... ");

            // Load user category
            try {
                List<String[]> user_category = getDataFromText(folder, "user_category.txt");
                PreparedStatement stmt_user_category = conn
                        .prepareStatement("INSERT INTO user_category (ucid, max, period) VALUES (?, ?, ?)");
                for (String[] data : user_category) {
                    stmt_user_category.setInt(1, Integer.parseInt(data[0])); // ucid
                    stmt_user_category.setInt(2, Integer.parseInt(data[1])); // max
                    stmt_user_category.setInt(3, Integer.parseInt(data[2])); //period
                    stmt_user_category.executeUpdate();
                }
                stmt_user_category.close();
            } catch (Exception ex) {
                System.out.println("[Error]: Failed to load user category.\n");
            }

            // Load libuser
            try {
                List<String[]> libuser = getDataFromText(folder, "user.txt");
                PreparedStatement stmt_libuser = conn
                        .prepareStatement("INSERT INTO libuser (libuid, name, age, address, ucid) VALUES (?, ?, ?, ?, ?)");
                for (String[] data : libuser) {
                    stmt_libuser.setString(1, data[0]); // libuid
                    stmt_libuser.setString(2, data[1]); // name
                    stmt_libuser.setInt(3, Integer.parseInt(data[2])); // age
                    stmt_libuser.setString(4, data[3]); // address
                    stmt_libuser.setInt(5, Integer.parseInt(data[4])); // ucid
                    stmt_libuser.executeUpdate();
                }
                stmt_libuser.close();
            } catch (Exception ex) {
                System.out.println("[Error]: Failed to load library user.\n");
            }

            // Load book category
            try {
                List<String[]> book_category = getDataFromText(folder, "book_category.txt");
                PreparedStatement stmt_book_category = conn
                        .prepareStatement("INSERT INTO book_category (bcid, bcname) VALUES (?, ?)");
                for (String[] data : book_category) {
                    stmt_book_category.setInt(1, Integer.parseInt(data[0])); // bcid
                    stmt_book_category.setString(2, data[1]); // bcname
                    stmt_book_category.executeUpdate();
                }
                stmt_book_category.close();
            } catch (Exception ex) {
                System.out.println("[Error]: Failed to load book category.\n");
            }

            // Load book, copy, authorship
            try {
                List<String[]> book = getDataFromText(folder, "book.txt");
                PreparedStatement stmt_book = conn.prepareStatement(
                        "INSERT INTO book (callnum, title, publish, rating, tborrowed, bcid) VALUES (?, ?, ?, ?, ?, ?)");
                PreparedStatement stmt_copy = conn.prepareStatement("INSERT INTO copy (callnum, copynum) VALUES (?, ?)");
                PreparedStatement stmt_authorship = conn
                        .prepareStatement("INSERT INTO authorship (aname, callnum) VALUES (?, ?)");
                for (String[] data : book) {
                    stmt_book.setString(1, data[0]); // callnum
                    int copyFlag = Integer.parseInt(data[1]); // copynum
                    stmt_book.setString(2, data[2]); // title
                    stmt_book.setDate(3, new java.sql.Date(dateFormat.parse(data[4]).getTime())); // publish
                    if (!data[5].equals("null")) {
                        stmt_book.setFloat(4, Float.parseFloat(data[5])); // rating
                    } else {
                        stmt_book.setNull(4, Types.NULL); // rating
                    }
                    stmt_book.setInt(5, Integer.parseInt(data[6])); // tborrowed
                    stmt_book.setInt(6, Integer.parseInt(data[7])); // bcid
                    stmt_book.executeUpdate();
                    for (int k = 0; k <copyFlag; k++) {
                        stmt_copy.setString(1, data[0]); // callnum
                        stmt_copy.setInt(2, k+1); // copynum
                        stmt_copy.executeUpdate();
                    }

                    String[] authors = data[3].split(",");
                    for (String author : authors) {
                        stmt_authorship.setString(1, author); // aname
                        stmt_authorship.setString(2, data[0]); // callnum
                        stmt_authorship.executeUpdate();
                    }
                }
                stmt_book.close();
                stmt_copy.close();
                stmt_authorship.close();
            } catch (Exception ex) {
                System.out.println("[Error]: Failed to load book, copy or authorship.\n");
            }

            // Load borrow
            try {
                List<String[]> borrow = getDataFromText(folder, "check_out.txt");
                PreparedStatement stmt_borrow = conn.prepareStatement(
                        "INSERT INTO borrow (callnum, copynum, libuid, checkout, return_date) VALUES (?, ?, ?, ?, ?)");
                for (String[] data : borrow) {
                    stmt_borrow.setString(1, data[0]); // callnum
                    stmt_borrow.setInt(2, Integer.parseInt(data[1])); // copynum
                    stmt_borrow.setString(3, data[2]); // libuid
                    stmt_borrow.setDate(4, new java.sql.Date(dateFormat.parse(data[3]).getTime())); // checkout
                    if (data[4].equals("null")) {
                        stmt_borrow.setDate(5, null); // return_date
                    } else {
                        stmt_borrow.setDate(5, new java.sql.Date(dateFormat.parse(data[4]).getTime())); // return_date
                    }
                    stmt_borrow.executeUpdate();
                }
                stmt_borrow.close();
            } catch (Exception ex) {
                System.out.println("[Error]: Failed to load borrow.\n");
            }

            System.out.println("Data is inputted to the database.\n");
            admin_operation(conn);
        } catch (Exception ex) {
            System.out.println("[Error]: Failed to load data from text file.\n");
        }
    }

    public static void showRecords(Connection conn) {
        final String[] tableNames = { "user_category", "libuser", "book_category", "book", "copy", "borrow",
                "authorship" };

        try {
            System.out.println("Number of records in each table:");
            for (int i = 0; i < tableNames.length; i++) {
                PreparedStatement stmt_show_records = conn.prepareStatement("SELECT COUNT(*) FROM " + tableNames[i]);
                ResultSet resultRecords = stmt_show_records.executeQuery();
                resultRecords.next();
                int countRecord = resultRecords.getInt(1);
                System.out.println(tableNames[i] + ": " + countRecord);
            }
            System.out.println("Done. Retrived all tables succussfully.\n");
            admin_operation(conn);
        } catch (Exception ex) {
            System.out.println("[Error]: Failed to retrive all tables.\n");
        }
    }

    // ---------- LIBRARY USER OPERATION ---------- //
    public static void searchBooks(Connection conn) throws SQLException, IOException {
        while (true) {
            System.out.println("Choose the Search criterion:");
            System.out.println("1. call number");
            System.out.println("2. title");
            System.out.println("3. author");
            System.out.printf("Choose the search criterion: ");

            int userInput;
            Scanner input = new Scanner(System.in);
            userInput = input.nextInt();

            switch (userInput) {
                case 1:
                    searchBooksByCallnum(conn);
                    break;
                case 2:
                    searchBooksByTitle(conn);
                    break;
                case 3:
                    searchBooksByAuthor(conn);
                    break;
                default:
                    System.out.println("[Error] Invalid option, please choose again.\n");
            }
        }
    }

    public static void searchBooksByCallnum(Connection conn) throws SQLException, IOException {
        System.out.print("Type in the Search Keyword: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String callnumTarget = "";
        String title = "";
        String bookCat = "";
        ArrayList<String> authors = new ArrayList<String>();
        Float rating;
        int availableCopies;

        boolean haveOneAuthor = true;

        try {
            String callnum = reader.readLine();
            // Determine if the book is existing. Because callnum is unique in book table, we don't need to use DISTINCT and ASC
            PreparedStatement stmt_search_by_callnum = conn.prepareStatement("SELECT callnum FROM book WHERE callnum = ?");
            stmt_search_by_callnum.setString(1, callnum);
            ResultSet result = stmt_search_by_callnum.executeQuery();
            while (result.next()) {
                callnumTarget = result.getString(1);
            }

            // if callnum is not exist
            if (callnumTarget.isEmpty()) {
                System.out.println("[Error]: Cannot find the book.\n");
                librarian_operation(conn);
            }

            // Format
            System.out.println("| Call Num | Title | Book Category | Author | Rating | Available No. of Copy |");
            
            // Select title
            PreparedStatement stmt_get_title = conn.prepareStatement("SELECT title FROM book WHERE callnum = ?");
            stmt_get_title.setString(1, callnumTarget);
            ResultSet titleResult = stmt_get_title.executeQuery();
            if (!titleResult.next()) {
                System.out.println("[Error]: No match title.\n");
            }
            title = titleResult.getString(1);

            // Select book category
            PreparedStatement stmt_get_bookcat = conn.prepareStatement("SELECT bcname FROM book, book_category WHERE book.callnum = ? AND book.bcid = book_category.bcid");
            stmt_get_bookcat.setString(1, callnumTarget);
            ResultSet bookcatResult = stmt_get_bookcat.executeQuery();
            if (!bookcatResult.next()) {
                System.out.println("[Error]: No match book category.\n");
            }
            bookCat = bookcatResult.getString(1);
        
            // Select authors
            PreparedStatement stmt_get_authors = conn.prepareStatement("SELECT aname FROM authorship WHERE callnum = ?");
            stmt_get_authors.setString(1, callnumTarget);
            ResultSet authorsResult = stmt_get_authors.executeQuery();
            if (!authorsResult.isBeforeFirst()) {
                System.out.println("[Error]: No match authors.\n");
            }
            while (authorsResult.next()) {
                authors.add(authorsResult.getString(1));
            }

            // Select rating
            PreparedStatement stmt_get_rating = conn.prepareStatement("SELECT rating FROM book WHERE callnum = ?");
            stmt_get_rating.setString(1, callnumTarget);
            ResultSet ratingResult = stmt_get_rating.executeQuery();
            if (!ratingResult.next()) {
                System.out.println("[Error]: No match book rating.\n");
            }
            rating = ratingResult.getFloat(1);
            if (rating == 0.0 || rating == 0) {
                rating = null;
            }

            // Select available copies
            String copies = "SELECT COUNT(*) FROM copy WHERE (callnum, copynum) NOT IN ";
            String checkoutCopies = "(SELECT callnum, copynum FROM borrow WHERE callnum = ? AND return_date IS NULL) AND callnum = ?";
            PreparedStatement stmt_get_available_copies = conn.prepareStatement(copies + checkoutCopies);
            stmt_get_available_copies.setString(1, callnumTarget);
            stmt_get_available_copies.setString(2, callnumTarget);
            ResultSet copiesResult = stmt_get_available_copies.executeQuery();
            if (!copiesResult.next()) {
                System.out.println("[Error]: No match available copies.\n");
            }
            availableCopies = copiesResult.getInt(1);

            stmt_search_by_callnum.close();
            stmt_get_title.close();
            stmt_get_bookcat.close();
            stmt_get_authors.close();
            stmt_get_rating.close();
            stmt_get_available_copies.close();

            System.out.print("| " + callnumTarget + " | " + title + " | " + bookCat + " | ");
            for (String author: authors) {
                System.out.printf(haveOneAuthor ? author : (", " + author));
                haveOneAuthor = false;
            }
            System.out.print(" | " + rating + " | " + availableCopies + " | " + "\n");
            System.out.println("End of Query");

            libuser_operation(conn);
        } catch (Exception ex) {
            System.out.println("[Error]: Failed to search book by call number.\n");
        }
    }

    public static void searchBooksByTitle(Connection conn) throws SQLException, IOException {
		// These are for getting list of results
        String wantedCallnum = "";
        ArrayList<String> callnumTargetList = new ArrayList<String>();
		int numOfTargetBooks;

		// Declaring variables for getting final result
		String callnumTarget = "";
		String title = "";
        String bookCat = "";
        ArrayList<String> authors = new ArrayList<String>();
        Float rating;
        int availableCopies;

	    // Getting input from user	
        System.out.print("Type in the Search Keyword: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String titleinput = "";
		while(true){
			titleinput = reader.readLine();
			if(!titleinput.isEmpty())
				break;
		}

		try {
	        /*  Get a list of book is existing */
			PreparedStatement stmt_search_by_title = conn.prepareStatement("SELECT distinct(callnum) FROM book WHERE title LIKE ? ORDER BY callnum ASC");

			stmt_search_by_title.setString(1, '%' + titleinput + '%');
			ResultSet callnumResult = stmt_search_by_title.executeQuery();
			
			while (callnumResult.next()){
				wantedCallnum = callnumResult.getString(1);
				callnumTargetList.add(wantedCallnum);
			}

			numOfTargetBooks = 	callnumTargetList.size();

			if(numOfTargetBooks == 0){
				System.out.println("[Error]: Cannot find any book matching.\n");
				libuser_operation(conn);
			}

			stmt_search_by_title.close();
			
 	        /* for each callnum, get result and print for each column */
			
			//  Format
            System.out.println("| Call Num | Title | Book Category | Author | Rating | Available No. of Copy |");

			//  For each callnum, print required results
			for (int i = 0; i < numOfTargetBooks; i++) {
				callnumTarget = callnumTargetList.get(i);
				authors.clear();

				// Select title
				PreparedStatement stmt_get_title = conn.prepareStatement("SELECT title FROM book WHERE callnum = ?");
				stmt_get_title.setString(1, callnumTarget);
				ResultSet titleResult = stmt_get_title.executeQuery();
				if (!titleResult.next()) {
					System.out.println("[Error]: No match title.\n");
				}
				title = titleResult.getString(1);

				// Select book category
				PreparedStatement stmt_get_bookcat = conn.prepareStatement("SELECT bcname FROM book, book_category WHERE book.callnum = ? AND book.bcid = book_category.bcid");
				stmt_get_bookcat.setString(1, callnumTarget);
				ResultSet bookcatResult = stmt_get_bookcat.executeQuery();
				if (!bookcatResult.next()) {
					System.out.println("[Error]: No match book category.\n");
				}
				bookCat = bookcatResult.getString(1);

				// Select authors
				PreparedStatement stmt_get_authors = conn.prepareStatement("SELECT aname FROM authorship WHERE callnum = ?");
				stmt_get_authors.setString(1, callnumTarget);
				ResultSet authorsResult = stmt_get_authors.executeQuery();
				if (!authorsResult.isBeforeFirst()) {
					System.out.println("[Error]: No match authors.\n");
				}
				while (authorsResult.next()) {
					authors.add(authorsResult.getString(1));
				}

				// Select rating
				PreparedStatement stmt_get_rating = conn.prepareStatement("SELECT rating FROM book WHERE callnum = ?");
				stmt_get_rating.setString(1, callnumTarget);
				ResultSet ratingResult = stmt_get_rating.executeQuery();
				if (!ratingResult.next()) {
					System.out.println("[Error]: No match book rating.\n");
				}
                rating = ratingResult.getFloat(1);
                if (rating == 0.0 || rating == 0) {
                    rating = null;
                }

				// Select available copies
				String copies = "SELECT COUNT(*) FROM copy WHERE (callnum, copynum) NOT IN ";
				String checkoutCopies = "(SELECT callnum, copynum FROM borrow WHERE callnum = ? AND return_date IS NULL) AND callnum = ?";
				PreparedStatement stmt_get_available_copies = conn.prepareStatement(copies + checkoutCopies);
				stmt_get_available_copies.setString(1, callnumTarget);
				stmt_get_available_copies.setString(2, callnumTarget);
				ResultSet copiesResult = stmt_get_available_copies.executeQuery();
				if (!copiesResult.next()) {
					System.out.println("[Error]: No match available copies.\n");
				}
				availableCopies = copiesResult.getInt(1);

				stmt_get_title.close();
				stmt_get_bookcat.close();
				stmt_get_authors.close();
				stmt_get_rating.close();
				stmt_get_available_copies.close();

				System.out.print("| " + callnumTarget + " | " + title + " | " + bookCat + " | ");

				if(authors.size() == 1){
			        System.out.print(authors.get(0));
				}

				if(authors.size() > 1){
					System.out.print(authors.get(0));
					for (int j = 1; j < authors.size(); j++){
						System.out.print(", " + authors.get(j));
					}
				}
				System.out.print(" | " + rating + " | " + availableCopies + " | " + "\n");
			}

			libuser_operation(conn);			
		}
		catch (Exception ex){
			System.out.println("[Error]: Failed to search book by book title.\n");
		}
    }

    public static void searchBooksByAuthor(Connection conn) throws SQLException, IOException {
		// These are for getting list of results
        String wantedCallnum = "";
        ArrayList<String> callnumTargetList = new ArrayList<String>();
		int numOfTargetBooks;

		// Declaring variables for getting final result
		String callnumTarget = "";
		String title = "";
        String bookCat = "";
        ArrayList<String> authors = new ArrayList<String>();
        Float rating;
        int availableCopies;

	    // Getting input from user	
        System.out.print("Type in the Search Keyword: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		String authorinput = "";
		while(true){
			authorinput = reader.readLine();
			if(!authorinput.isEmpty())
				break;
		}

		try {
	        /*  Get a list of book is existing */
			PreparedStatement stmt_search_by_author = conn.prepareStatement("SELECT distinct(callnum) FROM authorship WHERE aname LIKE ? ORDER BY callnum ASC");

			stmt_search_by_author.setString(1, '%' + authorinput + '%');
			ResultSet callnumResult = stmt_search_by_author.executeQuery();

			while (callnumResult.next()){
				wantedCallnum = callnumResult.getString(1);
				callnumTargetList.add(wantedCallnum);
			}

			numOfTargetBooks = 	callnumTargetList.size();

			if (numOfTargetBooks == 0) {
				System.out.println("[Error]: Cannot find any book matching.\n");
				libuser_operation(conn);
			}

			stmt_search_by_author.close();

 	        /*	For each callnum, get result and print for each column */

			//  Format
            System.out.println("| Call Num | Title | Book Category | Author | Rating | Available No. of Copy |");
			
			//  For each callnum, print required results
			for (int i = 0; i < numOfTargetBooks; i++){
				callnumTarget = callnumTargetList.get(i);
				authors.clear();

				// Select title
				PreparedStatement stmt_get_title = conn.prepareStatement("SELECT title FROM book WHERE callnum = ?");
				stmt_get_title.setString(1, callnumTarget);
				ResultSet titleResult = stmt_get_title.executeQuery();
				if (!titleResult.next()) {
					System.out.println("[Error]: No match title.\n");
				}
				title = titleResult.getString(1);

				// Select book category
				PreparedStatement stmt_get_bookcat = conn.prepareStatement("SELECT bcname FROM book, book_category WHERE book.callnum = ? AND book.bcid = book_category.bcid");
				stmt_get_bookcat.setString(1, callnumTarget);
				ResultSet bookcatResult = stmt_get_bookcat.executeQuery();
				if (!bookcatResult.next()) {
					System.out.println("[Error]: No match book category.\n");
				}
				bookCat = bookcatResult.getString(1);
			
				// Select authors
				PreparedStatement stmt_get_authors = conn.prepareStatement("SELECT aname FROM authorship WHERE callnum = ?");
				stmt_get_authors.setString(1, callnumTarget);
				ResultSet authorsResult = stmt_get_authors.executeQuery();
				if (!authorsResult.isBeforeFirst()) {
					System.out.println("[Error]: No match authors.\n");
				}
				while (authorsResult.next()) {
					authors.add(authorsResult.getString(1));
				}

				// Select rating
				PreparedStatement stmt_get_rating = conn.prepareStatement("SELECT rating FROM book WHERE callnum = ?");
				stmt_get_rating.setString(1, callnumTarget);
				ResultSet ratingResult = stmt_get_rating.executeQuery();
				if (!ratingResult.next()) {
					System.out.println("[Error]: No match book rating.\n");
				}
                rating = ratingResult.getFloat(1);
                if (rating == 0.0 || rating == 0) {
                    rating = null;
                }

				// Select available copies
				String copies = "SELECT COUNT(*) FROM copy WHERE (callnum, copynum) NOT IN ";
				String checkoutCopies = "(SELECT callnum, copynum FROM borrow WHERE callnum = ? AND return_date IS NULL) AND callnum = ?";
				PreparedStatement stmt_get_available_copies = conn.prepareStatement(copies + checkoutCopies);
				stmt_get_available_copies.setString(1, callnumTarget);
				stmt_get_available_copies.setString(2, callnumTarget);
				ResultSet copiesResult = stmt_get_available_copies.executeQuery();
				if (!copiesResult.next()) {
					System.out.println("[Error]: No match available copies.\n");
				}
				availableCopies = copiesResult.getInt(1);

				stmt_get_title.close();
				stmt_get_bookcat.close();
				stmt_get_authors.close();
				stmt_get_rating.close();
				stmt_get_available_copies.close();

				System.out.print("| " + callnumTarget + " | " + title + " | " + bookCat + " | ");
				
				if (authors.size() == 1) {
				System.out.print(authors.get(0));
				}
				if (authors.size() > 1) {
					System.out.print(authors.get(0));
					for (int j = 1; j < authors.size(); j++){
						System.out.print(", " + authors.get(j));
					}
					
				}
				System.out.print(" | " + rating + " | " + availableCopies + " | " + "\n");
			}
			libuser_operation(conn);			
		}
		catch (Exception ex){
			System.out.println("[Error]: Failed to search book by author name.\n");
		}
    }

    public static void showLoanRecord(Connection conn) throws SQLException, IOException {
		// These are for getting list of results
        String wantedCallnum = "";
		int wantedCopynum;
		String wantedCheckOut = "";
		String wantedReturn = "";
        ArrayList<String> callnumTargetList = new ArrayList<String>();
        ArrayList<Integer> copynumList = new ArrayList<Integer>();
        ArrayList<String> checkOutList = new ArrayList<String>();		
        ArrayList<Boolean> returnList = new ArrayList<Boolean>();

		int numOfTargetBooks;

		// Declaring variables for getting final result
		String callnumTarget = "";
		String title = "";
        ArrayList<String> authors = new ArrayList<String>();
        
		System.out.print("Enter The User ID: ");
		
	    // Getting input from user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String libuidinput = "";
		while(true){
			libuidinput = reader.readLine();
			if(!libuidinput.isEmpty())
				break;
		}

		try {
	        /*  Get a list of book is existing */
			PreparedStatement stmt_search_by_uid = conn.prepareStatement("SELECT callnum, copynum, checkout, return_date FROM borrow where libuid = ?");

			stmt_search_by_uid.setString(1, libuidinput);
			ResultSet callnumResult = stmt_search_by_uid.executeQuery();
			
			while (callnumResult.next()){
				wantedCallnum = callnumResult.getString(1);
				callnumTargetList.add(wantedCallnum);

				wantedCopynum = callnumResult.getInt(2);
				copynumList.add(wantedCopynum);

				wantedCheckOut = callnumResult.getString(3);
				checkOutList.add(wantedCheckOut);

				wantedReturn = callnumResult.getString(4);
				if(wantedReturn == null)
					returnList.add(false);
				else
					returnList.add(true);
			}

			numOfTargetBooks = 	callnumTargetList.size();

			// If no book, go back libuser
			if (numOfTargetBooks == 0) {
				System.out.println("[Error]: Cannot find any record matching.\n");
				libuser_operation(conn);				
			}

			//Format
			System.out.println("| Callnum | CopyNum | Title | Author | Check-out | Returned? |");

			for (int i = 0; i < numOfTargetBooks; i++) {
				callnumTarget = callnumTargetList.get(i);
				authors.clear();

				// Select title
				PreparedStatement stmt_get_title = conn.prepareStatement("SELECT title FROM book WHERE callnum = ?");
				stmt_get_title.setString(1, callnumTarget);
				ResultSet titleResult = stmt_get_title.executeQuery();
				if (!titleResult.next()) {
					System.out.println("[Error]: No match title.\n");
				}
				title = titleResult.getString(1);

				// Select authors
				PreparedStatement stmt_get_authors = conn.prepareStatement("SELECT aname FROM authorship WHERE callnum = ?");
				stmt_get_authors.setString(1, callnumTarget);
				ResultSet authorsResult = stmt_get_authors.executeQuery();
				if (!authorsResult.isBeforeFirst()) {
					System.out.println("[Error]: No match authors.\n");
				}
				while (authorsResult.next()) {
					authors.add(authorsResult.getString(1));
				}

				stmt_get_authors.close();
				stmt_get_title.close();

				System.out.print("| "+ callnumTargetList.get(i)+ " | "+ copynumList.get(i) + " | " + title + " | ");

				if(authors.size() == 1){
				System.out.print(authors.get(0));
				}
				if(authors.size() > 1){
					System.out.print(authors.get(0));
					for (int j = 1; j < authors.size(); j++){
						System.out.print(", " + authors.get(j));
					}
				}
				
				System.out.print(" | " + checkOutList.get(i) + " | " );

				if(returnList.get(i)) {
                    System.out.println("Yes" + " |");
                } else {
                    System.out.println("No" + " |");
                }
			}

			libuser_operation(conn);
		} catch (Exception ex) {
            System.out.println("[Error]: Failed to show loan record.\n");
		}
    }

    // ---------- LIBRARIAN OPERATION ---------- //
    public static void book_borrowing(Connection conn) throws SQLException, IOException {
        System.out.print("Enter The User ID: ");
        Scanner input = new Scanner(System.in);

        try{
            String userid=input.nextLine();
            PreparedStatement checkid = conn.prepareStatement("SELECT COUNT(*) FROM libuser where libuid = ?");
            checkid.setString(1, userid);
            ResultSet idresult = checkid.executeQuery();
            idresult.next();
            if  (idresult.getInt(1) == 0) {
                System.out.println("[Error]: Sorry, the user does not exist.\n");
                librarian_operation(conn);
            }

            System.out.print("Enter The Call Number: ");
            String callnum = input.nextLine();

            System.out.print("Enter The Copy Number: ");
            int copynumber = 0;
            try{
                copynumber=input.nextInt();
            } catch(Exception ex){
                System.out.println("[Error]: Invalid copy number.\n");
                librarian_operation(conn);
            }

            PreparedStatement stmt_copy = conn.prepareStatement("SELECT COUNT(*) FROM copy WHERE callnum = ? AND copynum = ?");
            stmt_copy.setString(1, callnum);
            stmt_copy.setInt(2, copynumber);
            ResultSet result=stmt_copy.executeQuery();
            result.next();

            int bookcounter=result.getInt(1);

            if (bookcounter == 0) {
                System.out.println("[Error]: No match book.\n");
                librarian_operation(conn);
            }

            PreparedStatement borrow_or_not = conn.prepareStatement("SELECT * FROM borrow WHERE callnum = ? AND copynum = ? AND return_date IS NULL");
            borrow_or_not.setString(1, callnum);
            borrow_or_not.setInt(2, copynumber);

            if (borrow_or_not.executeQuery().next() == true){
                System.out.println("[Error]: This book has been lent.\n");
                librarian_operation(conn);
            }

            PreparedStatement get_uid = conn.prepareStatement("SELECT ucid from libuser where libuid = ? ");
            get_uid.setString(1, userid);
            ResultSet for_uid = get_uid.executeQuery();
            for_uid.next();

            PreparedStatement get_max = conn.prepareStatement("select max from user_category where ucid=?");
            get_max.setInt(1, for_uid.getInt(1));
            ResultSet for_max = get_max.executeQuery();
            for_max.next();

            int max_can_borrow = for_max.getInt(1);

            PreparedStatement stmt_already_borrow=conn.prepareStatement("SELECT COUNT(*) FROM borrow WHERE  return_date is NULL and libuid = ?");

            stmt_already_borrow.setString(1, userid);

            ResultSet result_already_borrow=stmt_already_borrow.executeQuery();

            result_already_borrow.next();
            int inte_already_borrow=result_already_borrow.getInt(1);

            if (inte_already_borrow == max_can_borrow){
                System.out.println("[Error]: You have reached the limit of the number of borrowing books.\n");
                librarian_operation(conn);
                return;
            }

            PreparedStatement stmt_borrow_statement=conn.prepareStatement("INSERT INTO borrow (libuid, callnum, copynum, checkout, return_date) VALUES (?, ?, ?, ?, NULL)");

            Date present_time = new java.sql.Date(new java.util.Date().getTime());
            stmt_borrow_statement.setString(1, userid);
            stmt_borrow_statement.setString(2, callnum);
            stmt_borrow_statement.setInt(3, copynumber);
            stmt_borrow_statement.setDate(4, present_time);
            stmt_borrow_statement.execute();

            System.out.println("Book borrowing performed successfully.\n");

        }catch(Exception excp){
            System.out.println("[Error]: Failed to borrow book.\n");
            librarian_operation(conn);
        }
        librarian_operation(conn);
    }

    public static void book_returning(Connection conn) throws SQLException, IOException {
        try{
            System.out.print("Enter The User ID: ");
            Scanner input = new Scanner(System.in);
            String userid=input.nextLine();

            PreparedStatement checkid = conn.prepareStatement("SELECT COUNT(*) FROM libuser where libuid = ?");
            checkid.setString(1, userid);
            ResultSet idresult = checkid.executeQuery();
            idresult.next();
            if (idresult.getInt(1) == 0) {
                System.out.println("[Error]: Sorry, the user does not exist.\n");
                librarian_operation(conn);
            }

            System.out.print("Enter The Call Number: ");
            String callnum=input.nextLine();

            System.out.print("Enter The Copy Number: ");
            int copynumber = 0;
            try{
                copynumber=input.nextInt();
            } catch(Exception ex) {
                System.out.println("[Error]: Invalid copy number.\n");
                librarian_operation(conn);
            }

            PreparedStatement stmt_copy = conn.prepareStatement("SELECT COUNT(*) FROM copy WHERE callnum = ? AND copynum = ?");
            stmt_copy.setString(1, callnum);
            stmt_copy.setInt(2, copynumber);
            ResultSet result=stmt_copy.executeQuery();
            result.next();

            int bookcounter=result.getInt(1);

            if (bookcounter == 0) {
                System.out.println("[Error]: No matching book.\n");
                librarian_operation(conn);
            }

            System.out.printf("Enter Your Rating of the Book: ");

            Float rating;
            while (true) {
                rating = input.nextFloat();
                if (rating < 0 || rating > 10) {
                    System.out.println("[Error]: The range of rating is 0 to 10. Please try again.\n");
                    librarian_operation(conn);
                } else {
                    break;
                }
            }

            PreparedStatement stmt_exceed_or_not=conn.prepareStatement("SELECT COUNT(*) FROM borrow WHERE callnum=? AND copynum =? AND return_date is NULL AND libuid = ?");

            stmt_exceed_or_not.setString(1, callnum);
            stmt_exceed_or_not.setInt(2, copynumber);
            stmt_exceed_or_not.setString(3, userid);

            ResultSet res_exceed_or_not = stmt_exceed_or_not.executeQuery();

            res_exceed_or_not.next();

            if (res_exceed_or_not.getInt(1) == 0) {
                System.out.println("[Error]: No such record in the system.\n");
                librarian_operation(conn);
            }

            PreparedStatement stmt_return_book = conn.prepareStatement("UPDATE borrow SET return_date = ? where libuid = ? and callnum = ? and copynum= ?");

            // Update return date
            Date present_time = new java.sql.Date(new java.util.Date().getTime());
            stmt_return_book.setDate(1, present_time);
            stmt_return_book.setString(2, userid);
            stmt_return_book.setString(3, callnum);
            stmt_return_book.setInt(4,copynumber);
            stmt_return_book.executeUpdate();

            // Select rating and tborrowed
            PreparedStatement get_book_rating=conn.prepareStatement("SELECT rating, tborrowed FROM book WHERE callnum = ?");
            get_book_rating.setString(1, callnum);
            ResultSet res_book_rating=get_book_rating.executeQuery();
            res_book_rating.next();

            Float new_book_rating = (res_book_rating.getFloat(1) * res_book_rating.getInt(2) + rating) / (res_book_rating.getInt(2) + 1);

            // Increase the number of tborrowed
            int new_book_beingborrow = res_book_rating.getInt(2) + 1;
            PreparedStatement stmt_update_rating_n_tborrow=conn.prepareStatement("UPDATE book SET tborrowed = ? where callnum = ?");
            stmt_update_rating_n_tborrow.setInt(1, new_book_beingborrow);
            stmt_update_rating_n_tborrow.setString(2, callnum);
            stmt_update_rating_n_tborrow.executeUpdate();

            // Update new rating
            new_book_rating = ((float)(Math.round(new_book_rating*1000)) / 1000); // Three decimal places
            PreparedStatement stmt_update_book_rating = conn.prepareStatement("UPDATE book SET rating = ? where callnum = ?");
            stmt_update_book_rating.setFloat(1, new_book_rating);
            stmt_update_book_rating.setString(2, callnum);
            stmt_update_book_rating.executeUpdate();

            System.out.println("New rating: " + new_book_rating);
            System.out.println("The number of times borrowed: " + new_book_beingborrow);
            System.out.println("Book returning performed successfully.\n");
            librarian_operation(conn);
        }catch(Exception excp){
            System.out.println("[Error] Wrong format. Please try again.\n");
        }
    }

    public static void list_all_unreturned_books(Connection conn) throws SQLException, IOException {
        Scanner input = new Scanner(System.in);
        Date date_1 = null;
        Date date_2 = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try{
            while (true){
                System.out.print("Type in the starting date [dd/mm/yyyy]: ");
                String inputdate=input.next();
                try{
                    date_1 = new java.sql.Date(dateFormat.parse(inputdate).getTime());
                } catch(Exception ex){
                    System.out.println("[Error]: Wrong date format. Please ensure that the format like dd/mm/yyyy\n");
                    librarian_operation(conn);
                }
                System.out.print("Type in the ending date [dd/mm/yyyy]: ");

                String inputdate_2=input.next();
                try{
                    date_2 = new java.sql.Date(dateFormat.parse(inputdate_2).getTime());
                } catch(Exception ex){
                    System.out.println("[Error]: Wrong date format. Please ensure that the format like dd/mm/yyyy\n");
                    librarian_operation(conn);
                }

                if (date_1.compareTo(date_2) < 0 || date_1.compareTo(date_2) == 0) {
                    break;
                } else if (date_1.compareTo(date_2) > 0){
                    System.out.println("[Error]: Wrong date order. Please make sure to enter the smallest date first.\n");
                    librarian_operation(conn);
                }
            }

            System.out.println("List of UnReturned Book:");
            System.out.println("| LibUID | CallNum | CopyNum | Checkout |");

            PreparedStatement stmt_unreturn = conn.prepareStatement("select libuid, callnum, copynum, checkout from borrow where checkout between ? and ? and return_date is NULL ORDER BY checkout DESC");

            stmt_unreturn.setDate(1, date_1);
            stmt_unreturn.setDate(2, date_2);

            ResultSet result_unreturn=stmt_unreturn.executeQuery();

            while (result_unreturn.next() == true){
                System.out.printf("| " + result_unreturn.getString(1) + " | " + result_unreturn.getString(2) + " | " + result_unreturn.getString(3) + " | " + dateFormat.format(result_unreturn.getDate(4)) + " |\n");
            }

            System.out.println("End of Query\n");
            librarian_operation(conn);
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
}
