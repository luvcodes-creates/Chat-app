import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.net.*;

/**
 * ChatClient: Main client GUI application for chat communication
 */
public class ChatClient extends JFrame {
    private ChatController controller;
    private JTextPane chatDisplay; // Changed to JTextPane for formatting support
    private JTextField messageInput;
    private JButton sendButton;
    private JButton emojiButton;
    private JButton boldButton;
    private JButton italicButton;
    private JLabel statusLabel;
    private String username;
    private StyledDocument doc;
    private boolean isBold = false;
    private boolean isItalic = false;
    private Point dragPoint = new Point();
    
    public ChatClient() {
        setTitle("Chat Application");
        setSize(600, 700);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Now resizable!
        
        initializeUI();
        setVisible(true);
        
        showLoginDialog();
    }
    
    private void initializeUI() {
        // Header Panel - Make it draggable
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        statusLabel = new JLabel("Not connected");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(statusLabel);
        
        // Make header panel draggable to move window
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint.x = e.getXOnScreen() - getLocation().x;
                dragPoint.y = e.getYOnScreen() - getLocation().y;
            }
        });
        
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point newLocation = new Point(
                    e.getXOnScreen() - dragPoint.x,
                    e.getYOnScreen() - dragPoint.y
                );
                setLocation(newLocation);
            }
        });
        
        // Also make status label draggable
        statusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint.x = e.getXOnScreen() - getLocation().x;
                dragPoint.y = e.getYOnScreen() - getLocation().y;
            }
        });
        
        statusLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point newLocation = new Point(
                    e.getXOnScreen() - dragPoint.x,
                    e.getYOnScreen() - dragPoint.y
                );
                setLocation(newLocation);
            }
        });
        
        // Chat Display Panel - Using JTextPane for formatting support
        chatDisplay = new JTextPane();
        chatDisplay.setEditable(false);
        chatDisplay.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        doc = chatDisplay.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(chatDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Formatting Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbarPanel.setBackground(new Color(245, 245, 245));
        
        emojiButton = new JButton("😀");
        emojiButton.setToolTipText("Insert Emoji");
        emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        emojiButton.setEnabled(false);
        emojiButton.addActionListener(e -> showEmojiPicker());
        
        boldButton = new JButton("B");
        boldButton.setToolTipText("Bold");
        boldButton.setFont(new Font("Arial", Font.BOLD, 12));
        boldButton.setEnabled(false);
        boldButton.addActionListener(e -> toggleBold());
        
        italicButton = new JButton("I");
        italicButton.setToolTipText("Italic");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 12));
        italicButton.setEnabled(false);
        italicButton.addActionListener(e -> toggleItalic());
        
        toolbarPanel.add(emojiButton);
        toolbarPanel.add(boldButton);
        toolbarPanel.add(italicButton);
        
        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        messageInput.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> sendMessage());
        
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Combine toolbar and input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(toolbarPanel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.setSize(350, 250);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel hostLabel = new JLabel("Host:");
        JTextField hostField = new JTextField("localhost");
        
        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField("6001");
        
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        
        panel.add(hostLabel);
        panel.add(hostField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        
        JPanel buttonPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        
        loginBtn.addActionListener(e -> {
            try {
                String host = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText());
                String user = userField.getText().trim();
                String pass = new String(passField.getPassword());
                
                if (host.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(loginDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (connectToServer(host, port, user, pass, true)) {
                    loginDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(loginDialog, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registerBtn.addActionListener(e -> {
            try {
                String host = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText());
                String user = userField.getText().trim();
                String pass = new String(passField.getPassword());
                
                if (host.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(loginDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (connectToServer(host, port, user, pass, false)) {
                    loginDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(loginDialog, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        loginDialog.add(mainPanel);
        loginDialog.setVisible(true);
    }
    
    private boolean connectToServer(String host, int port, String user, String pass, boolean isLogin) {
        try {
            this.username = user;
            this.controller = new ChatController(host, port, user, pass, isLogin, this);
            
            if (controller.connect()) {
                statusLabel.setText("Connected as: " + user);
                statusLabel.setForeground(new Color(39, 174, 96));
                sendButton.setEnabled(true);
                messageInput.setEnabled(true);
                emojiButton.setEnabled(true);
                boldButton.setEnabled(true);
                italicButton.setEnabled(true);
                messageInput.requestFocus();
                
                // Request message history
                controller.requestMessageHistory();
                
                // Start message receiver thread
                new Thread(() -> controller.receiveMessages()).start();
                
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && controller != null) {
            try {
                // Apply formatting
                String formattedMessage = formatMessage(message);
                controller.sendMessage(formattedMessage);
                messageInput.setText("");
                // Reset formatting
                isBold = false;
                isItalic = false;
                boldButton.setBackground(null);
                italicButton.setBackground(null);
            } catch (IOException e) {
                appendMessage("Error sending message: " + e.getMessage());
            }
        }
    }
    
    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Parse formatting in message
                parseAndInsertFormattedText(message + "\n");
                chatDisplay.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void parseAndInsertFormattedText(String text) throws BadLocationException {
        Style regularStyle = doc.addStyle("regular", null);
        StyleConstants.setFontFamily(regularStyle, "Segoe UI Emoji");
        StyleConstants.setFontSize(regularStyle, 12);
        
        Style boldStyle = doc.addStyle("bold", regularStyle);
        StyleConstants.setBold(boldStyle, true);
        
        Style italicStyle = doc.addStyle("italic", regularStyle);
        StyleConstants.setItalic(italicStyle, true);
        
        Style boldItalicStyle = doc.addStyle("boldItalic", regularStyle);
        StyleConstants.setBold(boldItalicStyle, true);
        StyleConstants.setItalic(boldItalicStyle, true);
        
        int pos = 0;
        while (pos < text.length()) {
            // Look for *text* (bold) or _text_ (italic)
            int boldStart = text.indexOf("*", pos);
            int italicStart = text.indexOf("_", pos);
            
            if (boldStart == -1 && italicStart == -1) {
                // No more formatting, insert rest as regular
                doc.insertString(doc.getLength(), text.substring(pos), regularStyle);
                break;
            }
            
            int nextPos = (boldStart != -1 && italicStart != -1) ? 
                Math.min(boldStart, italicStart) : 
                (boldStart != -1 ? boldStart : italicStart);
            
            // Insert text before formatting
            if (nextPos > pos) {
                doc.insertString(doc.getLength(), text.substring(pos, nextPos), regularStyle);
            }
            
            if (nextPos == boldStart) {
                // Handle bold
                int boldEnd = text.indexOf("*", boldStart + 1);
                if (boldEnd != -1) {
                    String boldText = text.substring(boldStart + 1, boldEnd);
                    doc.insertString(doc.getLength(), boldText, boldStyle);
                    pos = boldEnd + 1;
                } else {
                    doc.insertString(doc.getLength(), "*", regularStyle);
                    pos = boldStart + 1;
                }
            } else if (nextPos == italicStart) {
                // Handle italic
                int italicEnd = text.indexOf("_", italicStart + 1);
                if (italicEnd != -1) {
                    String italicText = text.substring(italicStart + 1, italicEnd);
                    doc.insertString(doc.getLength(), italicText, italicStyle);
                    pos = italicEnd + 1;
                } else {
                    doc.insertString(doc.getLength(), "_", regularStyle);
                    pos = italicStart + 1;
                }
            }
        }
    }
    
    private void showEmojiPicker() {
        String[] emojis = {"😀", "😂", "😍", "😊", "👍", "❤️", "🎉", "🔥", "⭐", "💯",
                          "😎", "🤔", "😮", "😢", "😡", "👏", "🙌", "🤝", "✌️", "👋"};
        
        JDialog emojiDialog = new JDialog(this, "Emoji Picker", true);
        emojiDialog.setSize(300, 200);
        emojiDialog.setLocationRelativeTo(this);
        
        JPanel emojiPanel = new JPanel(new GridLayout(4, 5, 5, 5));
        emojiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (String emoji : emojis) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            emojiBtn.addActionListener(e -> {
                messageInput.setText(messageInput.getText() + emoji);
                emojiDialog.dispose();
                messageInput.requestFocus();
            });
            emojiPanel.add(emojiBtn);
        }
        
        emojiDialog.add(emojiPanel);
        emojiDialog.setVisible(true);
    }
    
    private void toggleBold() {
        isBold = !isBold;
        if (isBold) {
            boldButton.setBackground(new Color(200, 200, 200));
        } else {
            boldButton.setBackground(null);
        }
    }
    
    private void toggleItalic() {
        isItalic = !isItalic;
        if (isItalic) {
            italicButton.setBackground(new Color(200, 200, 200));
        } else {
            italicButton.setBackground(null);
        }
    }
    
    private String formatMessage(String message) {
        if (isBold) {
            message = "*" + message + "*";
        }
        if (isItalic) {
            message = "_" + message + "_";
        }
        return message;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient());
    }
}
