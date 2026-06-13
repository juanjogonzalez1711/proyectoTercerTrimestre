package es.studium;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class modificarProductora extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Modificación");
	Choice choProductoras = new Choice();
	Button btnEditar = new Button("Editar");
	Dialog dlgEdicion = new Dialog(ventana, "Editando...", true);
	Label lblTitulo = new Label("# Editando Productora #");
	Label lblNombre = new Label("Nombre:");
	TextField txtNombre = new TextField(15);
	Label lblPresupuesto = new Label("Presupuesto:");
	TextField txtPresupuesto = new TextField(15);
	Button btnAceptar = new Button("Aceptar"), btnLimpiar = new Button("Limpiar");
	Dialog dlgMensaje = new Dialog(ventana, "Respuesta", true);
	Label lblMensaje = new Label("");
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", login = "root", password = "studium2025;", idProductora = "";
	Connection connection = null; Statement statement = null; ResultSet rs = null;

	String usuarioSesion = "administrador";

	public modificarProductora() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(300, 120);
		ventana.addWindowListener(this);
		btnEditar.addActionListener(this);
		rellenarChoice();
		ventana.add(choProductoras);
		ventana.add(btnEditar);
		ventana.setLocationRelativeTo(null);
		dlgEdicion.setLayout(new FlowLayout());
		dlgEdicion.setSize(300, 200);
		dlgEdicion.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);
		dlgEdicion.add(lblTitulo);
		dlgEdicion.add(lblNombre); dlgEdicion.add(txtNombre);
		dlgEdicion.add(lblPresupuesto); dlgEdicion.add(txtPresupuesto);
		dlgEdicion.add(btnAceptar); dlgEdicion.add(btnLimpiar);
		dlgEdicion.setLocationRelativeTo(null);
		dlgMensaje.setLayout(new FlowLayout());
		dlgMensaje.setSize(200, 100);
		dlgMensaje.addWindowListener(this);
		dlgMensaje.add(lblMensaje);
		dlgMensaje.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	private void rellenarChoice() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT * FROM productoras");
			choProductoras.removeAll();
			choProductoras.add("Seleccionar una productora...");
			while(rs.next()) {
				choProductoras.add(rs.getInt("idProductora") + " " + rs.getString("nombreProductora"));
			}
		} catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
		finally { cerrarConexion(); }
	}

	private void cerrarConexion() {
		try { if(connection != null) connection.close(); } catch(SQLException e) {}
	}

	public static void main(String[] args) { new modificarProductora(); }

	@Override
	public void windowClosing(WindowEvent e) {
		if(dlgEdicion.isVisible()) dlgEdicion.setVisible(false);
		else if(dlgMensaje.isVisible()) dlgMensaje.setVisible(false);
		else System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		if(evento.getSource().equals(btnEditar)) {
			if(choProductoras.getSelectedIndex() != 0) {
				idProductora = choProductoras.getSelectedItem().split(" ")[0];
				try {
					Class.forName(driver);
					connection = DriverManager.getConnection(url, login, password);
					statement = connection.createStatement();
					rs = statement.executeQuery("SELECT * FROM productoras WHERE idProductora = " + idProductora);
					if(rs.next()) {
						txtNombre.setText(rs.getString("nombreProductora"));
						txtPresupuesto.setText(rs.getString("presupuestoProductora"));
						lblTitulo.setText("Editando ID: " + idProductora);
						dlgEdicion.setVisible(true);
					}
				} catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
				finally { cerrarConexion(); }
			}
		} else if(evento.getSource().equals(btnAceptar)) {
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url, login, password);
				statement = connection.createStatement();
				
				
				String sql = "UPDATE productoras SET nombreProductora = '" + txtNombre.getText() + "', presupuestoProductora = '" + txtPresupuesto.getText() + "' WHERE idProductora = " + idProductora;
				statement.executeUpdate(sql);
				
				
				logmanager.registrar(usuarioSesion, "Modificación: " + sql);
				
				lblMensaje.setText("Modificación con éxito");
				dlgMensaje.setVisible(true);
				dlgEdicion.setVisible(false);
				rellenarChoice();
			} catch(Exception e) {
				lblMensaje.setText("Error al modificar");
				dlgMensaje.setVisible(true);
			} finally { cerrarConexion(); }
		} else if(evento.getSource().equals(btnLimpiar)) {
			txtNombre.setText(""); txtPresupuesto.setText(""); txtNombre.requestFocus();
		}
	}
}
