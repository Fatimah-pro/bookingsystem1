package bookingsystem1; // Package declaration

import javax.swing.*; // Swing GUI components
import java.awt.*; // Layouts and colors
import java.awt.event.ActionEvent; // Action events for buttons
import java.awt.event.ActionListener; // Listen to button clicks

// --- CUSTOM EXCEPTION ---
// Exception thrown when trying to book an already booked seat
class SeatAlreadyBookedException extends Exception {
    public SeatAlreadyBookedException(String message) {
        super(message); // Pass message to Exception superclass
    }
}

// --- MODEL ---
// Handles seat data and booking logic
class BookingModel {
    private boolean[][] seats; // true = booked, false = available
    private final int ROWS = 10; // Rows A-J
    private final int COLS = 25; // Columns 1-25

    // Constructor: initialize all seats as available
    public BookingModel() {
        seats = new boolean[ROWS][COLS]; // Default false = available
    }

    // Check if a specific seat is booked
    public boolean isSeatBooked(int row, int col) {
        return seats[row][col]; // Return true if booked
    }

    // Book a specific seat
    public void bookSeat(int row, int col) throws SeatAlreadyBookedException {
        if (seats[row][col]) { // If already booked
            throw new SeatAlreadyBookedException(
                    "Seat " + (char)('A' + row) + (col + 1) + " is already booked!"
            );
        }
        seats[row][col] = true; // Mark as booked
    }

    // Get total rows
    public int getRows() { return ROWS; }

    // Get total columns
    public int getCols() { return COLS; }
}

// --- VIEW ---
// GUI for displaying seats
class BookingView extends JFrame {
    private JButton[][] seatButtons; // 2D array of seat buttons
    private JPanel seatPanel; // Panel to hold buttons

    // Constructor: build the GUI
    public BookingView(BookingModel model) {
        setTitle("Digital Box Office System"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit on close
        setSize(1000, 500); // Window size
        setLayout(new BorderLayout()); // Layout manager

        // Panel with grid layout for seats
        seatPanel = new JPanel(new GridLayout(model.getRows(), model.getCols()));
        seatButtons = new JButton[model.getRows()][model.getCols()]; // Initialize button array

        char rowLabel = 'A'; // Start labeling rows
        for (int i = 0; i < model.getRows(); i++) { // Loop through rows
            for (int j = 0; j < model.getCols(); j++) { // Loop through columns
                JButton btn = new JButton(rowLabel + String.valueOf(j + 1)); // Seat label
                btn.setBackground(Color.GREEN); // Green = available
                btn.setOpaque(true); // Make color visible
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Border
                seatButtons[i][j] = btn; // Store button
                seatPanel.add(btn); // Add button to panel
            }
            rowLabel++; // Next row
        }

        add(new JLabel("Screen This Way", SwingConstants.CENTER), BorderLayout.NORTH); // Top label
        add(seatPanel, BorderLayout.CENTER); // Add panel to center
        setVisible(true); // Show GUI
    }

    // Getter for a specific seat button
    public JButton getSeatButton(int row, int col) {
        return seatButtons[row][col];
    }
}

// --- SeatState ---
// Tracks the label and status of a seat for logging
class SeatState {
    private String label; // Seat label e.g., "E12"
    private boolean booked; // true = booked, false = not booked

    public SeatState(String label, boolean booked) {
        this.label = label; // Initialize label
        this.booked = booked; // Initialize booked status
    }

    public String getLabel() { return label; } // Get seat label
    public boolean isBooked() { return booked; } // Check if booked
    public void setBooked(boolean booked) { this.booked = booked; } // Update status

    // Return YES if booked, NO if not booked
    public String getStatus() { return booked ? "YES" : "NO"; }
}

// --- Logger ---
// Simple logger for debug messages
class Logger {
    public void debug(String message) {
        System.out.println("[DEBUG] " + message); // Print to console
    }
}

// --- CONTROLLER ---
// Handles user interactions
class BookingController {
    private BookingModel model; // Model reference
    private BookingView view;   // View reference
    private Logger logger = new Logger(); // Logger object

    public BookingController(BookingModel model, BookingView view) {
        this.model = model;
        this.view = view;
        initController(); // Initialize button listeners
    }

    // Add action listeners to all seat buttons
    private void initController() {
        for (int i = 0; i < model.getRows(); i++) {
            for (int j = 0; j < model.getCols(); j++) {
                final int row = i; // Row index
                final int col = j; // Column index
                final JButton seatButton = view.getSeatButton(row, col); // Button reference

                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        // Step 1: Create SeatState object for logging
                        final String seatLabel = ((char)('A' + row)) + String.valueOf(col + 1);
                        final SeatState seatState = new SeatState(seatLabel, model.isSeatBooked(row, col));

                        try {
                            if (!model.isSeatBooked(row, col)) {
                                // --- Booking logic ---
                                model.bookSeat(row, col);        // Mark seat booked in model
                                seatButton.setBackground(Color.RED); // Change button to red
                                JOptionPane.showMessageDialog(view,
                                        "Seat " + seatLabel + " booked successfully!"); // Show pop-up
                                
                                seatState.setBooked(true); // Update SeatState
                                logger.debug("Booking " + seatState.getLabel() + " status: " + seatState.getStatus()); 
                                // Logs YES after booking

                            } else {
                                // --- Already booked seat ---
                                JOptionPane.showMessageDialog(view,
                                        "Seat " + seatLabel + " is already booked!", "Error",
                                        JOptionPane.ERROR_MESSAGE); // Pop-up error
                                
                                seatState.setBooked(false); // Update SeatState
                                logger.debug("Booking " + seatState.getLabel() + " status: " + seatState.getStatus());
                                // Logs NO for already booked
                            }
                        } catch (SeatAlreadyBookedException ex) {
                            ex.printStackTrace(); // Print exception
                        }
                    }
                });
            }
        }
    }
}

// --- MAIN APPLICATION ---
public class bookingsystem1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookingModel model = new BookingModel(); // Create model
            BookingView view = new BookingView(model); // Create view
            new BookingController(model, view); // Initialize controller
        });
    }
}
