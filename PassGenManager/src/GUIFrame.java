import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUIFrame extends JFrame {
	public static void main(String[] args) {
		new GUIFrame();
	}

	private static final long serialVersionUID = 1L;
	
	private JPasswordField masterKeyBox;
	private JPasswordField keyBox;
	private JTextField siteBox;
	private JButton setButton;
	
	public GUIFrame() throws HeadlessException {
		super("Password Manager");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		masterKeyBox = new JPasswordField(16);
		keyBox = new JPasswordField(32);
		siteBox = new JTextField(32);
		setButton = new JButton("Set Master Password");
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.setMasterKey(new String(masterKeyBox.getPassword()));
			}
		});
		
		panel.add(masterKeyBox);
		panel.add(setButton);
		panel.add(siteBox);
		panel.add(keyBox);
		this.add(panel);
		
		this.pack();
		this.setVisible(true);
	}

}
