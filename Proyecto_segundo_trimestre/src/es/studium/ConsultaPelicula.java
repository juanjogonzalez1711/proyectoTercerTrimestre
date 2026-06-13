package es.studium;

import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

public class ConsultaPelicula extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Consulta Películas");
	TextArea txaPeliculas = new TextArea(10, 55);
	Button btnActualizar = new Button("Actualizar");
	Button btnExportarPDF = new Button("Exportar a PDF"); 
	
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", usuario = "root", clave = "studium2025;";
	Connection con = null; Statement st = null; ResultSet rs = null;

	String usuarioSesion = "administrador";

	public ConsultaPelicula() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(480, 270);
		ventana.addWindowListener(this);
		btnActualizar.addActionListener(this);
		btnExportarPDF.addActionListener(this);
		
		ventana.add(txaPeliculas);
		ventana.add(btnActualizar);
		ventana.add(btnExportarPDF);
		
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args) { new ConsultaPelicula(); }

	@Override public void windowClosing(WindowEvent e) { System.exit(0); }

	@Override public void actionPerformed(ActionEvent evento) {
		if (evento.getSource().equals(btnActualizar)) {
			cargarDatos();
		} else if (evento.getSource().equals(btnExportarPDF)) {
			exportarAPDF();
		}
	}

	private void cargarDatos() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, usuario, clave);
			st = con.createStatement();
			
			
			String sql = "SELECT p.idPelicula, p.tituloPelicula, p.fechaEstreno, pr.nombreProductora " +
			             "FROM peliculas p " +
			             "INNER JOIN productoras pr ON p.idProductoraFK = pr.idProductora";
			
			rs = st.executeQuery(sql);
			txaPeliculas.setText(""); 
			
			
			SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatoEuropeo = new SimpleDateFormat("dd/mm/yyyy");

			while(rs.next()) {
				String fechaOriginal = rs.getString("fechaEstreno");
				String fechaFormateada = fechaOriginal;
				
				try {
					java.util.Date fechaParseada = formatoMySQL.parse(fechaOriginal);
					fechaFormateada = formatoEuropeo.format(fechaParseada);
				} catch (Exception ex) {}

				
				txaPeliculas.append(rs.getInt("idPelicula") + " - " + 
				                    rs.getString("tituloPelicula") + " - " + 
				                    fechaFormateada + " - Prod: " + 
				                    rs.getString("nombreProductora") + "\n");
			}
		} catch(Exception e) { 
			System.err.println("Error al cargar: " + e.getMessage());
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}

	
	private void exportarAPDF() {
		try {
			String destino = "reporte_peliculas.pdf";
			
			PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
			PdfDocument pdf = new PdfDocument(writer);
			Document documento = new Document(pdf);

			Paragraph titulo = new Paragraph("LISTADO DE PELÍCULAS - SISTEMA CINE")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(18)
					.setBold();
			documento.add(titulo);

			
			float[] columnasAncho = {3f, 2f, 3f}; 
			Table tablaPDF = new Table(columnasAncho);
			tablaPDF.useAllAvailableWidth();

			tablaPDF.addHeaderCell(new Paragraph("Título Película").setBold());
			tablaPDF.addHeaderCell(new Paragraph("Fecha Estreno").setBold());
			tablaPDF.addHeaderCell(new Paragraph("Productora").setBold());

			Class.forName(driver);
			con = DriverManager.getConnection(url, usuario, clave);
			st = con.createStatement();
			
			String sql = "SELECT p.tituloPelicula, p.fechaEstreno, pr.nombreProductora " +
			             "FROM peliculas p " +
			             "INNER JOIN productoras pr ON p.idProductoraFK = pr.idProductora";
			rs = st.executeQuery(sql);

			SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatoEuropeo = new SimpleDateFormat("dd/MM/yyyy");

			while (rs.next()) {
				tablaPDF.addCell(rs.getString("tituloPelicula"));
				
				
				String fechaOriginal = rs.getString("fechaEstreno");
				String fechaFormateada = fechaOriginal;
				try {
					java.util.Date fechaParseada = formatoMySQL.parse(fechaOriginal);
					fechaFormateada = formatoEuropeo.format(fechaParseada);
				} catch (Exception ex) {}
				
				tablaPDF.addCell(fechaFormateada);
				tablaPDF.addCell(rs.getString("nombreProductora"));
			}

			documento.add(tablaPDF);
			documento.close(); 

			
			logmanager.registrar(usuarioSesion, "Exportar PDF: Archivo 'reporte_peliculas.pdf' generado con éxito mediante consulta JOIN.");

			txaPeliculas.append("\n[¡PDF 'reporte_peliculas.pdf' exportado y registrado en el Log!]\n");

		} catch (Exception e) {
			System.err.println("Error iText7 en películas: " + e.getMessage());
			txaPeliculas.append("\nError al generar PDF de películas.\n");
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}
}