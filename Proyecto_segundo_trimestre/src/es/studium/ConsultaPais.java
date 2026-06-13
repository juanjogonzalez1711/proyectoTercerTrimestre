package es.studium;

import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

public class ConsultaPais extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Consulta Países");
	TextArea txaPaises = new TextArea(10, 40);
	Button btnActualizar = new Button("Actualizar");
	Button btnExportarPDF = new Button("Exportar a PDF"); 
	
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", usuario = "root", clave = "studium2025;";
	Connection con = null; Statement st = null; ResultSet rs = null;

	
	String usuarioSesion = "administrador"; 

	public ConsultaPais() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(350, 270);
		ventana.addWindowListener(this);
		btnActualizar.addActionListener(this);
		btnExportarPDF.addActionListener(this);
		
		ventana.add(txaPaises);
		ventana.add(btnActualizar);
		ventana.add(btnExportarPDF);
		
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args) { new ConsultaPais(); }

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
			rs = st.executeQuery("SELECT * FROM paises");
			txaPaises.setText(""); 
			while(rs.next()) {
				txaPaises.append(rs.getInt("idPais") + " - " + rs.getString("nombrePais") + "\n");
			}
		} catch(Exception e) { 
			System.err.println("Error al cargar: " + e.getMessage());
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}

	
	private void exportarAPDF() {
		try {
			String destino = "reporte_paises.pdf";
			
			
			PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
			PdfDocument pdf = new PdfDocument(writer);
			Document documento = new Document(pdf);

			
			Paragraph titulo = new Paragraph("LISTADO DE PAÍSES - SISTEMA CINE")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(18)
					.setBold();
			documento.add(titulo);

			
			float[] columnasAncho = {1f, 3f}; 
			Table tablaPDF = new Table(columnasAncho);
			tablaPDF.useAllAvailableWidth(); 

			
			tablaPDF.addHeaderCell(new Paragraph("ID País").setBold());
			tablaPDF.addHeaderCell(new Paragraph("Nombre del País").setBold());

			
			Class.forName(driver);
			con = DriverManager.getConnection(url, usuario, clave);
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM paises");

			while (rs.next()) {
				tablaPDF.addCell(String.valueOf(rs.getInt("idPais")));
				tablaPDF.addCell(rs.getString("nombrePais"));
			}

			
			documento.add(tablaPDF);
			documento.close(); 

			
			logmanager.registrar(usuarioSesion, "Exportar PDF: Archivo 'reporte_paises.pdf' generado desde consulta países.");

			txaPaises.append("\n[¡PDF generado en la raíz del proyecto y registrado en el Log!]\n");

		} catch (Exception e) {
			System.err.println("Error iText7: " + e.getMessage());
			txaPaises.append("\nError al generar PDF.\n");
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}
}
