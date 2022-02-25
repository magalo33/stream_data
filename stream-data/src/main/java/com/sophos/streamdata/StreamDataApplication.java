package com.sophos.streamdata;

import com.sophos.streamdata.utilidades.ConfiguracionAplicaccion;
import com.sophos.streamdata.utilidades.ConfiguracionUtilValues;
import com.sophos.streamdata.utilidades.Log;
import com.sophos.streamdata.utilidades.ScannerFile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamDataApplication {
    private static Log log;
    public static boolean iniciado = false;
    public static void main(String[] args) {
        try {
            ConfiguracionAplicaccion.getConfiguracion();
            StreamDataApplication.log = new Log(ConfiguracionUtilValues.LOG_FILE_PATH);
            try {
                Thread.sleep(2000);
                iniciado = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(StreamDataApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
            SpringApplication.run(StreamDataApplication.class, args);
        } catch (IOException ex) {
            registrarErrorLog(ex.toString());
        }
    }

    
    public static void registrarErrorLog(String msg){
        try {
            if(iniciado)
                StreamDataApplication.log.addLine("[ERROR] "+msg);
        } catch (IOException ex) {
            Logger.getLogger(ScannerFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void registrarInfoLog(String msg){
        try {
            if(iniciado)
                StreamDataApplication.log.addLine("[INFO] "+msg);
        } catch (IOException ex) {
            Logger.getLogger(ScannerFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

       
       
}
