package es.studium;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AltaProductora extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Alta Productora");
    TextField txtNombre = new TextField(10);
    TextField txtPresupuesto = new TextField(10);
    Button btnAceptar = new Button("Aceptar");
    Button btnLim = new Button("Limpiar");
    Label lblNombre = new Label("Nombre Productora:");
    Label lblPresupuesto = new Label("Presupuesto:");
    
    Panel panelNombre = new Panel();
    Panel panelPresupuesto = new Panel();
    Panel panelBotones = new Panel();
    
    Dialog dlgFeedback = new Dialog(ventana, "Alta completada", true);
    Label lblMensaje = new Label("¡Operación realizada correctamente!");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/cine";
    String login = "root";
    String password = "studium2025;";

    
    String usuarioSesion = "usuario";

    public AltaProductora() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(300, 200);
        ventana.addWindowListener(this);

        btnAceptar.addActionListener(this);
        btnLim.addActionListener(this);

        panelNombre.add(lblNombre);
        panelNombre.add(txtNombre);
        ventana.add(panelNombre);

        panelPresupuesto.add(lblPresupuesto);
        panelPresupuesto.add(txtPresupuesto);
        ventana.add(panelPresupuesto);

        panelBotones.add(btnAceptar);
        panelBotones.add(btnLim);
        ventana.add(panelBotones);

        ventana.setLocationRelativeTo(null);
        
        dlgFeedback.setLayout(new FlowLayout());
        dlgFeedback.add(lblMensaje);
        dlgFeedback.setSize(250, 100);
        dlgFeedback.addWindowListener(this);
        dlgFeedback.setLocationRelativeTo(null);

        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        new AltaProductora();
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if (we.getSource().equals(dlgFeedback)) {
            dlgFeedback.setVisible(false);
        } else {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource().equals(btnAceptar)) {
            Connection connection = null;
            Statement statement = null;
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url, login, password);
                statement = connection.createStatement();

                String nombre = txtNombre.getText();
                String presupuesto = txtPresupuesto.getText();
                String sql = "INSERT INTO productoras VALUES (null, '" + nombre + "', " + presupuesto + ")";
                
                statement.executeUpdate(sql);
                
               
                logmanager.registrar(usuarioSesion, "Alta: INSERT INTO productoras (nombreProductora, presupuestoProductora) VALUES ('" + nombre + "', " + presupuesto + ")");
                
                txtNombre.setText("");
                txtPresupuesto.setText("");
                dlgFeedback.setVisible(true);

            } catch (ClassNotFoundException e) {
                System.err.println("Driver no encontrado");
            } catch (SQLException e) {
                System.err.println("Error SQL: " + e.getMessage());
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar");
                }
            }
        } else if (evento.getSource().equals(btnLim)) {
            txtNombre.setText("");
            txtPresupuesto.setText("");
        }
    }
}