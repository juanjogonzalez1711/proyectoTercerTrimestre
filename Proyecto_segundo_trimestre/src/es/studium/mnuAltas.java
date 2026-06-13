package es.studium;
import java.awt.*;
import java.awt.event.*;

public class mnuAltas extends WindowAdapter implements ActionListener {
	Frame ventana = new Frame("Menú de Altas");
	Button btnAltaPelicula = new Button("Alta Película");
	Button btnAltaProductora = new Button("Alta Productora");
	Button btnAltaPais = new Button("Alta País");
	Button btnVolver = new Button("Volver");

	public mnuAltas() {
		
		ventana.setLayout(new GridLayout(4, 1, 10, 10)); 
		ventana.setSize(250, 300);
		ventana.addWindowListener(this);

		btnAltaPelicula.addActionListener(this);
		btnAltaProductora.addActionListener(this);
		btnAltaPais.addActionListener(this);
		btnVolver.addActionListener(this);

		ventana.add(btnAltaPelicula);
		ventana.add(btnAltaProductora);
		ventana.add(btnAltaPais);
		ventana.add(btnVolver);

		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnAltaPelicula)) {
			new AltaPelicula();
		} else if (e.getSource().equals(btnAltaProductora)) {
			new AltaProductora();
		} else if (e.getSource().equals(btnAltaPais)) {
			new AltaPais();
		} else if (e.getSource().equals(btnVolver)) {
			ventana.dispose(); 
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		ventana.dispose(); 
	}
}