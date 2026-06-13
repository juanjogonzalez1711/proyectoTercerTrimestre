package es.studium;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BajaPais extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Baja País");
	Choice choPaises = new Choice();
	Button btnEliminar = new Button("Eliminar");
	Dialog dlgConfirmar = new Dialog(ventana, "Confirmación", true);
	Label lblConfirmar = new Label("¿Estás segur@ de borrar XXXXXXXXXXXX?");
	Panel arriba = new Panel(), abajo = new Panel();
	Button btnSi = new Button("Sí"), btnNo = new Button("No");
	Dialog dlgMensaje = new Dialog(ventana, "Respuesta", true);
	Label lblMensaje = new Label("Error en Baja");
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", login = "root", password = "studium2025;";
	Connection connection = null; Statement statement = null; ResultSet rs = null;

	
	String usuarioSesion = "administrador";

	public BajaPais() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(300, 120);
		ventana.addWindowListener(this);
		btnEliminar.addActionListener(this);
		rellenarChoice();
		ventana.add(choPaises);
		ventana.add(btnEliminar);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		dlgConfirmar.setLayout(new FlowLayout());
		dlgConfirmar.setSize(380, 150);
		dlgConfirmar.addWindowListener(this);
		dlgConfirmar.setLocationRelativeTo(null);
		arriba.add(lblConfirmar);
		dlgConfirmar.add(arriba);
		btnSi.addActionListener(this);
		btnNo.addActionListener(this);
		abajo.add(btnSi);
		abajo.add(btnNo);
		dlgConfirmar.add(abajo);
		dlgMensaje.setLayout(new FlowLayout());
		dlgMensaje.setSize(200, 100);
		dlgMensaje.addWindowListener(this);
		dlgMensaje.setLocationRelativeTo(null);
		dlgMensaje.add(lblMensaje);
		ventana.setVisible(true);
	}

	private void rellenarChoice() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT nombrePais FROM paises");
			choPaises.removeAll();
			choPaises.add("Seleccionar un país...");
			while(rs.next()) {
				choPaises.add(rs.getString("nombrePais"));
			}
		} catch(Exception e) {
			System.err.println("Error: " + e.getMessage());
		} finally { cerrarConexion(); }
	}

	private void cerrarConexion() {
		try { if(connection != null) connection.close(); } catch(SQLException e) { System.err.println("Error al cerrar"); }
	}

	public static void main(String[] args) { new BajaPais(); }

	@Override
	public void windowClosing(WindowEvent e) {
		if(dlgConfirmar.isVisible()) dlgConfirmar.setVisible(false);
		else if(dlgMensaje.isVisible()) dlgMensaje.setVisible(false);
		else System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		if(evento.getSource().equals(btnEliminar)) {
			if(choPaises.getSelectedIndex() != 0) {
				lblConfirmar.setText("¿Borrar " + choPaises.getSelectedItem() + "?");
				dlgConfirmar.setVisible(true);
			}
		} else if(evento.getSource().equals(btnSi)) {
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url, login, password);
				statement = connection.createStatement();
				String nombre = choPaises.getSelectedItem();
				
				String sql = "DELETE FROM paises WHERE nombrePais = '" + nombre + "'";
				statement.executeUpdate(sql);
				
				
				logmanager.registrar(usuarioSesion, "Baja: " + sql);
				
				lblMensaje.setText("Baja correcta");
				dlgMensaje.setVisible(true);
				dlgConfirmar.setVisible(false);
				rellenarChoice();
			} catch(Exception e) {
				lblMensaje.setText("Error en Baja");
				dlgMensaje.setVisible(true);
			} finally { cerrarConexion(); }
		} else if(evento.getSource().equals(btnNo)) { dlgConfirmar.setVisible(false); }
	}
}