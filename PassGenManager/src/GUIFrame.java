import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;

public class GUIFrame extends JFrame {
	public static void main(String[] args) {
		new GUIFrame();
	}
	//copied from Main.java
	private String MasterKey; //stores user-inputted master key
    private boolean correctKey = false; //used to decide if files should be overwritten upon shutdown
    private HashMap<String, String> passwordDatabase = new HashMap<>();  //hashmap for local memory storage of passwords for encryption

	private static final long serialVersionUID = 1L;
	
	private JLabel infoDisplay;
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
	private JPanel fullPanel;
	
	//expects infoDisplay initialized
	private JPanel getMain() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		//info
		addBagElement(panel, infoDisplay, 0, 0, 3);
		//site
		siteBox = new JTextField(32);
		siteBox.setToolTipText("Enter site name here.");
		addBagElement(panel, siteBox, 1, 1, 2);
		JLabel siteLabel = new JLabel("For:");
		addBagElement(panel, siteLabel, 0, 1, 1);
		//key
		keyBox = new JTextField(32);
		keyBox.setToolTipText("Site password here.");
		addBagElement(panel, siteBox, 1, 2, 2);
		JLabel keyLabel = new JLabel("Password:");
		addBagElement(panel, keyLabel, 0, 2, 1);
		//add
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordDatabase.put(siteBox.getText().toLowerCase(), keyBox.getText());
			}
		});
		addBagElement(panel, addButton, 0, 3, 1);
		//find
		findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: add disambiguation functionality
				keyBox.setText(passwordDatabase.get(siteBox.getText().toLowerCase()));
			}
		});
		addBagElement(panel, findButton, 1, 3, 1);
		//delete
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordDatabase.remove(siteBox.getText());
			}
		});
		addBagElement(panel, deleteButton, 2, 3, 1);
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
				//TODO: swap panels - acquire
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
		p.add(com, c);
	}
	//expects infoDisplay already initialized
	private JPanel getAcquire() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		//info
		addBagElement(panel, infoDisplay, 0, 0, 3);
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
				//TODO: lots of checks here
			}
		});
		addBagElement(panel, setButton, 1, 2, 1);
		//cancel
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: swap panels - main, or exit
			}
		});
		addBagElement(panel, cancelButton, 2, 2, 1);
		return panel;
	}
	
	public GUIFrame() throws HeadlessException {
		super("Password Manager");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//TODO: init infoDisplay
		
		fullPanel = getMain();
		
		//TODO: this should use getAcquire()
		
		JPanel quickPanel = new JPanel();
		JPasswordField firstKeyBox = new JPasswordField(16);
		firstKeyBox.setToolTipText("Master Password for decrypting the file.");
		quickPanel.add(firstKeyBox);
		JButton quickSetButton = new JButton("Set Master Password");
		GUIFrame me = this;
		quickSetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.setMasterKey(new String(firstKeyBox.getPassword()));
				masterKeyBox.setText(new String(firstKeyBox.getPassword()));
				try {
					Main.init();
				}
				catch(IOException error) {
					System.out.println("System Abort: File failure: " + error.getMessage());
					System.exit(ABORT);
				}
				me.full();
			}
		});
		quickPanel.add(quickSetButton);
		this.setContentPane(quickPanel);
		
		this.pack();
		panel.setPreferredSize(new Dimension(siteBox.getWidth(), siteBox.getHeight()*6));
		this.pack();
		this.setVisible(true);
	}
	public void full() {
		this.setContentPane(fullPanel);
		this.pack();
		fullPanel.setPreferredSize(new Dimension(siteBox.getWidth(), siteBox.getHeight()*6));
		this.pack();
		this.setVisible(true);
	}

}
