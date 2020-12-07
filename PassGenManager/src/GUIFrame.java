import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

public class GUIFrame extends JFrame {
	public static void main(String[] args) {
		new GUIFrame();
	}

	private static final long serialVersionUID = 1L;
	
	private JPasswordField masterKeyBox;
	private JTextField keyBox;
	private JTextField siteBox;
	private JButton setButton;
	private JButton addButton;
	private JButton findButton;
	private JButton deleteButton;
	private JPanel fullPanel;
	
	public GUIFrame() throws HeadlessException {
		super("Password Manager");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		masterKeyBox = new JPasswordField(16);
		masterKeyBox.setToolTipText("Master Password for encrypting or decrypting the underlying file.");
		keyBox = new JTextField(32);
		keyBox.setToolTipText("Site password here.");
		siteBox = new JTextField(32);
		siteBox.setToolTipText("Enter site name here.");
		setButton = new JButton("Set Master Password");
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.setMasterKey(new String(masterKeyBox.getPassword()));
			}
		});
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.add(siteBox.getText().toLowerCase(), keyBox.getText(), Main.passwordDatabase);
			}
		});
		findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyBox.setText(Main.passwordDatabase.get(siteBox.getText().toLowerCase()));
			}
		});
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.deletePassword(Main.passwordDatabase,siteBox.getText());
			}
		});
		
		JPanel panel = new JPanel();
		panel.add(masterKeyBox);
		panel.add(setButton);
		panel.add(siteBox);
		panel.add(keyBox);
		panel.add(addButton);
		panel.add(findButton);
		panel.add(deleteButton);
		fullPanel = panel;
		
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
