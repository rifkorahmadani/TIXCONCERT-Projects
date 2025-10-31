import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.swing.*;

public class MenuKonser extends JFrame implements ActionListener {

    private static final Logger logger = Logger.getLogger(MenuKonser.class.getName());
    
    private String loggedInUserName;
    private String loggedInUserEmail;
    private String loggedInUserPhone;

    public MenuKonser(String userName, String userEmail, String userPhone) {
        this.loggedInUserName = userName;
        this.loggedInUserEmail = userEmail;
        this.loggedInUserPhone = userPhone;

        setTitle("Pilih Konser");
        setSize(620, 420); //ukuran jndela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new java.awt.Color(179, 207, 194));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new java.awt.Color(179, 207, 194));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        
        JLabel welcomeLabel = new JLabel("Halo, " + loggedInUserName + "! Silahkan pilih, untuk memesan tiket konsernya:");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(welcomeLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel concertsGridPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        concertsGridPanel.setBackground(new java.awt.Color(179, 207, 194));
        concertsGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //daftar gambar-gambar yang untuk di panggil dari folder images
        addConcertCard(concertsGridPanel, "Maroon 5", "images/maroon5.jpg");
        addConcertCard(concertsGridPanel, "Bruno Mars", "images/brunomars.jpg");
        addConcertCard(concertsGridPanel, "Billie Eilish", "images/BillieEilish.jpg");
        addConcertCard(concertsGridPanel, "Adele", "images/adele.jpg");
        addConcertCard(concertsGridPanel, "Rich Brian", "images/richbrian.jpg");
        addConcertCard(concertsGridPanel, "Ed Sheeran", "images/edsheeran.jpg");
        mainPanel.add(concertsGridPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new java.awt.Color(179, 207, 194));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JButton backButton = new JButton("Keluar");
        backButton.setBackground(new java.awt.Color(255, 192, 0));
        backButton.setActionCommand("Logout");
        backButton.addActionListener(this);
        
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        backButtonPanel.setBackground(new java.awt.Color(179, 207, 194));
        backButtonPanel.add(backButton);
        footerPanel.add(backButtonPanel, BorderLayout.WEST);
        
        JButton historyButton = new JButton("Riwayat Pesanan");
        historyButton.setBackground(new java.awt.Color(255, 192, 0));
        historyButton.addActionListener(this);
        
        JPanel historyButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        historyButtonPanel.setBackground(new java.awt.Color(179, 207, 194));
        historyButtonPanel.add(historyButton);
        footerPanel.add(historyButtonPanel, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private void addConcertCard(JPanel parentPanel, String concertName, String imagePath) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new java.awt.Color(179, 207, 194));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        ImageIcon originalIcon = new ImageIcon(imagePath);
        JLabel imageLabel;
        
        if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image image = originalIcon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(image));
        } else {
            logger.warning("Gagal memuat gambar: " + imagePath);
            imageLabel = new JLabel("Gambar tidak ditemukan", SwingConstants.CENTER);
        }
        cardPanel.add(imageLabel, BorderLayout.CENTER);

        JButton selectButton = new JButton(concertName);
        selectButton.setBackground(new java.awt.Color(255, 192, 0));
        selectButton.setActionCommand(concertName);
        selectButton.addActionListener(this);
        selectButton.setFocusPainted(false);
        cardPanel.add(selectButton, BorderLayout.SOUTH);

        parentPanel.add(cardPanel);
    }

    private boolean checkIfUserHasOrders() {
        boolean hasOrders = false;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");
             PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM pesanan WHERE user_email = ?")) {
            
            pst.setString(1, loggedInUserEmail);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    hasOrders = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat memeriksa riwayat pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return hasOrders;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Logout".equals(command)) {
            dispose();
            new Main().setVisible(true);
        } else if ("Riwayat Pesanan".equals(command)) {
            if (checkIfUserHasOrders()) {
                new DaftarPesanan(loggedInUserName, loggedInUserEmail, loggedInUserPhone);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Anda Belum Memiliki Pesanan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            new Pesanan(loggedInUserName, loggedInUserEmail, loggedInUserPhone, command).setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuKonser("Nama Pengguna Test", "test@example.com", "08123456789").setVisible(true));
    }
}