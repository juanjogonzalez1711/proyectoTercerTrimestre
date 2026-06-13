package es.studium;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ModificarPeliculas extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Modificar Película");
	Choice choPeliculas = new Choice();
	Button btnEditar = new Button("Editar");
	Dialog dlgEdicion = new Dialog(ventana, "Editando...", true);
	Label lblTituloVentana = new Label("# Editando Película #");
	
	Label lblTitulo = new Label("Título:");
	TextField txtTitulo = new TextField(15);
	Label lblFecha = new Label("Fecha Estreno (DD/MM/AAAA):");
	TextField txtFecha = new TextField(15);
	
	
	Label lblIdProductora = new Label("Productora:");
	Choice choProductoras = new Choice();
	
	Button btnAceptar = new Button("Aceptar"), btnLimpiar = new Button("Limpiar");
	Dialog dlgMensaje = new Dialog(ventana, "Respuesta", true);
	Label lblMensaje = new Label("");
	
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", login = "root", password = "studium2025;", idPelicula = "";
	Connection connection = null; Statement statement = null; ResultSet rs = null;

	String usuarioSesion = "administrador"; 

	
	SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat formatoEuropeo = new SimpleDateFormat("dd/MM/yyyy");

	public ModificarPeliculas() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(320, 200);
		ventana.addWindowListener(this);
		btnEditar.addActionListener(this);
		rellenarChoice();
		
		ventana.add(choPeliculas);
		ventana.add(btnEditar);
		ventana.setLocationRelativeTo(null);
		
		dlgEdicion.setLayout(new FlowLayout());
		dlgEdicion.setSize(280, 300);
		dlgEdicion.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);
		
		dlgEdicion.add(lblTituloVentana);
		dlgEdicion.add(lblTitulo); dlgEdicion.add(txtTitulo);
		dlgEdicion.add(lblFecha); dlgEdicion.add(txtFecha);
		dlgEdicion.add(lblIdProductora); dlgEdicion.add(choProductoras);
		dlgEdicion.add(btnAceptar); dlgEdicion.add(btnLimpiar);
		dlgEdicion.setLocationRelativeTo(null);
		
		dlgMensaje.setLayout(new FlowLayout());
		dlgMensaje.setSize(300, 120);
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
			rs = statement.executeQuery("SELECT * FROM peliculas");
			choPeliculas.removeAll();
			choPeliculas.add("Seleccionar una película...");
			while(rs.next()) {
				choPeliculas.add(rs.getInt("idPelicula") + " " + rs.getString("tituloPelicula"));
			}
		} catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
		finally { cerrarConexion(); }
	}

	
	private void cargarProductorasEnChoice() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT idProductora, nombreProductora FROM productoras");
			choProductoras.removeAll();
			while(rs.next()) {
				choProductoras.add(rs.getInt("idProductora") + " - " + rs.getString("nombreProductora"));
			}
		} catch(Exception e) { System.err.println("Error productoras: " + e.getMessage()); }
		finally { cerrarConexion(); }
	}

	private void cerrarConexion() {
		try { if(connection != null) connection.close(); } catch(SQLException e) {}
	}

	public static void main(String[] args) { new ModificarPeliculas(); }

	@Override
	public void windowClosing(WindowEvent e) {
		if(dlgEdicion.isVisible()) dlgEdicion.setVisible(false);
		else if(dlgMensaje.isVisible()) dlgMensaje.setVisible(false);
		else System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		if(evento.getSource().equals(btnEditar)) {
			if(choPeliculas.getSelectedIndex() != 0) {
				idPelicula = choPeliculas.getSelectedItem().split(" ")[0];
				
				
				cargarProductorasEnChoice();
				
				try {
					Class.forName(driver);
					connection = DriverManager.getConnection(url, login, password);
					statement = connection.createStatement();
					rs = statement.executeQuery("SELECT * FROM peliculas WHERE idPelicula = " + idPelicula);
					if(rs.next()) {
						txtTitulo.setText(rs.getString("tituloPelicula"));
						
						
						String fechaMySQL = rs.getString("fechaEstreno");
						try {
							java.util.Date d = formatoMySQL.parse(fechaMySQL);
							txtFecha.setText(formatoEuropeo.format(d));
						} catch(Exception ex) { txtFecha.setText(fechaMySQL); }
						
						
						int idProductoraActual = rs.getInt("idProductoraFK");
						for(int i = 0; i < choProductoras.getItemCount(); i++) {
							if(choProductoras.getItem(i).startsWith(idProductoraActual + " ")) {
								choProductoras.select(i);
								break;
							}
						}
						
						lblTituloVentana.setText("Editando ID: " + idPelicula);
						dlgEdicion.setVisible(true);
					}
				} catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
				finally { cerrarConexion(); }
			}
		} else if(evento.getSource().equals(btnAceptar)) {
			try {
				
				String fechaIntroducida = txtFecha.getText();
				java.util.Date d = formatoEuropeo.parse(fechaIntroducida);
				String fechaParaBD = formatoMySQL.format(d);
				
				
				String idProductoraSeleccionada = choProductoras.getSelectedItem().split(" ")[0];

				Class.forName(driver);
				connection = DriverManager.getConnection(url, login, password);
				statement = connection.createStatement();
				
				String sql = "UPDATE peliculas SET tituloPelicula = '" + txtTitulo.getText() + 
				             "', fechaEstreno = '" + fechaParaBD + 
				             "', idProductoraFK = " + idProductoraSeleccionada + 
				             " WHERE idPelicula = " + idPelicula;
				
				statement.executeUpdate(sql);
				
				
				logmanager.registrar(usuarioSesion, "Modificación: " + sql);
				
				lblMensaje.setText("Modificación con éxito");
				dlgMensaje.setVisible(true);
				dlgEdicion.setVisible(false);
				rellenarChoice();
			} catch(Exception e) {
				lblMensaje.setText("Error al modificar: Comprueba el formato de fecha (DD/MM/AAAA)");
				dlgMensaje.setVisible(true);
			} finally { cerrarConexion(); }
		} else if(evento.getSource().equals(btnLimpiar)) {
			txtTitulo.setText(""); txtFecha.setText(""); txtTitulo.requestFocus();
		}
	}
}