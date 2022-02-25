package com.sophos.streamdata.utilidades;

import com.sophos.streamdata.StreamDataApplication;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScannerFile implements Runnable{
    private boolean listacargada = false;
    private ArrayList listaDataEntrada = new ArrayList();
    
    @SuppressWarnings("null")
    public ScannerFile() {
        this.listacargada = false;
        this.listaDataEntrada = new ArrayList();   
    }

    /**
     * @return the listacargada
     */
    public boolean isListacargada() {
        return listacargada;
    }

    /**
     * @param listacargada the listacargada to set
     */
    public void setListacargada(boolean listacargada) {
        this.listacargada = listacargada;
    }

    /**
     * @return the listaDataEntrada
     */
    public ArrayList<Double> getListaDataEntrada() {
        return listaDataEntrada;
    }

    /**
     * @param listaDataEntrada the listaDataEntrada to set
     */
    public void setListaDataEntrada(ArrayList<Double> listaDataEntrada) {
        this.listaDataEntrada = listaDataEntrada;
    }

    @Override
    public void run() {
        int milisegundos = Integer.parseInt(ConfiguracionUtilValues.MILISEGUNDOS);
        try {
                /*crear la variable de escucha del archivo base de datos*/
                WatchService service = FileSystems.getDefault().newWatchService();
                try {
                    WatchKey watchKey;
                    Map keyMap = new HashMap();
                    Path path = Paths.get(ConfiguracionUtilValues.LOCATION_FILE, new String[0]);
                    keyMap.put(path.register(service, (WatchEvent.Kind<?>[]) new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE}), path);
                    do {
                        /*Inicia el monitoreo del archivo*/
                        watchKey = service.take();
                        Path evenDir = (Path) keyMap.get(watchKey);
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path eventPath = (Path) event.context();
                            if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                try {
                                    /*Espera 7 segundos para continuar*/
                                    Thread.sleep(7000L);
                                } catch (InterruptedException exception) {

                                }

                                File directory = new File(ConfiguracionUtilValues.LOCATION_FILE);
                                File[] allFilesInDir = directory.listFiles();
                                if (allFilesInDir.length == 0) {
                                    StreamDataApplication.registrarInfoLog("No existes archivos legibles para el programa.");
                                } else {
                                    String fileName = allFilesInDir[0].getName();
                                    String pathh = allFilesInDir[0].getPath();
                                     StreamDataApplication.registrarInfoLog("procesando este path " + pathh);
                                    File file = new File(pathh);

                                    Scanner scan = new Scanner(file);
                                    ArrayList listaDataEntradaStrings = new ArrayList();
                                    listaDataEntrada = new ArrayList();
                                     StreamDataApplication.registrarInfoLog("Inicio procesamiento");
                                    while (scan.hasNextLine()) {
                                        listaDataEntradaStrings.add(scan.nextLine());
                                    }

                                    scan.close();

                                    /*Carga en memoria la lista de datos leidos desde el archivo de texto*/
                                    for (int i=0;i<listaDataEntradaStrings.size();i++) {
                                        String dato = listaDataEntradaStrings.get(i).toString();
                                        try {
                                            listaDataEntrada.add(Double.parseDouble(dato.trim()));
                                        } catch (NumberFormatException e) {
                                             StreamDataApplication.registrarErrorLog("ERROR TRANSFORMANDO EL REGISTRO :" + dato + " " + e);
                                        }
                                    }
                                    /*carga la variable listacargada con true para ser iniciar envio de datos desde el metodo principal*/
                                    this.listacargada = true;

                                    /*Espera a que la información haya sido enviada para proceder a borrar el archivo origen*/
                                    while(this.listacargada){
                                        Thread.sleep(milisegundos);
                                    }
                                    try {
                                        if (new File(pathh).delete()) {
                                            StreamDataApplication.registrarInfoLog("Archivo "+pathh+" eliminado");
                                        } else {
                                             StreamDataApplication.registrarInfoLog("No se pudo eliminar el archivo "+pathh);
                                        }
                                    } catch (Exception e) {
                                         StreamDataApplication.registrarErrorLog("error " + e.toString());
                                    }
                                }

                            }
                        }
                    } while (watchKey.reset());

                    if (service != null) {
                        service.close();
                    }
                } catch (IOException | InterruptedException throwable) {
                    if (service != null) try {
                        service.close();
                    } catch (IOException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
            } catch (IOException | InterruptedException e) {
                StreamDataApplication.registrarErrorLog("El path configruado no es valido, verifique y reinicie el componente");
            }
    }
    
    

    
    
 
}
