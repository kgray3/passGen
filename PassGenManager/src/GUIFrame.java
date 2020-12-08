import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class GUIFrame extends JFrame {
	public static void main(String[] args) {
		new GUIFrame();
	}
	//copied from Main.java
	private String masterKey; //stores user-inputed master key
    private HashMap<String, String> passwordDatabase = new HashMap<>();  //hashmap for local memory storage of passwords for encryption

    private boolean settingMaster;
    private int triesRemaining = 3;
    private boolean hookAdded = false;
    private GUIFrame me = this;
    
	private static final long serialVersionUID = 1L;
	
	private JLabel[] infoDisplay = new JLabel[4];
	private JPasswordField masterKeyBox;
	private JTextField keyBox;
	private JTextField siteBox;
	private JButton setButton;
	private JButton cancelButton;
	private JButton addButton;
	private JButton findButton;
	private JButton deleteButton;
	private JButton generateButton;
	private JButton resetButton;
	private JPanel mainPanel;
	private JPanel acquirePanel;
	private JPanel generatePanel;
	private JPanel findPanel;
	
	//expects infoDisplay initialized
	private JPanel getMain() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		//info
		addBagElement(panel, infoDisplay[0], 0, 0, 3);
		//site
		JLabel siteLabel = new JLabel("Site:");
		addBagElement(panel, siteLabel, 0, 1, 1);
		siteBox = new JTextField(32);
		siteBox.setToolTipText("Enter site name here.");
		addBagElement(panel, siteBox, 1, 1, 2);
		//key
		JLabel keyLabel = new JLabel("Password:");
		addBagElement(panel, keyLabel, 0, 2, 1);
		keyBox = new JTextField(32);
		keyBox.setToolTipText("Site password here.");
		addBagElement(panel, keyBox, 1, 2, 2);
		//add
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordDatabase.put(siteBox.getText().toLowerCase(), keyBox.getText());
				inform("Password added.");
			}
		});
		addBagElement(panel, addButton, 1, 3, 1);
		//find
		findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String site = siteBox.getText().toLowerCase();
				if (passwordDatabase.containsKey(site)) {
					keyBox.setText(passwordDatabase.get(site));
					inform("Password found.");
				}
				else if (!site.startsWith("*")){
					inform("Password not found. Try searching with a * as site.");
				}
				else {
					String part = site.substring(1);
					String matches = "";
					for (String key : me.passwordDatabase.keySet()) {
						if(key.contains(part)) {
							matches += key + ":";
						}
					}
					me.keyBox.setText(matches);
					if (matches.length() > 0) inform("Sites found:");
					else inform("No sites found.");
				}
			}
		});
		addBagElement(panel, findButton, 2, 3, 1);
		//delete
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordDatabase.remove(siteBox.getText());
			}
		});
		addBagElement(panel, deleteButton, 0, 3, 1);
		//generate
		generateButton = new JButton("Generate Random");
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: swap panels - generate
			}
		});
		addBagElement(panel, generateButton, 0, 4, 3);
		//reset
		resetButton = new JButton("Reset Master Key");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.settingMaster = true;
				inform("Enter new Master Key (length 16):");
				swapToAcquire();
			}
		});
		addBagElement(panel, resetButton, 0, 5, 3);
		return panel;
	}
	private void addBagElement(JPanel p, Component com, int x, int y, int width) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		p.add(com, c);
	}
	//expects infoDisplay already initialized
	private JPanel getAcquire() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		//info
		addBagElement(panel, infoDisplay[1], 0, 0, 3);
		//master
		masterKeyBox = new JPasswordField(16);
		masterKeyBox.setToolTipText("Master Key for encrypting or decrypting the underlying file.");
		addBagElement(panel, masterKeyBox, 1, 1, 2);
		JLabel masterLabel = new JLabel("Master:");
		addBagElement(panel, masterLabel, 0, 1, 1);
		//set/continue
		setButton = new JButton("Confirm");
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String attempt = new String(masterKeyBox.getPassword());
				if (attempt.length() < 16) inform("Key length is too short. Please try again.");
				else if (attempt.length() > 16) inform("Key length is too long. Please try again.");
				else if (me.settingMaster) {
					me.masterKey = attempt;
					addHook();
					inform("Master Key set successfully.");
					swapToMain();
				}
				else if (Main.match(attempt)) {
					me.masterKey = attempt;
					addHook();
					inform("Master Key accepted.");
					//decrypts password file using master key and deletes files so there isn't issues with overwriting later
                    Main.readDecrypt(passwordDatabase, attempt);
                    Main.deleteFile("ENCRYPTEDpasswords.txt");
                    Main.deleteFile("keyfile.txt");
					swapToMain();
				}
				else {
					me.triesRemaining--;
					inform("Incorrect key. " + me.triesRemaining + " attempts left.");
					if (me.triesRemaining < 1) {
						//terminate in half a second without blocking the event handler
						new Thread() {
							public void run() {
								try {
									Thread.sleep(500);
									System.exit(0);
								}
								catch (InterruptedException e) {
									System.exit(1);
								}
							}
						}.run();
					}
				}
			}
		});
		addBagElement(panel, setButton, 1, 2, 1);
		//cancel
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (me.masterKey == null) {
					System.exit(0);
				}
				else {
					inform("Master Key Unchanged.");
					swapToMain();
				}
			}
		});
		addBagElement(panel, cancelButton, 2, 2, 1);
		return panel;
	}
	private void inform(String message) {
		if (message != null) {
			infoDisplay[0].setText(message);
			infoDisplay[1].setText(message);
			infoDisplay[2].setText(message);
			infoDisplay[3].setText(message);
			pack();
		}
	}
	private void addHook() {
		//don't add more than one hook
		if (hookAdded) return;
        /* SHUTDOWN HOOK
        - in case of crash creates file of passwords only if user has entered correct key,
        - which is determined by only calling this function after that point
        - an incorrect key means no decryption can take place -->
                then passwords can't be stored locally and returned to the new file properly*/
        Runnable doShutDown = () -> {
            try {
                //Stores passwords in 'ENCRYPTEDpasswords.txt'
                Main.createFile("ENCRYPTEDpasswords.txt");
                for (Map.Entry<String, String> entry : me.passwordDatabase.entrySet()) {        //gathers all hashmap values into a set
                    Main.usingBufferedWriter(entry.getKey(), entry.getValue(), "ENCRYPTEDpasswords.txt", me.masterKey);
                }

                //Stores key in 'KEYFILE.txt'
                Main.createFile("keyfile.txt");
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter("keyfile.txt", true)
                );
                writer.newLine();
                writer.write(SHA.get_SHA_512_SecurePassword(me.masterKey)); //uses SHA-512 hash function to securely encrypt MasterKey
                writer.close();
            } catch (Exception e) {
            }
        };

        Runtime.getRuntime().addShutdownHook(new Thread(doShutDown, "ShutdownHook"));
        me.hookAdded = true;
	}
	public GUIFrame() throws HeadlessException {
		super("Password Manager");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		infoDisplay[0] = new JLabel();
		infoDisplay[1] = new JLabel();
		infoDisplay[2] = new JLabel();
		infoDisplay[3] = new JLabel();
		acquirePanel = getAcquire();
		mainPanel = getMain();
		
		boolean fileExists = Files.isReadable(Paths.get("keyfile.txt")) && Files.isWritable(Paths.get("keyfile.txt"));
		if (fileExists) {
			settingMaster = false;
			inform("Welcome back! Please enter your Master Key:");
		}
		else {
			settingMaster = true;
			inform("Welcome, first-time user. Please enter your master key (length 16):");
		}
		swapToAcquire();
		this.setVisible(true);
	}
	private void swapToMain() {
		settingMaster = false;
		this.setContentPane(mainPanel);
		this.pack();
		//mainPanel.setPreferredSize(new Dimension(siteBox.getWidth(), siteBox.getHeight()*6));
	}
	private void swapToAcquire() {
		this.setContentPane(acquirePanel);
		this.pack();
	}
	private void swapToGenerate() {
		this.setContentPane(generatePanel);
		this.pack();
	}
	private void swapToFind() {
		this.setContentPane(findPanel);
		this.pack();
	}
}
