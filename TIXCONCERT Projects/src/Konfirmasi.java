import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.*;

public class Konfirmasi extends JFrame implements ActionListener {
    
    private static final Logger logger = Logger.getLogger(Konfirmasi.class.getName());
    // bahan-bahan UI
    private int orderId;
    private String loggedInUserName, loggedInUserEmail, loggedInUserPhone;
    private JButton kembaliBtn, cetakBtn, transferBtn;
    private String kodeTiket = "";
    private String konserNama, namaPemesan, zona;
    private int jumlahTiket;
    private boolean sudahTransfer = false;

    public Konfirmasi(
        int orderId, String konserNama, String namaLengkap, String email,
        String noTelepon, String nik, int jumlahTiket, String zona,
        String totalHarga, String metodePembayaran,
        String userName, String userEmail, String userPhone
    ) {
        this.orderId = orderId;
        this.loggedInUserName = userName;
        this.loggedInUserEmail = userEmail;
        this.loggedInUserPhone = userPhone;
        this.konserNama = konserNama;
        this.namaPemesan = namaLengkap;
        this.zona = zona;
        this.jumlahTiket = jumlahTiket;

        setTitle("Konfirmasi & Tiket");
        setSize(620, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        Color backgroundColor = new Color(179, 207, 194);
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)); 
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Konfirmasi Pembayaran");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); 
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBackground(backgroundColor);
        
        Font labelFont = new Font("Arial", Font.BOLD, 14); 
        Font valueFont = new Font("Arial", Font.PLAIN, 14); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        row = addRow(dataPanel, "Nama Lengkap", namaLengkap, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "Email", email, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "No. Telepon", noTelepon, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "NIK", nik, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "Jumlah", jumlahTiket + " Tiket", row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "Zona", zona, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "Total Harga", "Rp " + totalHarga, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "Bank", metodePembayaran, row, labelFont, valueFont, gbc);
        row = addRow(dataPanel, "No. Rek", "0320012345", row, labelFont, valueFont, gbc);
        
        JPanel leftAlignWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftAlignWrapper.setBackground(backgroundColor);
        leftAlignWrapper.add(dataPanel);
        mainPanel.add(leftAlignWrapper, BorderLayout.CENTER);
        
        JPanel mainButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        mainButtonPanel.setBackground(backgroundColor);

        JPanel leftSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        leftSubPanel.setBackground(backgroundColor);

        JPanel transferSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        transferSubPanel.setBackground(backgroundColor);

        kembaliBtn = createStyledButton("Kembali");
        cetakBtn = createStyledButton("Cetak");
        transferBtn = createStyledButton("Saya Sudah Transfer");
        
        kembaliBtn.addActionListener(this);
        cetakBtn.addActionListener(this);
        transferBtn.addActionListener(this);
        
        leftSubPanel.add(kembaliBtn);
        leftSubPanel.add(cetakBtn);
        transferSubPanel.add(transferBtn);
        
        mainButtonPanel.add(leftSubPanel);
        mainButtonPanel.add(transferSubPanel);
        mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private int addRow(JPanel panel, String label, String value, int row, Font labelFont, Font valueFont, GridBagConstraints gbc) {
        gbc.gridx = 0; 
        gbc.gridy = row;
        JLabel lbl = new JLabel(label + " :");
        lbl.setFont(labelFont);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(value);
        val.setFont(valueFont);
        panel.add(val, gbc);

        return row + 1;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(255, 192, 0));
        button.setFocusPainted(false);
        return button;
    }
    
    private boolean updateStatusDiDatabase() {
        String sql = "UPDATE pesanan SET status = 'Sudah Bayar' WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException ex) {
            logger.severe("Database error while updating status: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status pembayaran di database.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == kembaliBtn) {
            dispose();
            new DaftarPesanan(loggedInUserName, loggedInUserEmail, loggedInUserPhone).setVisible(true);
        } else if (e.getSource() == transferBtn) {
            if (sudahTransfer) {
                JOptionPane.showMessageDialog(this, "Anda sudah membayar. Silakan cetak tiket Anda.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Sedang mengecek pembayaran...");

            if (updateStatusDiDatabase()) {
                sudahTransfer = true;
                kodeTiket = generateKodeTiket();
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil, pesanan telah dikonfirmasi.");
            } else {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan. Pembayaran tidak dapat dikonfirmasi.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cetakBtn) {
            if (!sudahTransfer) {
                JOptionPane.showMessageDialog(this, "Selesaikan pembayaran terlebih dahulu.");
            } else {
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(new JLabel("<html><b>Nama Konser:</b> " + konserNama + "</html>"));
                panel.add(new JLabel("<html><b>Nama Pemesan:</b> " + namaPemesan + "</html>"));
                panel.add(new JLabel("<html><b>Jumlah:</b> " + jumlahTiket + " Tiket</html>"));
                panel.add(new JLabel("<html><b>Zona:</b> " + zona + "</html>"));
                panel.add(new JLabel("<html><b>Kode Tiket:</b> " + kodeTiket + "</html>"));
                JOptionPane.showMessageDialog(this, panel, "Tiket Konser", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private String generateKodeTiket() {
        Random random = new Random();
        return "TK" + System.currentTimeMillis() % 10000 + "-" + String.format("%04d", random.nextInt(10000));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Konfirmasi(
            123, "Billie Eilish", "1", "1", "1", "1", 2, "Festival", "1.400.000", "Permata", "Test", "test@mail.com", "0123"));
    }
}