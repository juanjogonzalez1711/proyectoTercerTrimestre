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

public class ConsultaProductora extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Consulta Productoras");
	TextArea txaProductoras = new TextArea(10, 50);
	Button btnActualizar = new Button("Actualizar");
	Button btnExportarPDF = new Button("Exportar a PDF"); 
	
	String driver = "com.mysql.cj.jdbc.Driver", url = "jdbc:mysql://localhost:3306/cine", usuario = "root", clave = "studium2025;";
	Connection con = null; Statement st = null; ResultSet rs = null;

	String usuarioSesion = "administrador"; 

	public ConsultaProductora() {
		ventana.setLayout(new FlowLayout());
		ventana.setSize(450, 270); 
		ventana.addWindowListener(this);
		btnActualizar.addActionListener(this);
		btnExportarPDF.addActionListener(this);
		
		ventana.add(txaProductoras);
		ventana.add(btnActualizar);
		ventana.add(btnExportarPDF);
		
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args) { new ConsultaProductora(); }

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
			rs = st.executeQuery("SELECT * FROM productoras");
			txaProductoras.setText(""); 
			while(rs.next()) {
				txaProductoras.append(rs.getInt("idProductora") + " - " + 
						rs.getString("nombreProductora") + " - " + 
						rs.getString("presupuestoProductora") + "€\n");
			}
		} catch(Exception e) { 
			System.err.println("Error al cargar: " + e.getMessage());
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}

	
	private void exportarAPDF() {
		try {
			String destino = "reporte_productoras.pdf";
			
			PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
			PdfDocument pdf = new PdfDocument(writer);
			Document documento = new Document(pdf);

			
			Paragraph titulo = new Paragraph("LISTADO DE PRODUCTORAS - SISTEMA CINE")
					.setTextAlignment(TextAlignment.CENTER)
					.setFontSize(18)
					.setBold();
			documento.add(titulo);

			
			float[] columnasAncho = {1f, 3f, 2f}; 
			Table tablaPDF = new Table(columnasAncho);
			tablaPDF.useAllAvailableWidth();

			tablaPDF.addHeaderCell(new Paragraph("ID").setBold());
			tablaPDF.addHeaderCell(new Paragraph("Nombre Productora").setBold());
			tablaPDF.addHeaderCell(new Paragraph("Presupuesto").setBold());

			// Consulta a la Base de Datos
			Class.forName(driver);
			con = DriverManager.getConnection(url, usuario, clave);
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM productoras");

			while (rs.next()) {
				tablaPDF.addCell(String.valueOf(rs.getInt("idProductora")));
				tablaPDF.addCell(rs.getString("nombreProductora"));
				tablaPDF.addCell(rs.getString("presupuestoProductora") + " €");
			}

			documento.add(tablaPDF);
			documento.close(); 

			
			logmanager.registrar(usuarioSesion, "Exportar PDF: Archivo 'reporte_productoras.pdf' generado desde consulta productoras.");

			txaPaisesAppend("[¡PDF 'reporte_productoras.pdf' generado y registrado con éxito!]\n");

		} catch (Exception e) {
			System.err.println("Error iText7 productoras: " + e.getMessage());
			txaProductoras.append("\nError al generar PDF de productoras.\n");
		} finally {
			try { if(con != null) con.close(); } catch(SQLException e) {}
		}
	}

	
	private void txaPaisesAppend(String texto) {
		txaProductoras.append("\n" + texto);
	}
}
