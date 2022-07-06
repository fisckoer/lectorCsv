package com.gva.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SrvIntCsvRead {

    public static void main(String[] args)  {
        try {
            new SrvIntCsvRead().processCsv();
        }catch(Exception ex){
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "Algo salio mal :'( :"+ex.getMessage());
            jFrame.dispose();
        }
    }

    public static final char SEPARATOR=',';
    public static final char QUOTE='"';
    private List<String[]> datos = new ArrayList<>();


    public SrvIntCsvRead(){}


    public void processCsv(){
        String file = "export.csv";
        JFrame jFrame = new JFrame();
        try {
            CSVReader reader = null;
            reader = new CSVReader(new FileReader(file), SEPARATOR, QUOTE);
            reader.forEach(f->reFilleFile(f));
            reader.close();
        }catch (IOException ex){

            JOptionPane.showMessageDialog(jFrame, "Algo salio mal al leer el archivo: "+ex.getMessage()+"\n Validar que el nombre de archivo sea correcto export.csv" );
            jFrame.dispose();
            return;
        }
        datos.get(0)[5]="Cargo";
        datos.get(0)[6]="Abono";
        datos=datos.stream()
                .map(f->addColums(f))
                .map(f->cleanReference(f))
                .map(r->deleteComilla(r))
                .map(r->formatoFecha(r))
                .map(r->deleteSpace(r))
                .collect(Collectors.toList());
        System.out.println(datos.toString());
        datos.stream().forEach(s->System.out.println(Arrays.toString(s)));
        List<String> datosLimpios = datos.stream().map(l->cleanLine(l)).collect(Collectors.toList());
        try {
            PrintWriter writer = new PrintWriter(file.substring(0,file.indexOf('.'))+"_new.txt", "UTF-8");
            datosLimpios.forEach(r->writer.println(r));
            writer.close();
            JOptionPane.showMessageDialog(jFrame, "Se realizo el proceso sin problemas");
        }catch (IOException ex) {
            JOptionPane.showMessageDialog(jFrame, "Algo salio mal al escribir  el archivo: "+ex );
        }
        jFrame.dispose();

    }

    private String[] deleteSpace(String[] r) {
        for(int i =0;i<r.length;i++){
            r[i]=r[i].trim();
        }

        return r;
    }

    private String cleanLine(String[] l) {
        String linea = Arrays.stream(l).collect(Collectors.joining(","));
        linea = linea.replaceAll("'","");
        linea = linea.trim();
        linea = linea.substring(0, linea.length() - 1);
        return linea;
    }

    private String[] cleanReference(String[] f) {
        if(f[8].trim().equals("")){
            f[8]="0";
        }
        return f;
    }

    private String[] formatoFecha(String[] r) {

        String fecha = r[1];
        if(fecha.length()==8) {
            String dia = fecha.substring(0, 2);
            String mes = fecha.substring(2, 4);
            String anio = fecha.substring(4, 8);
            r[1]=dia+"-"+mes+"-"+anio;
        }else if(fecha.length()==7){
            String dia = fecha.substring(0, 1);
            String mes = fecha.substring(1, 3);
            String anio = fecha.substring(3, 7);
            r[1]="0"+dia+"-"+mes+"-"+anio;
        }
        return r;
    }

    private String[] deleteComilla(String[] r) {
        r[1]=r[1].replaceAll("'","");
        for(int i=2;i<r.length-1;i++){
            r[i]=r[i+1];
        }
        r[r.length-1]="";
        return r;
    }

    private String[] addColums(String[] f) {
        if(f[5].equals("-")){
                f[5]=f[6];
                f[6]="";
        }else if(!f[5].equals("Cargo")){
            f[5]="";
        }
        return f;
    }

    private void reFilleFile(String[] f) {
        datos.add(Arrays.copyOf(f,10));
    }


}
