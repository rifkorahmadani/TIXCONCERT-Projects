import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class DaftarAkun extends JFrame {
    // bahan UI
    private JTextField tfNama, tfEmail, tfTelepon;
    private JPasswordField pfPassword, pfKonfirmasi;
    private JButton btnDaftar, btnKembali;
    private JPanel panel;

    public DaftarAkun() {
        setTitle("Form Registrasi");
        setSize(620, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(179, 207, 194)); //warna dari hijau toska

        JLabel lblJudul = new JLabel("TIKET KONSER MUSIK");
        lblJudul.setFont(new Font("Bauhaus 93", Font.PLAIN, 36));
        lblJudul.setBounds(143, 30, 400, 40);
        panel.add(lblJudul);

        JLabel lblNama = new JLabel("Nama Lengkap:");
        lblNama.setBounds(165, 70, 100, 25);
        panel.add(lblNama);

        tfNama = new JTextField();
        tfNama.setBounds(275, 70, 180, 27);
        panel.add(tfNama);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(165, 110, 100, 25);
        panel.add(lblEmail);

        tfEmail = new JTextField();
        tfEmail.setBounds(275, 110, 180, 27);
        panel.add(tfEmail);

        JLabel lblTelepon = new JLabel("No. Telepon:");
        lblTelepon.setBounds(165, 150, 100, 25);
        panel.add(lblTelepon);

        tfTelepon = new JTextField();
        tfTelepon.setBounds(275, 150, 180, 27);
        panel.add(tfTelepon);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(165, 190, 100, 25);
        panel.add(lblPassword);

        pfPassword = new JPasswordField();
        pfPassword.setBounds(275, 190, 180, 27);
        panel.add(pfPassword);

        JLabel lblKonfirmasi = new JLabel("Konfirmasi:");
        lblKonfirmasi.setBounds(165, 230, 100, 25);
        panel.add(lblKonfirmasi);

        pfKonfirmasi = new JPasswordField();
        pfKonfirmasi.setBounds(275, 230, 180, 27); 
        panel.add(pfKonfirmasi);

        btnKembali = new JButton("Kembali");
        btnKembali.setBounds(165, 280, 100, 30);
        btnKembali.setBackground(new Color(255, 192, 0));
        panel.add(btnKembali);
        
        btnDaftar = new JButton("Daftar Sekarang");
        btnDaftar.setBounds(275, 280, 180, 30);
        btnDaftar.setBackground(new Color(255, 192, 0));
        panel.add(btnDaftar);

        add(panel);

        btnDaftar.addActionListener(e -> prosesRegistrasi());
        
        btnKembali.addActionListener(e -> {
            new Main().setVisible(true);
            this.dispose();
        });
    }

    private void prosesRegistrasi() {
        String nama = tfNama.getText().trim();
        String email = tfEmail.getText().trim();
        String noTelp = tfTelepon.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();
        String konfirmasi = new String(pfKonfirmasi.getPassword()).trim();

        if (nama.isEmpty() || email.isEmpty() || noTelp.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!password.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(this, "Password dan konfirmasi tidak sama!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");
             PreparedStatement pst = conn.prepareStatement("INSERT INTO users (nama, email, telepon, password) VALUES (?, ?, ?, ?)")) {
            
            pst.setString(1, nama);
            pst.setString(2, email);
            pst.setString(3, noTelp);
            pst.setString(4, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pendaftaran berhasil! Silakan login.", "Message", JOptionPane.INFORMATION_MESSAGE);

            tfNama.setText("");
            tfEmail.setText("");
            tfTelepon.setText("");
            pfPassword.setText("");
            pfKonfirmasi.setText("");

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                JOptionPane.showMessageDialog(this, "Email sudah terdaftar. Silakan gunakan email lain.", "Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat pendaftaran: " + e.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DaftarAkun().setVisible(true));
    }
}