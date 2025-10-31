import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class DaftarPesanan extends JFrame implements ActionListener {

    private static final Logger logger = Logger.getLogger(DaftarPesanan.class.getName());

    private String loggedInUserEmail;
    private String loggedInUserName;
    private String loggedInUserPhone;

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton kembaliBtn, updateBtn, deleteBtn, bayarBtn;

    private final List<List<Object>> orderDataMap = new ArrayList<>();

    // private static final String TANGGAL_KONSER_DEFAULT = "14 September 2024";
    private static final String LOKASI_KONSER_DEFAULT = "JIS, Jakarta";

    public DaftarPesanan(String userName, String userEmail, String userPhone) {
        this.loggedInUserName = userName;
        this.loggedInUserEmail = userEmail;
        this.loggedInUserPhone = userPhone;

        setTitle("Daftar Pesanan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 420); //ukuran jendela
        setLocationRelativeTo(null);

        Color backgroundColor = new java.awt.Color(179, 207, 194);
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] visibleColumnNames = {
            "", "No.", "Nama Konser", "Detail Tiket", "Total Harga", "Status Pembayaran"
        };
        
        tableModel = new DefaultTableModel(visibleColumnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.getTableHeader().setBackground(new java.awt.Color(200, 230, 200));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setFillsViewportHeight(true);
        orderTable.setBackground(backgroundColor);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        orderTable.setRowSorter(sorter);

        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(30);
        columnModel.getColumn(1).setPreferredWidth(30);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(250);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        buttonPanel.setBackground(backgroundColor);
        
        JPanel leftSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        leftSubPanel.setBackground(backgroundColor);
        
        JPanel bayarSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bayarSubPanel.setBackground(backgroundColor);
        
        kembaliBtn = createStyledButton("Kembali");
        updateBtn = createStyledButton("Update");
        deleteBtn = createStyledButton("Delete");
        bayarBtn = createStyledButton("Bayar");

        kembaliBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        bayarBtn.addActionListener(this);

        leftSubPanel.add(kembaliBtn);
        leftSubPanel.add(updateBtn);
        leftSubPanel.add(deleteBtn);
        bayarSubPanel.add(bayarBtn);

        buttonPanel.add(leftSubPanel);
        buttonPanel.add(bayarSubPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        loadOrders();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new java.awt.Color(255, 192, 0));
        return button;
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        orderDataMap.clear();
        
        String query = "SELECT * FROM pesanan WHERE user_email = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, loggedInUserEmail);
            try (ResultSet rs = pst.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    List<Object> fullRowData = new ArrayList<>();
                    fullRowData.add(false); 
                    fullRowData.add(rs.getInt("id"));
                    fullRowData.add(no++); 
                    fullRowData.add(rs.getString("nama_lengkap"));
                    fullRowData.add(rs.getString("email")); 
                    fullRowData.add(rs.getString("no_telepon"));
                    fullRowData.add(rs.getString("nik")); 
                    fullRowData.add(rs.getString("konser_nama"));
                    fullRowData.add(rs.getString("zona")); 
                    fullRowData.add(rs.getInt("jumlah_tiket"));
                    fullRowData.add(rs.getString("nomor_kursi")); 
                    fullRowData.add(rs.getDouble("total_harga"));
                    fullRowData.add(rs.getString("status")); 
                    fullRowData.add(rs.getString("metode_pembayaran"));
                    orderDataMap.add(fullRowData);

                    String detailTiket = String.format("%s (%d Tiket) - Kursi: %s", 
                        rs.getString("zona"), rs.getInt("jumlah_tiket"), rs.getString("nomor_kursi"));
                    String hargaFormatted = String.format("Rp %,.0f", rs.getDouble("total_harga"));
                    tableModel.addRow(new Object[]{ false, fullRowData.get(2), fullRowData.get(7), detailTiket, hargaFormatted, fullRowData.get(12) });
                }
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{false, "-", "Tidak ada pesanan", "-", "-", "-"});
                orderTable.setEnabled(false);
                updateBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                bayarBtn.setEnabled(false);
            } else {
                orderTable.setEnabled(true);
                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                bayarBtn.setEnabled(true);
            }
        } catch (SQLException ex) {
            logger.severe("Gagal memuat pesanan: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == kembaliBtn) {
            dispose();
            new MenuKonser(loggedInUserName, loggedInUserEmail, loggedInUserPhone).setVisible(true);
            return;
        }
        
        int selectedViewRow = orderTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih terlebih dahulu daftar pesanan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = orderTable.convertRowIndexToModel(selectedViewRow);
        
        if ("-".equals(tableModel.getValueAt(modelRow, 1).toString())) {
             JOptionPane.showMessageDialog(this, "Silakan pilih pesanan yang valid.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<Object> data = orderDataMap.get(modelRow);
        
        int orderId = (int) data.get(1);
        String currentStatus = (String) data.get(12);
        
        if (e.getSource() == updateBtn) {
            if ("Sudah Bayar".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Pesanan sudah dibayar! Tidak bisa melakukan pembaruan data.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new Pesanan(loggedInUserName, loggedInUserEmail, loggedInUserPhone, (String)data.get(7),
                                  orderId, (String)data.get(3), (String)data.get(4), (String)data.get(5), (String)data.get(6),
                                  (String)data.get(8), (int)data.get(9), (String)data.get(10), (String)data.get(13)).setVisible(true);
            dispose();

        } else if (e.getSource() == deleteBtn) {
            if (performDeleteOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Pesanan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
                
                if (tableModel.getRowCount() > 0 && "-".equals(tableModel.getValueAt(0, 1).toString())) {
                    dispose();
                    new MenuKonser(loggedInUserName, loggedInUserEmail, loggedInUserPhone).setVisible(true);
                }
            }
        } else if (e.getSource() == bayarBtn) {
            String konserNama = (String) data.get(7);
            String namaLengkap = (String) data.get(3);
            int jumlahTiket = (int) data.get(9);
            String zona = (String) data.get(8);
            
            if ("Sudah Bayar".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Pesanan sudah dibayar!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                
                Random random = new Random();
                String kodeTiket = "TK" + orderId + "-" + String.format("%04d", random.nextInt(10000));
                
                JPanel ticketPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                ticketPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                ticketPanel.add(new JLabel("<html><b>Nama Konser:</b> " + konserNama + "</html>"));
                ticketPanel.add(new JLabel("<html><b>Nama Pemesan:</b> " + namaLengkap + "</html>"));
                ticketPanel.add(new JLabel("<html><b>Jumlah:</b> " + jumlahTiket + " Tiket</html>"));
                ticketPanel.add(new JLabel("<html><b>Zona:</b> " + zona + "</html>"));
                // ticketPanel.add(new JLabel("<html><b>Tanggal:</b> " + TANGGAL_KONSER_DEFAULT + "</html>"));
                ticketPanel.add(new JLabel("<html><b>Lokasi:</b> " + LOKASI_KONSER_DEFAULT + "</html>"));
                ticketPanel.add(new JLabel("<html><b>Kode Tiket:</b> " + kodeTiket + "</html>"));
                JOptionPane.showMessageDialog(this, ticketPanel, "Tiket Konser", JOptionPane.INFORMATION_MESSAGE);
            } else { 
                String emailKontak = (String) data.get(4);
                String noTeleponKontak = (String) data.get(5);
                String nik = (String) data.get(6);
                String totalHargaDisplay = String.format("%,.0f", (double) data.get(11));
                String metodePembayaran = (String) data.get(13);
                
                new Konfirmasi(orderId, konserNama, namaLengkap, emailKontak, noTeleponKontak,
                                               nik, jumlahTiket, zona, totalHargaDisplay, metodePembayaran,
                                               loggedInUserName, loggedInUserEmail, loggedInUserPhone).setVisible(true);
                dispose();
            }
        }
    }
    
    private boolean performDeleteOrder(int orderId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiket_konser", "root", "12345");
             PreparedStatement pst = conn.prepareStatement("DELETE FROM pesanan WHERE id = ? AND user_email = ?")) {
            
            pst.setInt(1, orderId);
            pst.setString(2, loggedInUserEmail);
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pesanan. Pesanan tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            logger.severe("Gagal menghapus pesanan: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database saat menghapus pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DaftarPesanan("Test User", "test@mail.com", "0123"));
    }
}