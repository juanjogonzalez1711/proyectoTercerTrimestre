package es.studium;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class login extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Login Cine");
	TextField txtUsuario = new TextField(20), txtClave = new TextField(20);
	Button btnEntrar = new Button("Entrar");
	Label lblU = new Label("Usuario:"), lblC = new Label("Clave:");
	Dialog dlgError = new Dialog(ventana, "Error", true);
	Label lblError = new Label("Usuario o clave incorrectos");

	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", userBD = "root", passBD = "studium2025;";

	public login() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(220, 180);
		ventana.addWindowListener(this);
		txtClave.setEchoChar('*'); 
		btnEntrar.addActionListener(this);
		ventana.add(lblU); ventana.add(txtUsuario);
		ventana.add(lblC); ventana.add(txtClave);
		ventana.add(btnEntrar);
		
		dlgError.setLayout(new FlowLayout());
		dlgError.setSize(200, 100);
		dlgError.add(lblError);
		dlgError.addWindowListener(this);
		
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args) { new login(); }

	@Override public void windowClosing(WindowEvent e) {
		if(dlgError.isVisible()) dlgError.setVisible(false);
		else System.exit(0);
	}

	@Override public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnEntrar)) {
			Connection con = null; Statement st = null; ResultSet rs = null;
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, userBD, passBD);
				st = con.createStatement();
				
				String usuarioFormulario = txtUsuario.getText();
				String sql = "SELECT tipoUsuario FROM usuarios WHERE nombreUsuario = '" + usuarioFormulario + "' AND contraseñaUsuario = '" + txtClave.getText() + "'";
				rs = st.executeQuery(sql);

				if (rs.next()) {
					int rol = rs.getInt("tipoUsuario");
					
					
					logmanager.registrar(usuarioFormulario, "Inicio de sesión correcto.");
					
					ventana.dispose();
					new Principal(rol == 1); 
				} else {
					dlgError.setLocationRelativeTo(null);
					dlgError.setVisible(true);
				}
			} catch (Exception ex) { 
				System.err.println("Error: " + ex.getMessage());
			} finally {
				try { 
					if(rs != null) rs.close(); 
					if(st != null) st.close(); 
					if(con != null) con.close(); 
				} catch(SQLException ex) {}
			}
		}
	}
}