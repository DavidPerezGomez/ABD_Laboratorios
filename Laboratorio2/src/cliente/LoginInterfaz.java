package cliente;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginInterfaz extends JFrame {
	
	private Interfaz parent;
	private String address;
	private String port;
	
	private JTextField userField;
	private JPasswordField passwordField;

	public LoginInterfaz(Interfaz pParent, String pAddress, String pPort) {
		parent = pParent;
		address = pAddress;
		port = pPort;
		parent.setEnabled(false);
		initialize();
	}

	/**
	 * Initialize the contents of the this.
	 */
	private void initialize() {
		this.setBounds(100, 100, 450, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		JLabel lblUser = new JLabel("User");
		lblUser.setBounds(10, 42, 80, 21);
		this.getContentPane().add(lblUser);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 102, 70, 14);
		this.getContentPane().add(lblPassword);
		
		userField = new JTextField();
		userField.setBounds(130, 42, 170, 20);
		this.getContentPane().add(userField);
		userField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(130, 99, 170, 20);
		this.getContentPane().add(passwordField);
		passwordField.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(148, 185, 89, 23);
		this.getContentPane().add(btnOk);
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String user = userField.getText();	
				String password = String.copyValueOf(passwordField.getPassword());
				Data data;
				try {
					data = new Data(address, port, user, password);
				} catch (SQLException e) {
					e.printStackTrace();
					data = null;
				}

				if (data != null) {
					parent.setData(data);
					JOptionPane.showMessageDialog(LoginInterfaz.this, "Conexión abierta");
				} else {
					JOptionPane.showMessageDialog(LoginInterfaz.this, "No se ha podido establecer la conexión");
				}
				parent.setEnabled(true);
				LoginInterfaz.this.dispose();
			}
			
		});
	}
}
