import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

public class Kursi extends JDialog {

    private final JPanel kursiCheckboxPanel;
    private final List<JCheckBox> seatCheckBoxes = new ArrayList<>();
    private String selectedSeats;
    // nomor kursi sesuai kategorinya
    private static final String[] VIP_SEATS = {"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10"};
    private static final String[] FESTIVAL_SEATS = {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "C1", "C2", "C3", "C4", "C5"};
    private static final String[] REGULER_SEATS = {"D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10", "E1", "E2", "E3", "E4", "E5"};

    public Kursi(JFrame parent, String zona, String currentSelection) {
        super(parent, "Pilih Kursi untuk Zona " + zona, true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        kursiCheckboxPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        updateSeatCheckboxes(zona, currentSelection);

        JScrollPane scrollPane = new JScrollPane(kursiCheckboxPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("OK");
        JButton btnBatal = new JButton("Batal");

        btnOk.addActionListener(e -> {
            selectedSeats = seatCheckBoxes.stream()
                .filter(JCheckBox::isSelected)
                .map(JCheckBox::getText)
                .collect(Collectors.joining(", "));
            dispose();
        });

        btnBatal.addActionListener(e -> {
            selectedSeats = null;
            dispose();
        });

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnOk);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateSeatCheckboxes(String zona, String currentSelection) {
        kursiCheckboxPanel.removeAll();
        seatCheckBoxes.clear();

        String[] seatsToCreate = {};
        if ("VIP".equals(zona)) {
            seatsToCreate = VIP_SEATS;
        } else if ("Festival".equals(zona)) {
            seatsToCreate = FESTIVAL_SEATS;
        } else if ("Reguler".equals(zona)) {
            seatsToCreate = REGULER_SEATS;
        }

        List<String> previouslySelected = new ArrayList<>();
        if (currentSelection != null && !currentSelection.isEmpty()) {
            previouslySelected = Arrays.asList(currentSelection.split(",\\s*"));
        }

        for (String seat : seatsToCreate) {
            JCheckBox checkBox = new JCheckBox(seat);
            if (previouslySelected.contains(seat)) {
                checkBox.setSelected(true);
            }
            seatCheckBoxes.add(checkBox);
            kursiCheckboxPanel.add(checkBox);
        }

        kursiCheckboxPanel.revalidate();
        kursiCheckboxPanel.repaint();
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }
}