import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// ---------- User Encapsulation ----------
class User {
    private String firstName, lastName, email, driver, carNumber;
    private int fare = 0;

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDriver(String driver) { this.driver = driver; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }
    public void setFare(int fare) { this.fare = fare; }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getDriver() { return driver; }
    public String getCarNumber() { return carNumber; }
    public int getFare() { return fare; }
}

// ---------- Driver Encapsulation ----------
class Driver {
    private String name;
    private String model;
    private String carNumber;
    private double rating;

    public Driver(String name, String model, String carNumber, double rating) {
        this.name = name;
        this.model = model;
        this.carNumber = carNumber;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getModel() { return model; }
    public String getCarNumber() { return carNumber; }
    public double getRating() { return rating; }

    @Override
    public String toString() {
        return name + " (" + model + ") - " + carNumber;
    }
}

// ---------- Custom Background Panel ----------
class BackgroundPanel extends JPanel {
    private Image bgImage;

    public BackgroundPanel(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                bgImage = new ImageIcon(imagePath).getImage();
            }
        } catch (Exception e) { e.printStackTrace(); }
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else {
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0,0,new Color(180,30,30),getWidth(),getHeight(), new Color(100,100,100));
            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }
}

// ---------- Rounded Button ----------
class RoundedButton extends JButton {
    private Color bgColor;

    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
        super.paintComponent(g2);
        g2.dispose();
    }

    public void setBackgroundColor(Color c) { this.bgColor = c; repaint(); }
}

// ---------- MAIN APP ----------
public class WomenSafetyApp extends JFrame {
    private User user = new User();
    private CardLayout layout = new CardLayout();
    private JPanel mainPanel = new JPanel(layout);

    private javax.swing.Timer rideTimer;
    private int remainingSeconds = 90;

    private static final String DRIVERS_FILE = "drivers.txt";
    private static final String PASS_FILE = "pass.txt";
    private static final String CARINFO_FILE = "carinfo.txt";
    private static final String HISTORY_FILE = "history.txt";
    private static final String DRIVER_HISTORY_FILE = "driver_history.txt";
    private static final String PLACES_FILE = "places.txt";

    private JLabel rideStatusLabel;
    private JLabel timerLabel;

    public WomenSafetyApp() {
        setTitle("Women Travel Safety App");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createDriversFile();
        createPassFile();
        createCarInfoFile();
        createHistoryFile();
        createDriverHistoryFile();
        createPlacesFile();

        mainPanel.add(createMainPage(), "main");
        mainPanel.add(createLoginPage(), "login");
        mainPanel.add(createSignUpPage(), "signup");
        mainPanel.add(createSecondPage(), "second");
        add(mainPanel);

        layout.show(mainPanel, "main");
        setVisible(true);
    }

    // ---------- MAIN PAGE ----------
    private JPanel createMainPage() {
        BackgroundPanel panel = new BackgroundPanel("book.png");
        RoundedButton startBtn = new RoundedButton("Start Trip", new Color(180,30,30));
        startBtn.setBounds(150,200,200,50);
        startBtn.addActionListener(e -> layout.show(mainPanel,"login"));
        panel.add(startBtn);

        RoundedButton exitBtn = new RoundedButton("Exit", new Color(100,100,100));
        exitBtn.setBounds(150,300,200,50);
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        JLabel footer = new JLabel("Your safety is our priority!");
        footer.setBounds(0,520,500,20);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setForeground(Color.WHITE);
        panel.add(footer);

        return panel;
    }

    // ---------- LOGIN PAGE ----------
    private JPanel createLoginPage() {
        BackgroundPanel panel = new BackgroundPanel(null);
        panel.setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(80,120,150,20); emailLabel.setForeground(Color.WHITE); panel.add(emailLabel);
        JTextField emailField = new JTextField(); emailField.setBounds(80,150,300,35); panel.add(emailField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(80,200,150,20); passLabel.setForeground(Color.WHITE); panel.add(passLabel);
        JPasswordField passField = new JPasswordField(); passField.setBounds(80,230,300,35); panel.add(passField);

        RoundedButton loginBtn = new RoundedButton("Login", new Color(180,30,30));
        loginBtn.setBounds(140,300,100,40);
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passField.getPassword());
            if(email.isEmpty() || password.isEmpty()){ JOptionPane.showMessageDialog(this,"Fill all fields"); return;}
            if(checkLogin(email,password)){
                user.setEmail(email);
                JOptionPane.showMessageDialog(this,"Login Successful!");
                layout.show(mainPanel,"second");
            } else {
                int option = JOptionPane.showConfirmDialog(this,"No account found. Sign up?","Sign Up",JOptionPane.YES_NO_OPTION);
                if(option==JOptionPane.YES_OPTION) layout.show(mainPanel,"signup");
            }
        });
        panel.add(loginBtn);

        RoundedButton signUpBtn = new RoundedButton("Sign Up", new Color(100,100,100));
        signUpBtn.setBounds(260,300,100,40); signUpBtn.addActionListener(e -> layout.show(mainPanel,"signup"));
        panel.add(signUpBtn);

        return panel;
    }

    // ---------- SIGN UP PAGE ----------
    private JPanel createSignUpPage() {
        BackgroundPanel panel = new BackgroundPanel(null);
        panel.setLayout(null);

        JLabel title = new JLabel("Sign Up"); title.setBounds(180,20,200,40); title.setForeground(Color.WHITE); title.setFont(new Font("Arial",Font.BOLD,26)); panel.add(title);

        JLabel fLabel = new JLabel("First Name:"); fLabel.setBounds(50,80,100,25); fLabel.setForeground(Color.WHITE); panel.add(fLabel);
        JTextField fField = new JTextField(); fField.setBounds(180,80,250,30); panel.add(fField);

        JLabel lLabel = new JLabel("Last Name:"); lLabel.setBounds(50,120,100,25); lLabel.setForeground(Color.WHITE); panel.add(lLabel);
        JTextField lField = new JTextField(); lField.setBounds(180,120,250,30); panel.add(lField);

        JLabel emailLabel = new JLabel("Email:"); emailLabel.setBounds(50,160,100,25); emailLabel.setForeground(Color.WHITE); panel.add(emailLabel);
        JTextField emailField = new JTextField(); emailField.setBounds(180,160,250,30); panel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone Number:"); phoneLabel.setBounds(50,200,120,25); phoneLabel.setForeground(Color.WHITE); panel.add(phoneLabel);
        JTextField phoneField = new JTextField(); phoneField.setBounds(180,200,250,30); panel.add(phoneField);

        JLabel passLabel = new JLabel("Password:"); passLabel.setBounds(50,240,100,25); passLabel.setForeground(Color.WHITE); panel.add(passLabel);
        JPasswordField passField = new JPasswordField(); passField.setBounds(180,240,250,30); panel.add(passField);

        RoundedButton submitBtn = new RoundedButton("Submit", new Color(180,30,30));
        submitBtn.setBounds(180,300,140,40);
        submitBtn.addActionListener(e -> {
            String fn=fField.getText().trim(), ln=lField.getText().trim(), em=emailField.getText().trim();
            String ph=phoneField.getText().trim(), pw=new String(passField.getPassword()).trim();
            if(fn.isEmpty()||ln.isEmpty()||em.isEmpty()||ph.isEmpty()||pw.isEmpty()){ JOptionPane.showMessageDialog(this,"Fill all fields"); return;}
            if(!em.endsWith("@gmail.com")){ JOptionPane.showMessageDialog(this,"Email must be Gmail"); return;}
            try{Long.parseLong(ph);}catch(Exception ex){ JOptionPane.showMessageDialog(this,"Phone must be numeric"); return;}
            if(checkLogin(em,pw)){ JOptionPane.showMessageDialog(this,"Account exists, login"); layout.show(mainPanel,"login"); return;}
            try(BufferedWriter bw=new BufferedWriter(new FileWriter(PASS_FILE,true))){ bw.write(fn+","+ln+","+em+","+ph+","+pw+"\n"); }catch(Exception ex){ ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this,"Sign Up Successful! Please login."); layout.show(mainPanel,"login");
        });
        panel.add(submitBtn);

        return panel;
    }

    // ---------- SECOND PAGE ----------
    private JPanel createSecondPage() {
        BackgroundPanel panel = new BackgroundPanel("d.png");
        panel.setLayout(null);

        JLabel prompt = new JLabel("Verify Driver");
        prompt.setBounds(140,20,220,30);
        prompt.setFont(new Font("Arial",Font.BOLD,20));
        prompt.setForeground(Color.WHITE);
        panel.add(prompt);

        RoundedButton pickDriverBtn = new RoundedButton("Pick a Driver", new Color(180,30,30));
        pickDriverBtn.setBounds(150,80,200,45);
        pickDriverBtn.addActionListener(e -> {
            java.util.List<Driver> drivers = readDriversFromFile();
            if(drivers.size()==0){ JOptionPane.showMessageDialog(this,"No drivers available"); return;}
            boolean chosen=false;
            for(Driver d:drivers){
                int ans = JOptionPane.showConfirmDialog(this,
                        "Driver: "+d.getName()+"\nCar: "+d.getModel()+"\nCar Number: "+d.getCarNumber()+"\nDo you want this ride?",
                        "Choose Driver", JOptionPane.YES_NO_OPTION);
                if(ans==JOptionPane.YES_OPTION){
                    user.setDriver(d.getName());
                    user.setCarNumber(d.getCarNumber());
                    saveCarInfo();

                    Map<String,Integer> places = readPlaces();
                    String[] options = places.keySet().toArray(new String[0]);
                    String selectedPlace = (String) JOptionPane.showInputDialog(this,"Select destination:","Destination",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
                    if(selectedPlace==null) return;
                    int fare = places.get(selectedPlace);
                    user.setFare(fare);

                    showRideStartedPage(d);
                    chosen=true;
                    break;
                }
            }
            if(!chosen) JOptionPane.showMessageDialog(this,"No drivers accepted");
        });
        panel.add(pickDriverBtn);

        RoundedButton back = new RoundedButton("Back", new Color(100,100,100));
        back.setBounds(150,140,200,40);
        back.addActionListener(e -> layout.show(mainPanel,"main"));
        panel.add(back);

        return panel;
    }

    // ---------- RIDE PAGE ----------
    private void showRideStartedPage(Driver chosenDriver){
        JDialog rideDialog = new JDialog(this,"Ride In Progress",true);
        rideDialog.setSize(480,420); rideDialog.setLocationRelativeTo(this); rideDialog.setLayout(new BorderLayout());

        JPanel leftMenu = new JPanel(); leftMenu.setPreferredSize(new Dimension(120,0)); leftMenu.setLayout(new GridLayout(6,1,5,5)); leftMenu.setBackground(new Color(230,230,230));
        RoundedButton historyBtn = new RoundedButton("History", new Color(120,120,120)); historyBtn.setFont(new Font("Arial",Font.PLAIN,13));
        historyBtn.addActionListener(e -> showHistoryDialog()); leftMenu.add(historyBtn);

        RoundedButton emergencyBtn = new RoundedButton("Emergency", new Color(180,30,30));
        emergencyBtn.setFont(new Font("Arial",Font.PLAIN,13));
        emergencyBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(rideDialog,"Emergency Number: 1122\nDo you want to cancel the ride?","Emergency",JOptionPane.YES_NO_OPTION);
            if(option==JOptionPane.YES_OPTION){
                if(rideTimer!=null && rideTimer.isRunning()) rideTimer.stop();
                rideDialog.dispose();
                resetUser();
                layout.show(mainPanel,"login");
            }
        });
        leftMenu.add(emergencyBtn); leftMenu.add(new JLabel(""));

        rideDialog.add(leftMenu, BorderLayout.WEST);

        JPanel center = new JPanel(null); center.setBackground(new Color(250,250,250));
        rideStatusLabel = new JLabel("Your ride has started"); rideStatusLabel.setFont(new Font("Arial",Font.BOLD,20)); rideStatusLabel.setBounds(40,30,360,30); center.add(rideStatusLabel);
        JLabel destLabel = new JLabel("Destination in 1.5 minutes"); destLabel.setFont(new Font("Arial",Font.PLAIN,16)); destLabel.setBounds(40,70,360,25); center.add(destLabel);
        JLabel fareLabel = new JLabel("Fare: Rs "+user.getFare()); fareLabel.setFont(new Font("Arial",Font.PLAIN,18)); fareLabel.setBounds(40,150,200,25); center.add(fareLabel);
        timerLabel = new JLabel(formatTime(remainingSeconds)); timerLabel.setFont(new Font("Arial",Font.BOLD,28)); timerLabel.setBounds(40,110,200,40); center.add(timerLabel);

        RoundedButton satisfiedBtn = new RoundedButton("Satisfied", new Color(0,153,76)); satisfiedBtn.setBounds(40,180,120,40);
        satisfiedBtn.addActionListener(e -> { appendToHistory(user.getEmail(),chosenDriver.getName(),chosenDriver.getCarNumber(),"Satisfied"); appendToDriverHistory(chosenDriver.getName(),chosenDriver.getCarNumber(),"Satisfied"); JOptionPane.showMessageDialog(rideDialog,"Thank you for your feedback!"); });
        center.add(satisfiedBtn);

        RoundedButton notSatisfiedBtn = new RoundedButton("Not Satisfied", new Color(180,30,30)); notSatisfiedBtn.setBounds(180,180,140,40);
        notSatisfiedBtn.addActionListener(e -> { appendToHistory(user.getEmail(),chosenDriver.getName(),chosenDriver.getCarNumber(),"Not Satisfied"); appendToDriverHistory(chosenDriver.getName(),chosenDriver.getCarNumber(),"Not Satisfied"); JOptionPane.showMessageDialog(rideDialog,"Feedback recorded."); });
        center.add(notSatisfiedBtn);

        rideDialog.add(center, BorderLayout.CENTER);

        rideTimer = new javax.swing.Timer(1000, new ActionListener() {
            int count = remainingSeconds;
            public void actionPerformed(ActionEvent e) {
                count--; timerLabel.setText(formatTime(count));
                if(count<=0){
                    rideTimer.stop(); JOptionPane.showMessageDialog(rideDialog,"Your ride has completed!"); rideDialog.dispose(); resetUser(); layout.show(mainPanel,"main");
                }
            }
        });
        rideTimer.start();
        rideDialog.setVisible(true);
    }

    private String formatTime(int sec){ int m = sec/60, s = sec%60; return String.format("%02d:%02d",m,s); }

    // ---------- FILE OPERATIONS ----------
    private void createDriversFile(){ File f=new File(DRIVERS_FILE); if(!f.exists()){ try(BufferedWriter bw=new BufferedWriter(new FileWriter(f))){ bw.write("Ali,Toyota,ABC123,4.5\n"); bw.write("Sara,Honda,DEF456,4.8\n"); bw.write("Usman,Suzuki,GHI789,4.2\n"); }catch(Exception e){e.printStackTrace();} } }
    private void createPassFile(){ File f=new File(PASS_FILE); if(!f.exists()){ try{f.createNewFile();}catch(Exception e){e.printStackTrace();} } }
    private void createCarInfoFile(){ File f=new File(CARINFO_FILE); if(!f.exists()){ try{f.createNewFile();}catch(Exception e){e.printStackTrace();} } }
    private void createHistoryFile(){ File f=new File(HISTORY_FILE); if(!f.exists()){ try{f.createNewFile();}catch(Exception e){e.printStackTrace();} } }
    private void createDriverHistoryFile(){ File f=new File(DRIVER_HISTORY_FILE); if(!f.exists()){ try{f.createNewFile();}catch(Exception e){e.printStackTrace();} } }
    private void createPlacesFile(){ File f=new File(PLACES_FILE); if(!f.exists()){ try(BufferedWriter bw=new BufferedWriter(new FileWriter(f))){ bw.write("Airport,500\n"); bw.write("Mall,300\n"); bw.write("Station,200\n"); }catch(Exception e){e.printStackTrace();} } }

    private Map<String,Integer> readPlaces(){ Map<String,Integer> map=new LinkedHashMap<>(); try(BufferedReader br=new BufferedReader(new FileReader(PLACES_FILE))){ String line; while((line=br.readLine())!=null){ String[] arr=line.split(","); if(arr.length>=2) map.put(arr[0],Integer.parseInt(arr[1])); } }catch(Exception e){e.printStackTrace();} return map; }

    private java.util.List<Driver> readDriversFromFile(){ java.util.List<Driver> list=new ArrayList<>(); try(BufferedReader br=new BufferedReader(new FileReader(DRIVERS_FILE))){ String line; while((line=br.readLine())!=null){ String[] arr=line.split(","); if(arr.length>=4) list.add(new Driver(arr[0],arr[1],arr[2],Double.parseDouble(arr[3]))); } }catch(Exception e){e.printStackTrace();} return list; }

    private boolean checkLogin(String email,String password){ try(BufferedReader br=new BufferedReader(new FileReader(PASS_FILE))){ String line; while((line=br.readLine())!=null){ String[] arr=line.split(","); if(arr.length>=5 && arr[2].equals(email) && arr[4].equals(password)) return true; } }catch(Exception e){e.printStackTrace();} return false; }

    private void saveCarInfo(){ try(BufferedWriter bw=new BufferedWriter(new FileWriter(CARINFO_FILE,true))){ bw.write(user.getDriver()+","+user.getCarNumber()+"\n"); }catch(Exception e){e.printStackTrace();} }
    private void appendToHistory(String email,String driverName,String carNumber,String feedback){ try(BufferedWriter bw=new BufferedWriter(new FileWriter(HISTORY_FILE,true))){ bw.write(email+","+driverName+","+carNumber+","+feedback+"\n"); }catch(Exception e){e.printStackTrace();} }
    private void appendToDriverHistory(String driverName,String carNumber,String feedback){ try(BufferedWriter bw=new BufferedWriter(new FileWriter(DRIVER_HISTORY_FILE,true))){ bw.write(driverName+","+carNumber+","+feedback+"\n"); }catch(Exception e){e.printStackTrace();} }

    private void resetUser(){ user.setDriver(null); user.setCarNumber(null); user.setFare(0); remainingSeconds=90; }

    private void showHistoryDialog(){ JTextArea area=new JTextArea(20,30); area.setEditable(false); try(BufferedReader br=new BufferedReader(new FileReader(HISTORY_FILE))){ String line; while((line=br.readLine())!=null) area.append(line+"\n"); }catch(Exception e){e.printStackTrace();} JScrollPane scroll=new JScrollPane(area); JOptionPane.showMessageDialog(this,scroll,"Ride History",JOptionPane.INFORMATION_MESSAGE); }

    public static void main(String[] args){ SwingUtilities.invokeLater(() -> new WomenSafetyApp()); }
}
