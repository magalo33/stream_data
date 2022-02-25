package com.sophos.streamdata.utilidades;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    /*Lee las propiedades desde el archivo origen*/
    public static Properties getConfigs(String rutaProperties) throws FileNotFoundException, IOException {
        Properties configs = new Properties();
        configs.load(new FileInputStream(rutaProperties));
        return configs;
    }

}
