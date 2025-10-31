import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.*;

public class Pesanan extends JFrame implements ActionListener {

    private static final Logger logger = Logger.getLogger(Pesanan.class.getName());
     // Variabel untuk menyimpan data pengguna dan konser
    private String userNamePassed;
    private String userEmailPassed;
    private String userPhonePassed;
    private String concertName;
     // bahan UI
    private JTextField tfNamaLengkap, tfEmail, tfNoTelepon, tfNIK, tfJumlahTiket, tfTotalHarga, tfPilihanKursi;
    private JComboBox<String> cbZona;
    private JTextArea taInformasiKonser;
    private JRadioButton rbMandiri, rbBRI, rbBCA, rbBJB, rbBNI, rbPermata, rbCIMB, rbDanamon, rbBTN;
    private ButtonGroup bgMetodePembayaran;
    private JButton btnKembali, btnSimpanPesanan, btnPilihKursi;

    private Integer orderIdToUpdate = null;
    private boolean initiallyInUpdateMode = false;

    private static final String[] ZONA_OPTIONS = {"Pilih Zona", "VIP", "Festival", "Reguler"};
    // strukturnya untuk membuat pesanan baru
    public Pesanan(String userName, String userEmail, String userPhone, String concertName) {
        this.userNamePassed = userName;
        this.userEmailPassed = userEmail;
        this.userPhonePassed = userPhone;
        this.concertName = concertName;
        this.orderIdToUpdate = null;
        this.initiallyInUpdateMode = false;

        initComponents();
        setTitle("Detail Konser & Form Pemesanan");
        btnSimpanPesanan.setText("Simpan Pesanan");
    }
    // untuk mengedit pesanan yang sudah ada
    public Pesanan(String userName, String userEmailLogin, String userPhone, String concertName,
                             Integer orderId, String namaLengkap, String emailKontak, String noTeleponKontak,
                             String nik, String zona, int jumlahTiket, String noKursi, String metodePembayaran) {
        this.userNamePassed = userName;
        this.userEmailPassed = userEmailLogin;
        this.userPhonePassed = userPhone;
        this.concertName = concertName;
        this.orderIdToUpdate = orderId;
        this.initiallyInUpdateMode = true;

        initComponents(); // bahan UI untuk bagian update di daftarpesanan
        setTitle("Update Detail Pesanan Konser");
        btnSimpanPesanan.setText("Update Pesanan");

        tfNamaLengkap.setText(namaLengkap);
        tfEmail.setText(emailKontak);
        tfNoTelepon.setText(noTeleponKontak);
        tfNIK.setText(nik);
        cbZona.setSelectedItem(zona);
        tfPilihanKursi.setText(noKursi);
        updateTicketCountAndPrice();

        for (java.util.Enumeration<AbstractButton> buttons = bgMetodePembayaran.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.getText().equalsIgnoreCase(metodePembayaran)) {
                button.setSelected(true);
                break;
            }
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new java.awt.Color(179, 207, 194));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new java.awt.Color(179, 207, 194));
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.anchor = GridBagConstraints.NORTHWEST;
        gbcLeft.fill = GridBagConstraints.HORIZONTAL;

        gbcLeft.gridx = 0; gbcLeft.gridy = 0; gbcLeft.weightx = 1.0; gbcLeft.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(createSectionTitle("Informasi Konser"), gbcLeft);

        String tanggalKonser = "";
        String jamKonser = "";
        switch (this.concertName) {
            case "Maroon 5":
                tanggalKonser = "1 Juli 2025";
                jamKonser = "19.30 WIB - selesai";
                break;
            case "Bruno Mars":
                tanggalKonser = "14 September 2025";
                jamKonser = "16.00 WIB - selesai";
                break;
            case "Billie Eilish":
                tanggalKonser = "17 Agustus 2025";
                jamKonser = "16.30 WIB - selesai";
                break;
            case "Adele":
                tanggalKonser = "1 Desember 2025";
                jamKonser = "20.00 WIB - selesai";
                break;
            case "Rich Brian":
                tanggalKonser = "2 Oktober 2025";
                jamKonser = "15.00 WIB - selesai";
                break;
            case "Ed Sheeran":
                tanggalKonser = "8 November 2025";
                jamKonser = "19.45 WIB - selesai";
                break;
            default:
                tanggalKonser = "Segera Diumumkan";
                jamKonser = "Segera Diumumkan";
                break;
        }

        String infoKonserText = "Nama konser: " + this.concertName + "\n" +
                                "Tanggal: " + tanggalKonser + "\n" +
                                "Lokasi: Jakarta International Stadium\n" +
                                "Harga per Tiket: VIP        = 1.700.000\n" +
                                "                           Festival = 700.000\n" +
                                "                           Reguler = 300.000\n" +
                                "Jam: " + jamKonser;

        taInformasiKonser = new JTextArea(infoKonserText);
        taInformasiKonser.setEditable(false);
        taInformasiKonser.setLineWrap(true);
        taInformasiKonser.setWrapStyleWord(true);
        taInformasiKonser.setPreferredSize(new Dimension(280, 120));
        taInformasiKonser.setBorder(BorderFactory.createEtchedBorder());
        taInformasiKonser.setMargin(new Insets(5,5,5,5));
        
        gbcLeft.gridy = 1; gbcLeft.insets = new Insets(0, 5, 0, 5);
        leftPanel.add(taInformasiKonser, gbcLeft);

        JLabel lblPetaStadion = new JLabel();
        ImageIcon seatingMapIcon = new ImageIcon("images/Gambar Peta.jpg");
        if (seatingMapIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image scaledImage = seatingMapIcon.getImage().getScaledInstance(350, 300, Image.SCALE_SMOOTH);
            lblPetaStadion.setIcon(new ImageIcon(scaledImage));
        } else {
            lblPetaStadion.setText("Peta tidak ditemukan");
        }
        lblPetaStadion.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBackground(new java.awt.Color(179, 207, 194));
        mapContainer.add(lblPetaStadion, BorderLayout.CENTER);
        
        gbcLeft.gridy = 2; gbcLeft.insets = new Insets(10, 5, 0, 5);
        leftPanel.add(mapContainer, gbcLeft);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        mainPanel.add(leftPanel, gbc);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new java.awt.Color(179, 207, 194));
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.insets = new Insets(2, 5, 2, 5);
        
        final Dimension labelSize = new Dimension(100, 28);
        final Dimension fieldSize = new Dimension(220, 28);
        int yPos = 0;

        gbcRight.gridx = 0; gbcRight.gridy = yPos++; gbcRight.gridwidth = 2; gbcRight.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(createSectionTitle("Data Diri"), gbcRight);
        gbcRight.gridwidth = 1; gbcRight.insets = new Insets(2, 5, 2, 5);
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblNama = new JLabel("Nama Lengkap :"); 
        lblNama.setPreferredSize(labelSize); 
        rightPanel.add(lblNama, gbcRight);
        
        gbcRight.gridx = 1; 
        tfNamaLengkap = new JTextField(); 
        tfNamaLengkap.setPreferredSize(fieldSize); 
        rightPanel.add(tfNamaLengkap, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblEmail = new JLabel("Email Kontak :"); 
        lblEmail.setPreferredSize(labelSize); 
        rightPanel.add(lblEmail, gbcRight);
        
        gbcRight.gridx = 1; 
        tfEmail = new JTextField(); 
        tfEmail.setPreferredSize(fieldSize); 
        rightPanel.add(tfEmail, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblTelepon = new JLabel("No. Telepon :"); 
        lblTelepon.setPreferredSize(labelSize); 
        rightPanel.add(lblTelepon, gbcRight);
        
        gbcRight.gridx = 1; 
        tfNoTelepon = new JTextField(); 
        tfNoTelepon.setPreferredSize(fieldSize); 
        rightPanel.add(tfNoTelepon, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblNIK = new JLabel("NIK :"); 
        lblNIK.setPreferredSize(labelSize); 
        rightPanel.add(lblNIK, gbcRight);
        
        gbcRight.gridx = 1; 
        tfNIK = new JTextField(); 
        tfNIK.setPreferredSize(fieldSize); 
        rightPanel.add(tfNIK, gbcRight);
        yPos++;

        gbcRight.gridx = 0; gbcRight.gridy = yPos++; gbcRight.gridwidth = 2; gbcRight.insets = new Insets(15, 0, 5, 0);
        rightPanel.add(createSectionTitle("Pemilihan Kursi"), gbcRight);
        gbcRight.gridwidth = 1; gbcRight.insets = new Insets(2, 5, 2, 5);

        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblZona = new JLabel("Zona :"); 
        lblZona.setPreferredSize(labelSize); 
        rightPanel.add(lblZona, gbcRight);
        
        gbcRight.gridx = 1; 
        cbZona = new JComboBox<>(ZONA_OPTIONS); 
        cbZona.setPreferredSize(fieldSize); 
        rightPanel.add(cbZona, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblKursi = new JLabel("No. Kursi :"); 
        lblKursi.setPreferredSize(labelSize); 
        rightPanel.add(lblKursi, gbcRight);
        
        JPanel kursiSelectionPanel = new JPanel(new BorderLayout(5, 0));
        kursiSelectionPanel.setBackground(rightPanel.getBackground());
        tfPilihanKursi = new JTextField(); 
        tfPilihanKursi.setEditable(false); 
        tfPilihanKursi.setBackground(Color.WHITE);
        btnPilihKursi = new JButton("Pilih Kursi...");
        btnPilihKursi.addActionListener(this);
        kursiSelectionPanel.add(tfPilihanKursi, BorderLayout.CENTER);
        kursiSelectionPanel.add(btnPilihKursi, BorderLayout.EAST);
        kursiSelectionPanel.setPreferredSize(fieldSize);
        gbcRight.gridx = 1; 
        rightPanel.add(kursiSelectionPanel, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblJumlah = new JLabel("Jumlah Tiket :"); 
        lblJumlah.setPreferredSize(labelSize); 
        rightPanel.add(lblJumlah, gbcRight);
        
        gbcRight.gridx = 1; 
        tfJumlahTiket = new JTextField(); 
        tfJumlahTiket.setEditable(false); 
        tfJumlahTiket.setText("0"); 
        tfJumlahTiket.setPreferredSize(fieldSize); 
        rightPanel.add(tfJumlahTiket, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos; 
        JLabel lblHarga = new JLabel("Total Harga :"); 
        lblHarga.setPreferredSize(labelSize); 
        rightPanel.add(lblHarga, gbcRight);
        
        gbcRight.gridx = 1; 
        tfTotalHarga = new JTextField(); 
        tfTotalHarga.setEditable(false); 
        tfTotalHarga.setText("0"); 
        tfTotalHarga.setPreferredSize(fieldSize); 
        rightPanel.add(tfTotalHarga, gbcRight);
        yPos++;
        
        gbcRight.gridx = 0; gbcRight.gridy = yPos++; gbcRight.gridwidth = 2; gbcRight.insets = new Insets(15, 0, 5, 0);
        rightPanel.add(createSectionTitle("Metode Pembayaran"), gbcRight);
        
        JPanel metodePembayaranPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        metodePembayaranPanel.setBackground(new java.awt.Color(179, 207, 194));
        bgMetodePembayaran = new ButtonGroup();
        
        rbMandiri = createStyledRadioButton("Mandiri");
        rbBRI = createStyledRadioButton("BRI");
        rbBCA = createStyledRadioButton("BCA");
        rbBJB = createStyledRadioButton("BJB");
        rbBNI = createStyledRadioButton("BNI");
        rbPermata = createStyledRadioButton("Permata");
        rbCIMB = createStyledRadioButton("CIMB Niaga");
        rbDanamon = createStyledRadioButton("Danamon");
        rbBTN = createStyledRadioButton("BTN");

        JRadioButton[] rbs = { rbMandiri, rbBRI, rbBCA, rbBJB, rbBNI, rbPermata, rbCIMB, rbDanamon, rbBTN };
        for (JRadioButton rb : rbs) {
            bgMetodePembayaran.add(rb);
            metodePembayaranPanel.add(rb);
        }

        gbcRight.gridx = 0; gbcRight.gridy = yPos; gbcRight.gridwidth = 2; gbcRight.insets = new Insets(2, 5, 2, 5);
        rightPanel.add(metodePembayaranPanel, gbcRight);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        mainPanel.add(rightPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new java.awt.Color(179, 207, 194));
        
        btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new java.awt.Color(255, 192, 0));
        btnKembali.addActionListener(this);
        buttonPanel.add(btnKembali);
        
        btnSimpanPesanan = new JButton("Create Pesanan");
        btnSimpanPesanan.setBackground(new java.awt.Color(255, 192, 0));
        btnSimpanPesanan.addActionListener(this);
        buttonPanel.add(btnSimpanPesanan);
        
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0; 
        gbcButtons.gridy = 1;
        gbcButtons.gridwidth = 2;
        gbcButtons.anchor = GridBagConstraints.SOUTHEAST;
        mainPanel.add(buttonPanel, gbcButtons);

        GridBagConstraints gbcFiller = new GridBagConstraints();
        gbcFiller.gridy = 2;
        gbcFiller.weighty = 1.0;
        mainPanel.add(new JLabel(""), gbcFiller);

        add(mainPanel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setBackground(new java.awt.Color(179, 207, 194));
        rb.setOpaque(true);
        return rb;
    }
    
    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }
    
    private void updateTicketCountAndPrice() {
        String selectedKursiStr = tfPilihanKursi.getText();
        int count = 0;
        if (selectedKursiStr != null && !selectedKursiStr.isEmpty()) {
            count = selectedKursiStr.split(",").length;
        }
        tfJumlahTiket.setText(String.valueOf(count));
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        String selectedZona = (String) cbZona.getSelectedItem();
        long jumlahTiket = 0;
        
        try {
            jumlahTiket = Long.parseLong(tfJumlahTiket.getText());
        } catch (NumberFormatException e) { 
            // Abaikan saja jika tidak valid
        }

        double hargaPerTiket = 0.0;
        if (jumlahTiket > 0) {
            if ("VIP".equals(selectedZona)) {
                hargaPerTiket = 1_700_000.0;
            } else if ("Festival".equals(selectedZona)) {
                hargaPerTiket = 700_000.0;
            } else if ("Reguler".equals(selectedZona)) {
                hargaPerTiket = 300_000.0;
            }
        }
        
        double totalHarga = jumlahTiket * hargaPerTiket;
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        currencyFormat.setMaximumFractionDigits(0);
        tfTotalHarga.setText(currencyFormat.format(totalHarga));
    }

    private boolean isFormValid() {
        if (tfNamaLengkap.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty() ||
            tfNoTelepon.getText().trim().isEmpty() || tfNIK.getText().trim().isEmpty()) {
            return false;
        }
        if ("Pilih Zona".equals(cbZona.getSelectedItem())) {
            return false;
        }
        if (tfPilihanKursi.getText().trim().isEmpty()) {
            return false;
        }
        return bgMetodePembayaran.getSelection() != null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnKembali) {
            dispose();
            if (this.initiallyInUpdateMode) {
                new DaftarPesanan(userNamePassed, userEmailPassed, userPhonePassed).setVisible(true);
            } else {
                new MenuKonser(userNamePassed, userEmailPassed, userPhonePassed).setVisible(true);
            }
        } else if (e.getSource() == btnPilihKursi) {
            String zona = (String) cbZona.getSelectedItem();
            if ("Pilih Zona".equals(zona)) {
                JOptionPane.showMessageDialog(this, "Silakan pilih Zona terlebih dahulu!");
                return;
            }
            
            Kursi dialog = new Kursi(this, zona, tfPilihanKursi.getText());
            dialog.setVisible(true);
            
            String hasilPilihan = dialog.getSelectedSeats();
            if (hasilPilihan != null) {
                tfPilihanKursi.setText(hasilPilihan);
                updateTicketCountAndPrice();
            }
        } else if (e.getSource() == btnSimpanPesanan) {
            if (!isFormValid()) {
                JOptionPane.showMessageDialog(this, "Silakan lengkapi semua data terlebih dahulu.");
                return;
            }

            String namaLengkap = tfNamaLengkap.getText().trim();
            String emailKontak = tfEmail.getText().trim();
            String noTelepon = tfNoTelepon.getText().trim();
            String nik = tfNIK.getText().trim();
            String selectedZona = (String) cbZona.getSelectedItem();
            int jumlahTiket = Integer.parseInt(tfJumlahTiket.getText());
            String selectedNoKursi = tfPilihanKursi.getText();
            String selectedMetodePembayaran = "";
            
            for (java.util.Enumeration<AbstractButton> buttons = bgMetodePembayaran.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    selectedMetodePembayaran = button.getText();
                    break;
                }
            }
            
            double totalHarga = 0.0;
            try {
                NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                totalHarga = format.parse(tfTotalHarga.getText()).doubleValue();
            } catch (ParseException ex) {
                logger.severe("Gagal parse total harga. Error: " + ex.getMessage());
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345")) {
                if (orderIdToUpdate != null) {
                    String query = "UPDATE pesanan SET nama_lengkap=?, email=?, no_telepon=?, nik=?, zona=?, jumlah_tiket=?, nomor_kursi=?, total_harga=?, metode_pembayaran=? WHERE id=? AND user_email=?";
                    try (PreparedStatement pst = conn.prepareStatement(query)) {
                        pst.setString(1, namaLengkap); 
                        pst.setString(2, emailKontak);
                        pst.setString(3, noTelepon); 
                        pst.setString(4, nik);
                        pst.setString(5, selectedZona); 
                        pst.setInt(6, jumlahTiket);
                        pst.setString(7, selectedNoKursi); 
                        pst.setDouble(8, totalHarga);
                        pst.setString(9, selectedMetodePembayaran); 
                        pst.setInt(10, orderIdToUpdate);
                        pst.setString(11, userEmailPassed);
                        
                        int rowsAffected = pst.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Pesanan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            new DaftarPesanan(userNamePassed, userEmailPassed, userPhonePassed).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Gagal mengupdate pesanan.", "Error Update", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    String query = "INSERT INTO pesanan (user_email, nama_lengkap, email, no_telepon, nik, konser_nama, zona, jumlah_tiket, nomor_kursi, total_harga, metode_pembayaran, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = conn.prepareStatement(query)) {
                        pst.setString(1, userEmailPassed); 
                        pst.setString(2, namaLengkap);
                        pst.setString(3, emailKontak); 
                        pst.setString(4, noTelepon);
                        pst.setString(5, nik); 
                        pst.setString(6, concertName);
                        pst.setString(7, selectedZona); 
                        pst.setInt(8, jumlahTiket);
                        pst.setString(9, selectedNoKursi); 
                        pst.setDouble(10, totalHarga);
                        pst.setString(11, selectedMetodePembayaran);
                        pst.setString(12, "Belum Bayar");
                        pst.executeUpdate();
                        
                        JOptionPane.showMessageDialog(this, "Pesanan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        resetForm();
                    }
                }
            } catch (SQLException ex) {
                logger.severe("Database error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        tfNamaLengkap.setText(""); 
        tfEmail.setText("");
        tfNoTelepon.setText(""); 
        tfNIK.setText("");
        tfPilihanKursi.setText(""); 
        tfJumlahTiket.setText("0");
        tfTotalHarga.setText("0");
        cbZona.setSelectedItem("Pilih Zona");
        rbMandiri.setSelected(true);
        
        if (this.orderIdToUpdate != null) {
            this.orderIdToUpdate = null;
            btnSimpanPesanan.setText("Simpan Pesanan");
            setTitle("Detail Konser & Form Pemesanan");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Pesanan("User", "user@mail.com", "123", "Konser Keren"));
    }
}