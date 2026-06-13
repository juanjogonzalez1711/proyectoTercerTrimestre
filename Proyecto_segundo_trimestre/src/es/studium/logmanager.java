package es.studium;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class logmanager {
    private static final String FILE_NAME = "movimientos.log";
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void registrar(String usuario, String accion) {
        String fechaHora = LocalDateTime.now().format(FORMATTER);
        
        String lineaLog = "[" + fechaHora + "] [" + usuario + "] [" + accion + "]";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(lineaLog);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el fichero de log: " + e.getMessage());
        }
    }
}
