package es.studium;
import java.awt.*;
import java.awt.event.*;

public class Principal extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Menú Principal");
	MenuBar barraMenu = new MenuBar();
	
	Menu mnuAltas = new Menu("Altas"), mnuBajas = new Menu("Bajas");
	Menu mnuMod = new Menu("Modificaciones"), mnuCons = new Menu("Consultas");

	MenuItem iAltaPeli = new MenuItem("Películas"), iAltaProd = new MenuItem("Productoras"), iAltaPais = new MenuItem("Países");
	MenuItem iBajaPeli = new MenuItem("Películas"), iBajaProd = new MenuItem("Productoras"), iBajaPais = new MenuItem("Países");
	MenuItem iModPeli = new MenuItem("Películas"), iModProd = new MenuItem("Productoras"), iModPais = new MenuItem("Países");
	MenuItem iConsPeli = new MenuItem("Películas"), iConsProd = new MenuItem("Productoras"), iConsPais = new MenuItem("Países");

	public Principal(boolean esAdmin) {
		ventana.setSize(500, 400);
		ventana.setMenuBar(barraMenu);
		ventana.addWindowListener(this);

		mnuAltas.add(iAltaPeli); mnuAltas.add(iAltaProd); mnuAltas.add(iAltaPais);
		mnuBajas.add(iBajaPeli); mnuBajas.add(iBajaProd); mnuBajas.add(iBajaPais);
		mnuMod.add(iModPeli); mnuMod.add(iModProd); mnuMod.add(iModPais);
		mnuCons.add(iConsPeli); mnuCons.add(iConsProd); mnuCons.add(iConsPais);

		barraMenu.add(mnuAltas); barraMenu.add(mnuBajas); barraMenu.add(mnuMod); barraMenu.add(mnuCons);

		
		if (!esAdmin) {
			mnuBajas.setEnabled(false);
			mnuMod.setEnabled(false);
			mnuCons.setEnabled(true); 
			ventana.setTitle("Cine - MODO USUARIO (Altas y Consultas)");
		} else {
			ventana.setTitle("Cine - MODO ADMINISTRADOR (Acceso Total)");
		}

		iAltaPeli.addActionListener(this); iAltaProd.addActionListener(this); iAltaPais.addActionListener(this);
		iBajaPeli.addActionListener(this); iBajaProd.addActionListener(this); iBajaPais.addActionListener(this);
		iModPeli.addActionListener(this); iModProd.addActionListener(this); iModPais.addActionListener(this);
		iConsPeli.addActionListener(this); iConsProd.addActionListener(this); iConsPais.addActionListener(this);

		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src.equals(iAltaPeli)) new AltaPelicula();
		else if(src.equals(iAltaProd)) new AltaProductora();
		else if(src.equals(iAltaPais)) new AltaPais();
		
		else if(src.equals(iBajaPeli)) new BajaPelicula();
		else if(src.equals(iBajaProd)) new BajaProductora();
		else if(src.equals(iBajaPais)) new BajaPais();
		
		
		else if(src.equals(iModPeli)) new ModificarPeliculas();
		else if(src.equals(iModProd)) new modificarProductora(); 
		else if(src.equals(iModPais)) new ModificarPais();
		
		else if(src.equals(iConsPeli)) new ConsultaPelicula();
		else if(src.equals(iConsProd)) new ConsultaProductora();
		else if(src.equals(iConsPais)) new ConsultaPais();
	}

	@Override public void windowClosing(WindowEvent e) { System.exit(0); }
}