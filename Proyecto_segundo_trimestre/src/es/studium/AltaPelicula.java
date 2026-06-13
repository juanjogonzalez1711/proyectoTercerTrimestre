package es.studium;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AltaPelicula extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Alta Película");
	
	Label lblTitulo = new Label("Título:"), lblFecha = new Label("Fecha (DD/MM/AAAA):"), lblProductora = new Label("Productora:");
	TextField txtTitulo = new TextField(20), txtFecha = new TextField(20);
	Choice choProductoras = new Choice();
	Button btnAceptar = new Button("Aceptar"), btnLimpiar = new Button("Limpiar");
	Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);
	Label lblMensaje = new Label("");
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", login = "root", password = "studium2025;";
	Connection connection = null; Statement statement = null; ResultSet rs = null;

	
	String usuarioSesion = "usuario";

	public AltaPelicula() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(330, 200);
		ventana.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);
		rellenarProductoras();
		ventana.add(lblTitulo); ventana.add(txtTitulo);
		ventana.add(lblFecha); ventana.add(txtFecha);
		ventana.add(lblProductora); ventana.add(choProductoras);
		ventana.add(btnAceptar); ventana.add(btnLimpiar);
		ventana.setLocationRelativeTo(null);
		dlgMensaje.setLayout(new FlowLayout());
		dlgMensaje.setSize(240, 100); 
		dlgMensaje.addWindowListener(this);
		dlgMensaje.add(lblMensaje);
		dlgMensaje.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	private void rellenarProductoras() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT idProductora, nombreProductora FROM productoras");
			choProductoras.removeAll();
			choProductoras.add("Seleccione ID...");
			while(rs.next()) {
				choProductoras.add(rs.getInt("idProductora") + "-" + rs.getString("nombreProductora"));
			}
		} catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
		finally { cerrarConexion(); }
	}

	private void cerrarConexion() {
		try { if(connection != null) connection.close(); } catch(SQLException e) {}
	}

	
	private String convertirFechaAEuropa(String fechaOriginal) throws Exception {
		
		String[] partes = fechaOriginal.split("/");
		if (partes.length != 3) {
			throw new Exception("Formato inválido");
		}
		String dia = partes[0];
		String mes = partes[1];
		String anio = partes[2];
		
		return anio + "-" + mes + "-" + dia;
	}

	public static void main(String[] args) { new AltaPelicula(); }

	@Override
	public void windowClosing(WindowEvent e) {
		if(dlgMensaje.isVisible()) dlgMensaje.setVisible(false);
		else System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnAceptar)) {
			if(choProductoras.getSelectedIndex() != 0 && !txtTitulo.getText().isEmpty() && !txtFecha.getText().isEmpty()) {
				try {
					Class.forName(driver);
					connection = DriverManager.getConnection(url, login, password);
					statement = connection.createStatement();
					
					String idProd = choProductoras.getSelectedItem().split("-")[0];
					
					
					String fechaConvertida = convertirFechaAEuropa(txtFecha.getText());
					
					String titulo = txtTitulo.getText();
					String sql = "INSERT INTO peliculas VALUES (null, '" + titulo + "', '" + fechaConvertida + "', " + idProd + ")";
					statement.executeUpdate(sql);
					
					
					logmanager.registrar(usuarioSesion, "Alta: INSERT INTO peliculas (tituloPelicula, fechaEstreno, idProductoraFK) VALUES ('" + titulo + "', '" + fechaConvertida + "', " + idProd + ")");
					
					lblMensaje.setText("Alta completada");
					txtTitulo.setText(""); txtFecha.setText(""); choProductoras.select(0);
				} catch(Exception ex) {
					lblMensaje.setText("Error en Alta: Verifique la fecha (DD/MM/AAAA)");
				} finally { cerrarConexion(); }
				dlgMensaje.setVisible(true);
			}
		} else if(e.getSource().equals(btnLimpiar)) {
			txtTitulo.setText(""); txtFecha.setText(""); choProductoras.select(0);
		}
	}
}