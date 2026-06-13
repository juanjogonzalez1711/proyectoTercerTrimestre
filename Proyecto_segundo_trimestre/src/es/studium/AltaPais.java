package es.studium;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AltaPais extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Alta Pais");
    TextField txtNombrePais = new TextField(10);
    Button btnAceptar = new Button("Aceptar");
    Button btnLim = new Button("Limpiar");
    Label lblNombrePais = new Label("Nombre País:");
    
    Panel panelNombrePais = new Panel();
    Panel panelBotones = new Panel();
    
    Dialog dlgFeedback = new Dialog(ventana, "Alta completada", true);
    Label lblMensaje = new Label("¡Operación realizada correctamente!");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/cine";
    String login = "root";
    String password = "studium2025;";
    
    
    String usuarioSesion = "usuario"; 

    public AltaPais() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(300, 200);
        ventana.addWindowListener(this);

        btnAceptar.addActionListener(this);
        btnLim.addActionListener(this);

        panelNombrePais.add(lblNombrePais);
        panelNombrePais.add(txtNombrePais);
        ventana.add(panelNombrePais);

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
        new AltaPais();
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

                String valorPais = txtNombrePais.getText();
                String sql = "INSERT INTO paises VALUES (null, '" + valorPais + "')";
                
                statement.executeUpdate(sql);
                
                
                logmanager.registrar(usuarioSesion, "Alta: INSERT INTO paises (nombrePais) VALUES ('" + valorPais + "')");
                
                txtNombrePais.setText("");
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
            txtNombrePais.setText("");
        }
    }
}