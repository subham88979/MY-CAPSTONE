import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class FileEncryptorGUI extends JFrame {

  private JLabel fileLabel, algorithmLabel, statusLabel;
  private JTextField fileField, outputNameField, keyField;
  private JComboBox<String> algorithmComboBox;
  private JButton browseButton, browseOutputButton, encryptButton;

  public FileEncryptorGUI() {
    setTitle("File Encryptor");
    setSize(500, 350);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // GUI Components Initialization
    fileLabel = new JLabel("Select File:");
    algorithmLabel = new JLabel("Encryption Algorithm:");
    statusLabel = new JLabel("");

    fileField = new JTextField(20);
    fileField.setEditable(false);

    algorithmComboBox = new JComboBox<>(new String[]{"AES", "RSA", "Blowfish"});

    browseButton = new JButton("Browse");
    browseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Browse button action to select file
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(FileEncryptorGUI.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          fileField.setText(selectedFile.getAbsolutePath());
        }
      }
    });

    outputNameField = new JTextField(20);

    keyField = new JTextField(20);
    JLabel keyLabel = new JLabel("Enter Key:");

    browseOutputButton = new JButton("Browse");
    browseOutputButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Browse button action to select output directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Restrict to directories
        int result = fileChooser.showSaveDialog(FileEncryptorGUI.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedDir = fileChooser.getSelectedFile();
          outputNameField.setText(selectedDir.getAbsolutePath());
        }
      }
    });

    encryptButton = new JButton("Encrypt");
    encryptButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String inputFile = fileField.getText();
        String outputFile = outputNameField.getText();
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String keyString = keyField.getText();

        if (inputFile.isEmpty() || outputFile.isEmpty() || keyString.isEmpty()) {
          JOptionPane.showMessageDialog(FileEncryptorGUI.this, "Please select a file, specify output directory, and enter a key.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // Validate key length based on the algorithm
        if (algorithm.equals("AES") && keyString.length() != 16) {
          JOptionPane.showMessageDialog(FileEncryptorGUI.this, "Key length must be exactly 16 characters for AES.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        } else if (algorithm.equals("Blowfish") && (keyString.length() < 4 || keyString.length() > 56)) {
          JOptionPane.showMessageDialog(FileEncryptorGUI.this, "Key length must be between 4 and 56 characters for Blowfish.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          if (algorithm.equals("RSA")) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Key publicKey = keyPair.getPublic(); // Use the public key for encryption
            encryptFile(inputFile, outputFile, algorithm, publicKey);
            statusLabel.setText("File encrypted successfully!");
          } else {
            encryptFile(inputFile, outputFile, algorithm, keyString);
            statusLabel.setText("File encrypted successfully!");
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          statusLabel.setText("Error: " + ex.getMessage());
        }
      }
    });

    // GUI Layout Setup
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Adding components to the panel
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(fileLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(fileField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    panel.add(browseButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(algorithmLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(algorithmComboBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(keyLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add(keyField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Output Directory:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 3;
    panel.add(outputNameField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 3;
    panel.add(browseOutputButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 4;
    panel.add(encryptButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 3;
    panel.add(statusLabel, gbc);

    add(panel);
  }

  // Method to perform file encryption with symmetric key (AES or Blowfish)
  private void encryptFile(String inputFile, String outputFile, String algorithm, String keyString) throws Exception {
    byte[] keyBytes = keyString.getBytes();

    // Handle encryption based on the chosen algorithm
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);

    // Create a cipher instance based on the algorithm
    Cipher cipher = Cipher.getInstance(algorithm);

    // Initialize the cipher for encryption mode with the key
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);

    // Perform file encryption
    try (InputStream inputStream = new FileInputStream(inputFile);
         OutputStream outputStream = new FileOutputStream(outputFile + File.separator + new File(inputFile).getName() + ".enc");
         CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        cipherOutputStream.write(buffer, 0, bytesRead);
      }
    }
  }

  // Method to perform file encryption with RSA public key
  private void encryptFile(String inputFile, String outputFile, String algorithm, Key publicKey) throws Exception {
    // Generate a random symmetric key for file encryption (e.g., AES key)
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128);
    SecretKey secretKey = keyGen.generateKey();

    // Create a cipher instance for symmetric encryption
    Cipher symmetricCipher = Cipher.getInstance("AES");
    symmetricCipher.init(Cipher.ENCRYPT_MODE, secretKey);

    // Create a cipher instance for RSA encryption
    Cipher rsaCipher = Cipher.getInstance("RSA");
    rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

    // Get the encrypted symmetric key
    byte[] encryptedKey = rsaCipher.doFinal(secretKey.getEncoded());

    // Write the encrypted key to the output file
    try (OutputStream keyOutputStream = new FileOutputStream(outputFile + File.separator + new File(inputFile).getName() + ".key")) {
        keyOutputStream.write(encryptedKey);
    }

    // Perform file encryption with the symmetric key
    try (InputStream inputStream = new FileInputStream(inputFile);
         OutputStream outputStream = new FileOutputStream(outputFile + File.separator + new File(inputFile).getName() + ".enc");
         CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, symmetricCipher)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        cipherOutputStream.write(buffer, 0, bytesRead);
      }
    }
  }

  // Main method to launch the application
  public static void main(String[] args) {
    // Run the GUI in the event dispatch thread
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new FileEncryptorGUI().setVisible(true);
      }
    });
  }
}
