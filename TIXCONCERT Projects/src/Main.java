import java.sql.*;
import java.util.logging.Logger;
import javax.swing.*;

public class Main extends JFrame {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel titleLabel, emailLabel, passwordLabel, registerPromptLabel;
    private JPanel mainPanel;

    public Main() {
        initComponents(); // Inisialisasi semua komponen UI
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        setSize(620, 420);
        setLocationRelativeTo(null); // Menampilkan jendela di tengah layar

        mainPanel = new JPanel();
        mainPanel.setBackground(new java.awt.Color(179, 207, 194));
        mainPanel.setLayout(null); // Mengatur tata letak manual

        titleLabel = new JLabel("TIKET KONSER MUSIK");
        titleLabel.setFont(new java.awt.Font("Bauhaus 93", 0, 36));
        titleLabel.setBounds(143, 30, 400, 40);
        mainPanel.add(titleLabel);

        emailLabel = new JLabel("Email :");
        emailLabel.setBounds(100, 100, 100, 25);
        mainPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(200, 100, 220, 30);
        mainPanel.add(emailField);

        passwordLabel = new JLabel("Password :");
        passwordLabel.setBounds(100, 140, 100, 25);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 140, 220, 30);
        mainPanel.add(passwordField);

        loginButton = new JButton("Masuk");
        loginButton.setBackground(new java.awt.Color(255, 192, 0));
        loginButton.setBounds(200, 180, 220, 30);
        mainPanel.add(loginButton);

        registerPromptLabel = new JLabel("Belum Punya Akun? Silahkan Daftar di sini.");
        registerPromptLabel.setBounds(180, 230, 280, 20);
        mainPanel.add(registerPromptLabel);

        registerButton = new JButton("Daftar");
        registerButton.setBackground(new java.awt.Color(255, 192, 0));
        registerButton.setBounds(200, 260, 220, 30);
        mainPanel.add(registerButton);

        add(mainPanel);

        //saat tombol login diklik
        loginButton.addActionListener(e -> prosesLogin());

        //saat tombol daftar diklik
        registerButton.addActionListener(e -> {
            new DaftarAkun().setVisible(true);
            this.dispose(); // Tutup jendela login setelah buka jendela daftar
        });
    }

    private void prosesLogin() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        // Validasi input
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan Data Terlebih dahulu.", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Koneksi ke database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");

            // Query untuk mencocokkan email dan password
            String query = "SELECT nama, email, telepon FROM users WHERE email=? AND password=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Jika login berhasil, ambil data user
                String userName = rs.getString("nama");
                String userEmail = rs.getString("email");
                String userPhone = rs.getString("telepon");

                // Buka menu konser dan tutup jendela login
                this.dispose();
                new MenuKonser(userName, userEmail, userPhone).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email atau Password salah!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            // Penanganan kesalahan SQL
            logger.severe("Koneksi database atau query gagal: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat login: " + ex.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            // Tutup koneksi dan resource database
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.severe("Gagal menutup resource database: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Jalankan aplikasi GUI
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
