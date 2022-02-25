package com.sophos.streamdata.utilidades;

import com.sophos.streamdata.StreamDataApplication;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfiguracionAplicaccion {

    public static void getConfiguracion() throws IOException {
        try {
            /*Obtiene propiedades desde el archivo de origen de las mismas*/
            Properties extractedProperties = Utils.getConfigs("websocket.properties");
            ConfiguracionUtilValues.LOG_FILE_PATH = extractedProperties.getProperty("LOG_FILE_PATH");
            ConfiguracionUtilValues.LOCATION_FILE = extractedProperties.getProperty("LOCATION_FILE");
            ConfiguracionUtilValues.MILISEGUNDOS = extractedProperties.getProperty("MILISEGUNDOS");           
           StreamDataApplication.registrarInfoLog("Se asignó correctamente las varibles desde el archivo de propiedades");
        } catch (FileNotFoundException e1) {
            StreamDataApplication.registrarErrorLog(e1.toString());
        } catch (IOException e1) {
             StreamDataApplication.registrarErrorLog(e1.toString());
        }
    }
}
