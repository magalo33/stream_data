package com.sophos.streamdata.controller;

import com.sophos.streamdata.StreamDataApplication;
import com.sophos.streamdata.utilidades.ConfiguracionUtilValues;
import com.sophos.streamdata.utilidades.ScannerFile;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
//http://190.27.52.244:9192/stream-data/api/stream/data
//http://localhost:8080/stream-data/api/stream/data

@RequestMapping("/api/stream")
@RestController
public class APIController {
    @GetMapping(value = "/data")
    public ResponseEntity<StreamingResponseBody> streamData() {
        final int milisegundos = Integer.parseInt(ConfiguracionUtilValues.MILISEGUNDOS);
        final String cabeceraXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        final String iuXml = "<[ETIQUETA] X=\"31414\" V=[VALUE] E=\"8005\" S=\"[TIME]\"/>";
        final String hbXml = "<HB T=[TIME] s=\"standby\"/>";
        /*Llama al hilo que escanea la carpeta en busca de archivos para leer informacion*/
        final ScannerFile sf = new ScannerFile();
        Thread hup = new Thread(sf);
        hup.start();
        @SuppressWarnings("SleepWhileInLoop")
        StreamingResponseBody response = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {       
                int indice = 1;
                List<String> listaEnvio = new ArrayList();
                while (true) {
                    try {
                        Thread.sleep(milisegundos);
                    } catch (InterruptedException ex) {
                        StreamDataApplication.registrarErrorLog(APIController.class.getName() + " " + ex.toString());
                    }
                    //Si no hay información cargada en memoria lanza un HB de lo contrario envia IU al cliente
                    try{
                        
                        if (!sf.isListacargada()) {
                            Thread.sleep(100);
                            String datoEnvio = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><HB T="+(Calendar.getInstance().getTimeInMillis() + "")+" s=\"standby\"/>";
                            System.out.println("enviando---> " + datoEnvio);
                            outputStream.write((datoEnvio + "\n").getBytes());
                            outputStream.flush();                        
                    } else {
                            listaEnvio = new ArrayList();
                        try{
                            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nINICIANDO ENVIO\n");
                            Calendar cal0 = Calendar.getInstance();
                            long t1 = cal0.getTimeInMillis();

                            for (int i = 0; i < sf.getListaDataEntrada().size(); i++) {
                                double d = sf.getListaDataEntrada().get(i);
                                String etiqueta = "IU";
                                if (i == 0) {
                                    etiqueta = "IO";
                                } else if ((i + 1) == sf.getListaDataEntrada().size()) {
                                    etiqueta = "IC";
                                }
                                Calendar cal = Calendar.getInstance();
                                long t = cal.getTimeInMillis();
                                String ping = "\"" + d + "\"";
                                String datoEnvio = cabeceraXml.concat(iuXml.replace("[ETIQUETA]", etiqueta).replace("[TIME]", (t + "")).replace("[VALUE]", ping));
                                //if(!listaEnvio.contains(ping)){
                                    ///listaEnvio.add(ping);
                                    while((datoEnvio + "\n").getBytes() == null){
                                        System.out.println(datoEnvio+"<----cargando");
                                    }
                                    if(i%600==0){
                                        Thread.sleep(500);
                                    }
                                    
                                    outputStream.write((datoEnvio + "\n").getBytes());                            
                                    outputStream.flush();
                                    indice+=1;
                                    System.out.println(indice+"-enviando " + ping);
                                    
                                ///}
                            }
                            sf.setListacargada(false);
                            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nFIN ENVIO\n");
                            Calendar cal2 = Calendar.getInstance();
                            long t2 = cal2.getTimeInMillis();
                            long nt= (t2-t1)/1000;
                            System.out.println("tiempo de ejecución "+nt);
                            Thread.sleep(500);
                        }catch(Exception e){
                            System.out.println("Error controlado "+e.toString());
                            try {
                                Thread.sleep(milisegundos);
                            } catch (InterruptedException ex) {
                                StreamDataApplication.registrarErrorLog(APIController.class.getName() + " " + ex.toString());
                            }
                        }                        
                    }
                    }catch(Exception e){
                        System.out.println("Error técnico "+e.toString());
                        System.out.println("tamaño de la lista "+sf.getListaDataEntrada().size());
                        try {
                            System.out.println("Reconectando ");
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            StreamDataApplication.registrarErrorLog(APIController.class.getName() + " " + ex.toString());
                        }
                    }                    
                }
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(response);
    }
    
}
