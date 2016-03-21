/*
 * VCO2.java
 *
 * ADICO: Desarrollo de dos versiones de una Aplicación para el DIseño de COcinas,
 * una con interfaz de usuario tipo WIMP y otra con interfaz IGO.
 *
 * Realizado por: Manuel Flores Vivas <mflores at alu.uma.es>
 * Tutor: Antonio Luis Carrillo León <alcarrillo at uma.es>
 *
 * Proyecto Fin de Carrera
 * Ingeniería Técnica en Informática de Sistemas (Universidad de Málaga)
 */

/*
 * NOTA: Para cambiar el comportamiento del paso Decide se puede
 * cambiar el valor de la variable 'modoDecide' en la linea 48
 *
 * private int modoDecide = 4; //1=Con boton Decide, 2=Mensaje en estado, 3=Mensaje en ventanita, 4=Mensaje incrustado en VCO, 5=Mensaje incrustado siempre visible
 *
 * - El valor 1 es el que hemos usado siempre.
 * - El valor 2 lo notifica abajo en la barra de estado.
 * - El valor 3 muestra una ventanita.
 * - El valor 4 incrusta el mensaje dentro de la VCO, mostrandose cuando se alcanza ese paso.
 * - Y el valor 5 incrusta el mensaje en la VCO pero queda siempre visible.
 *
 * La variable autoclick puesta a true permite que se pulse un boton automaticamente al realizar cierta tarea en la ventana de trabajo
 *
 * Y justo a continuación se encuentran las variables referentes a colores y fuentes.
 */


package igo;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import controlador.*;

//Clase encargada de crear y gestionar una ventana conductora de objetivos

/**
 * Clase encargada de crear y gestionar una ventana conductora de objetivos
 *
 * @author Manuel Flores Vivas
 * @version 2.0
 *
 * Basado en el codigo generado automaticamente por el PFC de Manuel Cruces Jiménez
 *
 */
public class VCO2 extends JPanel {
    
    //Modo del Decide en la VCO
    private int modoDecide = 5; //1=Con boton Decide, 2=Mensaje en estado, 3=Mensaje en ventanita, 4=Mensaje incrustado en VCO, 5=Mensaje incrustado siempre visible
    
    //Auto-click
    private boolean autoclick = true; //controla que se pulse un boton automaticamente al realizar cierta tarea en la ventana de trabajo
    
    //COLORES
    //Color de fondo del panel
    private Color colorFondo=new Color(40,100,140);
    //Color de la letra del panel
    private Color colorLetra=new Color(220,220,255);
    //Color de fondo de boton activo
    private Color colorBotonActivo = new Color(110,180,240);
    //Color de fondo de boton cancelar activo
    private Color colorBotonCancelableActivo = new Color(150,200,200);
    //Color de fondo de boton activo mas resaltado (para los decides)
    private Color colorBotonActivoRe=new Color(30,160,255); //=colorBotonActivo;
    //Color de la letra de un boton activo
    private Color colorLetraBotonActivo = Color.BLACK;
    //Color de fondo de boton inactivo
    private Color colorBotonInactivo = colorFondo; //new Color(240,240,240)
    //Color de la letra de un boton inactivo
    private Color colorLetraBotonInactivo = new Color(160,160,160); //Color.GRAY
    //Color de la letra del decide incrustado
    private Color colorLabelDecide = new Color(10,10,10);
    
    //FUENTES
    //Fuente de los botones
    private Font fuente = new Font(Font.SANS_SERIF,Font.BOLD,11); //10 en Linux, 11 en Windows
    //Fuente de los títulos de los paneles
    private Font fuenteTitulo = new Font(Font.SANS_SERIF,Font.BOLD,12);
    
    //Paneles que contendran los objetivos
    private JPanel[] panel;
    //Panel que contendra el panel actual que se muestra por pantalla.
    private JPanel pActual=null;
    //Botones de los objetivos
    private JButton[][] boton;
    //Iconos de los objetivos
    private JLabel[][] icono;
    //tipos (make, init, decice, return)
    private char[][] tipoboton;
    //Etiquetas que contendran los titulos de los paneles
    private JLabel[] titulos;
    //Botones Cancelar
    private JButton[] bCancelable;
    private JLabel[] icCancelable;
    //Tipos (Metodo o Selector)
    private char[] tipoPanel;
    //Pila donde meteremos los métodos padres.
    private Stack<ElementoPilaVCO> pilaVCO;
    //Pila donde meteremos los paneles incrustados
    private Stack<JPanel> pilaPanelesIncrustados;
    //identificadores
    private int idPanelActual, lineaActual;
    //label donde incrustar el mensaje del decide en modo 4
    private JPanel panelDecideIncrustado = null;
    
    //Listener para cambios
    private ActionListener listenerCambio = null;
    
    //Estructura Disable-If
    private Map<JButton, String> condicionesDI = new HashMap<JButton, String>();
    private Map<JButton, Integer> valoresDI = new HashMap<JButton, Integer>();
    
    //Estructura Selection-For-The-System
    private Map<Integer, String> SELcondiciones = new HashMap<Integer, String>();
    private Map<Integer, Integer> SELrama0 = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> SELrama1 = new HashMap<Integer, Integer>();
    
    //ESTADOS
    //Aplicacion Condicion-Valor para los estados
    private Map<String,Integer> estados = new HashMap<String,Integer>();

    //bloqueo
    private boolean bloqueo = false;
    
    //decide siempre visible
    private JLabel labelDestacado = null;
    
    //Constructor

    /**
     * Crea un nuevo objeto con una Ventana Conductora de Objetivos sin objetivos, hay que a&ntilde;adirle los subobjetivos y los pasos con los m&eacute;todos definePanel y defineBoton*
     */
    public VCO2() {
        //color de fondo
        this.setBackground(colorFondo);
        
        //Añadimos el numero de paneles, que seran necesarios. Uno por cada objetivo con lineas.
        int numPaneles=0; //arrays vacios, seran redimensionados
        //Inicializamos los arrays
        titulos=new JLabel[numPaneles];
        panel=new JPanel[numPaneles];
        boton=new JButton[numPaneles][0]; //sera redimensionado
        icono=new JLabel[numPaneles][0]; 
        tipoboton=new char[numPaneles][0];
        pilaVCO = new Stack<ElementoPilaVCO>();
        pilaPanelesIncrustados = new Stack<JPanel>();
        tipoPanel = new char[numPaneles];
        bCancelable = new JButton[numPaneles];
        icCancelable= new JLabel[numPaneles];    
    }

    /**
     * Establece el modo en que se muestran los 'Decide'
     * @param modo El modo a establecer. 1=Con boton Decide, 2=Mensaje en estado, 3=Mensaje en ventanita, 4=Mensaje incrustado en VCO, 5=Mensaje incrustado siempre visible
     */
    public void setModoDecide(int modo) {
        modoDecide = modo;
    }

    /**
     * Establece si se hace click automaticamente bajo ciertas acciones en la ventana de trabajo
     * @param estado true si se hace click automaticamente, false para deshabilitarlo
     * @see #simularClick() 
     */
    public void setAutoClick(boolean estado) {
        autoclick = estado;
    }

    /**
     * Carga el panel con el objetivo inicial
     */
    public void cargarPanelInicial(){
        cargarPanel(0);
    }

    //define un panel
    //tipo='M' para Método, tipo='S' para Selección

    /**
     * A&ntilde;ade un panel a la estructura
     * @param id Identificador del panel, debe ser &uacute;nico
     * @param titulo Titlo del panel, nombre del subobjetivo
     * @param tipo 'M' para M&eacute;todo; 'S' para Selecci&oacute;n
     * @param rutina Subrutina que se ejecuta al cargar el panel
     * @param rutinaElim Subrutina que se ejecuta al eliminar el panel
     */
    public void definePanel(int id, String titulo, char tipo, ActionListener rutina, ActionListener rutinaElim) {
        //redimensionar arrays si es necesario
        if (panel.length<=id) {
            panel = Arrays.copyOf(panel, id+1);
            tipoPanel = Arrays.copyOf(tipoPanel, id+1);
            titulos = Arrays.copyOf(titulos, id+1);
            boton = Arrays.copyOf(boton, id+1);
            icono = Arrays.copyOf(icono, id+1);
            tipoboton = Arrays.copyOf(tipoboton, id+1);
            bCancelable = Arrays.copyOf(bCancelable, id+1);
            icCancelable = Arrays.copyOf(icCancelable, id+1);
        }
        
        //crear panel
        panel[id] = new JPanel();
        panel[id].setBackground(colorFondo);
        panel[id].setLayout(new BoxLayout(panel[id], BoxLayout.Y_AXIS));
        
        final ActionListener rutina2 = rutina; //para pasarlo a la otra clase tienen que ser final
        final ActionListener rutinaElim2 = rutinaElim;
        //este listener detecta los eventos mostrar y ocultar de un panel, y lo usamos llamando a las rutinas de control
        panel[id].addAncestorListener(new AncestorListener(){
            public void ancestorAdded(AncestorEvent e) { //se muestra el panel
                if (rutina2 != null) { rutina2.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"ancestor")); }
            }
            
            public void ancestorMoved(AncestorEvent e) {} //no lo usamos
            
            public void ancestorRemoved(AncestorEvent e) { //se oculta el panel
                if (rutinaElim2 != null) { rutinaElim2.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"ancestor")); }
            }
        });
        
        //anotar tipo
        tipoPanel[id]=tipo;
        
        //titulo
        titulos[id] = new JLabel(titulo);
        titulos[id].setFont(fuenteTitulo);
        titulos[id].setForeground(colorLetra);
        panel[id].add(titulos[id]);
        
        //botones
        boton[id] = new JButton[0];
        icono[id] = new JLabel[0];
        tipoboton[id] = new char[0];
        
        //boton de cancelar (opcional, pero se crea aqui)
        bCancelable[id]=null;
        icCancelable[id]=null;  
    }

     /**
     * A&ntilde;ade un panel a la estructura
     * @param id Identificador del panel, debe ser &uacute;nico
     * @param titulo Titlo del panel, nombre del subobjetivo
     * @param tipo 'M' para M&eacute;todo; 'S' para Selector
     */
    public void definePanel(int id, String titulo, char tipo) {
        definePanel(id, titulo, tipo, null, null);
    }

     /**
     * A&ntilde;ade un panel a la estructura
     * @param id Identificador del panel, debe ser &uacute;nico
     * @param titulo Titlo del panel, nombre del subobjetivo
     * @param tipo 'M' para M&eacute;todo; 'S' para Selector
     * @param rutina Evento que se ejecuta al cargar el panel
     */
    public void definePanel(int id, String titulo, char tipo, ActionListener rutina) {
        definePanel(id, titulo, tipo, rutina, null);
    }
    
    //añade un boton a un panel
    //tipo='m' //make
    //tipo='i //init
    //tipo='d' //decide
    //tipo='g' //goto (no implementado)
    private void defineBoton(int idpanel, String titulo, char tipo) {
        //crear contenedor
        JPanel paneles = new JPanel();
        paneles.setBackground(colorFondo);
        paneles.setLayout(new BoxLayout(paneles,BoxLayout.X_AXIS));
        
        //crear boton
        int l = boton[idpanel].length;
        boton[idpanel]=Arrays.copyOf(boton[idpanel], l+1); //se redimensiona el array en tiempo de ejecucion (a partir de Java SE 6)
        
        //y crear icono
        icono[idpanel]=Arrays.copyOf(icono[idpanel], l+1);
        
        //anotar tipo
        tipoboton[idpanel]=Arrays.copyOf(tipoboton[idpanel], l+1);
        tipoboton[idpanel][l]=tipo;
                
        //numeros para los metodos, y letras para los selectores
        String prefijo = (tipoPanel[idpanel]=='M') ? Integer.toString(l+1) : Character.toString((char)(((int)'a')+l));
        
        boton[idpanel][l]=new JButton("<html><font size=3>" + prefijo + ".</font> " + titulo + "</html>"); //al ser codigo html, si no cabe en una linea ocupa las necesarias
        boton[idpanel][l].setFont(fuente);
        boton[idpanel][l].setBackground(colorBotonActivo);
        boton[idpanel][l].setForeground(colorLetraBotonActivo);
        boton[idpanel][l].setHorizontalAlignment(JButton.LEFT);
        
        //crear icono
        icono[idpanel][l] = new JLabel(" ");
        icono[idpanel][l].setIcon(new ImageIcon("imagenes"+File.separator+"i.gif")); //o usar tipo
        
        //añadir icono y boton
        paneles.add(icono[idpanel][l]);
        paneles.add(boton[idpanel][l]);
        paneles.setAlignmentX(0);
        
        if ((tipo!='d')||(modoDecide<2))  {
            //añadir panel vacio al panel grande (espaciado)
            if ((modoDecide!=5)||(l==0)||(this.tipoboton[idpanel][l-1]!='d')) { //siempre excepto si arriba hay un decide incrustado
            JPanel panelVacio = new JPanel();
            panelVacio.setBackground(colorFondo);
            panel[idpanel].add(panelVacio);
            }

            //añadir contenedor al panel grande
            panel[idpanel].add(paneles);
        }        
        
        //teclado
        if (l<9) {
            boton[idpanel][l].setMnemonic(((tipoPanel[idpanel]=='M')?KeyEvent.VK_1:KeyEvent.VK_A)+l);
        }
        
        if (tipoPanel[idpanel]=='M') { //Alt + 1..9
            if (l<9) { boton[idpanel][l].setMnemonic(KeyEvent.VK_1+l); }
        } else { //Alt + A..Z
            if (l<26) { boton[idpanel][l].setMnemonic(KeyEvent.VK_A+l); }
        }
    }
    
    //Gestion de eventos de botones
    //defineBotonMake defineBotonInit defineBotonDecide defineBotonGoto
    
    //----------------------------
    //define un boton de tipo Init
    
    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Init' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subobjetivo Identificador del panel que incluye el subobjetivo a hacer en este 'Init'
     */
    public void defineBotonInit(int idpanel, String titulo, int subobjetivo) {
        defineBotonInit(idpanel, titulo, subobjetivo, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Init' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subobjetivo Identificador del panel que incluye el subobjetivo a hacer en este 'Init'
     * @param subrutina Subrutina que se ejecuta al pulsar este bot&oacute;
     */
    public void defineBotonInit(int idpanel, String titulo, int subobjetivo, ActionListener subrutina) {
        defineBotonInitDisableIf(idpanel, titulo, subobjetivo, subrutina, null, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Init' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subobjetivo Identificador del panel que incluye el subobjetivo a hacer en este 'Init'
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonInitDisableIf(int idpanel, String titulo, int subobjetivo, String condicionDI, Integer valorDI) {
        defineBotonInitDisableIf(idpanel, titulo, subobjetivo, null, condicionDI, valorDI);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Init' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subobjetivo Identificador del panel que incluye el subobjetivo a hacer en este 'Init'
     * @param subrutina Subrutina que se ejecuta al pulsar este bot&oacute;n
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonInitDisableIf(int idpanel, String titulo, int subobjetivo, ActionListener subrutina, String condicionDI, Integer valorDI) {
        defineBoton(idpanel, titulo+"...", 'i');
        int l = boton[idpanel].length;
        boton[idpanel][l-1].addActionListener(new InitActionListener(idpanel, l-1,subobjetivo));
        if (subrutina!=null) { boton[idpanel][l-1].addActionListener(subrutina); }
        
        //rellenar estructura disableIf
        if (condicionDI!=null && valorDI!=null) {
            condicionesDI.put(boton[idpanel][l-1],condicionDI);
            valoresDI.put(boton[idpanel][l-1],valorDI);
        }
    }
    
    //----------------------------
    //define un boton de tipo Make

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Make' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     */
    public void defineBotonMake(int idpanel, String titulo) {
        defineBotonMake(idpanel, titulo, null);        
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Make' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subrutina Subrutina que se ejecuta al pulsar este bot&oacute;n
     */
    public void defineBotonMake(int idpanel, String titulo, ActionListener subrutina) {
        defineBotonMakeDisableIf(idpanel, titulo, subrutina, null, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Make' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonMakeDisableIf(int idpanel, String titulo, String condicionDI, Integer valorDI) {
        defineBotonMakeDisableIf(idpanel, titulo, null, condicionDI, valorDI);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Make' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param subrutina Subrutina que se ejecuta al pulsar este bot&oacute;n
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonMakeDisableIf(int idpanel, String titulo, ActionListener subrutina, String condicionDI, Integer valorDI) {
        defineBoton(idpanel, titulo, 'm');
        int l = boton[idpanel].length;
        boton[idpanel][l-1].addActionListener(new MakeActionListener(idpanel,l));
        if (subrutina!=null) { boton[idpanel][l-1].addActionListener(subrutina); }
        
        //rellenar estructura disableIf
        if (condicionDI!=null && valorDI!=null) {
            condicionesDI.put(boton[idpanel][l-1],condicionDI);
            valoresDI.put(boton[idpanel][l-1],valorDI);
        }
    }
    
    //------------------------------
    //define un boton de tipo Decide

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Decide' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param go Paso al que vuelve el decide, se activar&aacute;
     */
    public void defineBotonDecide(int idpanel, String titulo, int go) {
        defineBotonDecide(idpanel, titulo, go, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Decide' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param titulo Nombre del bot&oacute;n
     * @param go Paso al que vuelve el decide, se activar&aacute;
     * @param subrutina Subrutina que se ejecuta al pulsar a este bot&oacute;n
     */
    public void defineBotonDecide(int idpanel, String titulo, int go, ActionListener subrutina) {
        defineBoton(idpanel, titulo, 'd');
        int l = boton[idpanel].length;
        
        boton[idpanel][l-1].addActionListener(new DecideActionListener(idpanel, l, go));
        if (subrutina!=null) { boton[idpanel][l-1].addActionListener(subrutina); }
        
        //decide modo siempre visible
        if (modoDecide==5) { //Incrustado en la VCO

            //crear contenedor
            panelDecideIncrustado = new JPanel();
            panelDecideIncrustado.setBackground(colorFondo);
            panelDecideIncrustado.setLayout(new BoxLayout(panelDecideIncrustado,BoxLayout.X_AXIS));

            JLabel labelDecideIncrustado = new JLabel(
            boton[idpanel][l-1].getText().substring(0,boton[idpanel][l-1].getText().length()-7)+"<br>&nbsp;</html>");
            labelDecideIncrustado.setForeground(colorLetraBotonInactivo);

            //añadir icono y label
            panelDecideIncrustado.add(new JLabel("          "));
            panelDecideIncrustado.add(labelDecideIncrustado);
            panelDecideIncrustado.setAlignmentX(0);
            
            //añadir panel vacio al panel grande (espaciado)
            JPanel panelVacio = new JPanel();
            panelVacio.setBackground(colorFondo);

            panel[idpanel].add(panelVacio);
            panel[idpanel].add(panelDecideIncrustado);
        }
    }
    
    //define un boton de tipo Goto
    /*private void defineBotonGoto(int idpanel, String titulo, int go) {
        defineBoton(idpanel, titulo, 'g');
        int l = boton[idpanel].length;
        boton[idpanel][l-1].addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent e){
                
              }
          });
    }*/
    
    //------------------------------
    //define un boton de tipo Return

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Return' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     */
    public void defineBotonReturn(int idpanel) {
        defineBotonReturn(idpanel, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Return' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param subrutina Subrutina que se ejecuta al pulsar a este bot&oacute;n
     */
    public void defineBotonReturn(int idpanel, ActionListener subrutina) {
        defineBoton(idpanel, "Hecho", 'r');
        int l = boton[idpanel].length;
        boton[idpanel][l-1].addActionListener(new ReturnActionListener(idpanel, 0));
        if (subrutina!=null) { boton[idpanel][l-1].addActionListener(subrutina); }
    }
    
    //--------------
    //boton Cancelar

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Cancelar' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     */
    public void defineBotonCancelar(int idpanel) {
        defineBotonCancelar(idpanel, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Cancelar' a un panel
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param subrutina Subrutina que se ejecuta al pulsar a este bot&oacute;n
     */
    public void defineBotonCancelar(int idpanel, ActionListener subrutina) {
        defineBotonCancelarDisableIf(idpanel, subrutina, null, null);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Cancelar' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonCancelarDisableIf(int idpanel, String condicionDI, Integer valorDI) {
            defineBotonCancelarDisableIf(idpanel, null, condicionDI, valorDI);
    }

    /**
     * A&ntilde;ade un bot&oacute;n de tipo 'Cancelar' a un panel, con opci&oacute;n de desactivarlo 'Disable-If'
     * @param idpanel Identificador del panel al que se a&ntilde;ade
     * @param subrutina Subrutina que se ejecuta al pulsar a este bot&oacute;n
     * @param condicionDI Nombre de la condici&oacute;n que se usar&aacute para desactivar el bot&oacute;n
     * @param valorDI Valor que ha de tener condicionDI para desactivar el bot&oacute;n
     */
    public void defineBotonCancelarDisableIf(int idpanel, ActionListener subrutina, String condicionDI, Integer valorDI) {
        //crear contenedor
        JPanel pCancelable = new JPanel();
        pCancelable.setBackground(colorFondo);
        pCancelable.setLayout(new BoxLayout(pCancelable,BoxLayout.X_AXIS));
        pCancelable.setAlignmentX(0);
        
        //crear boton
        bCancelable[idpanel] = new JButton("Cancelar");
        bCancelable[idpanel].setFont(fuente);
        bCancelable[idpanel].setBackground(colorBotonActivo);
        bCancelable[idpanel].setForeground(colorLetraBotonActivo);
        bCancelable[idpanel].setHorizontalAlignment(JButton.LEFT);
        bCancelable[idpanel].setMaximumSize(new Dimension(220,25));
        bCancelable[idpanel].addActionListener(new ReturnActionListener(idpanel, 1));
        bCancelable[idpanel].addActionListener(subrutina);
          
        //crear icono
        icCancelable[idpanel] = new JLabel(" ");
        icCancelable[idpanel].setIcon(new ImageIcon("imagenes"+File.separator+"c.gif"));
        
        //añadir icono y boton
        pCancelable.add(icCancelable[idpanel]);
        pCancelable.add(bCancelable[idpanel]);
        
        //añadir panel vacio al panel grande (espaciado)
        JPanel panelVacio;
        for (int i=0;i<5;i++) { //para que quede mas separado
            panelVacio = new JPanel();
            panelVacio.setBackground(colorFondo);
            panel[idpanel].add(panelVacio);
        }
        
        //añadir contenedor al panel grande
        panel[idpanel].add(pCancelable);
        
        //teclado
        bCancelable[idpanel].setMnemonic(KeyEvent.VK_0); //Alt+0
        
        //rellenar estructura disableIf
        if (condicionDI!=null && valorDI!=null) {
            condicionesDI.put(bCancelable[idpanel],condicionDI);
            valoresDI.put(bCancelable[idpanel],valorDI);
        }
    }
    
    //seleccion para el sistema
    // (condicion==0 ? rama0 : rama1)

    /**
     * A&ntilde;ade una selecci&oacute;n para el sistema a la estructura
     * @param id Identificador del subobjetivo
     * @param condicion Nombre de la condici&oacute;n a evaluar
     * @param rama0 Identificador del panel con el subobjetivo a mostrar en caso de que la condici&oacute;n sea 0 (falso)
     * @param rama1 Identificador del panel con el subobjetivo a mostrar en caso de que la condici&oacute;n sea mayor de cero (cierto)
     */
    public void defineSeleccionSistema(int id, String condicion, int rama0, int rama1) {
        SELcondiciones.put(id, condicion);
        SELrama0.put(id, rama0);
        SELrama1.put(id, rama1);
    }
    
    //Subclases:
    
    //Gestion de eventos de botones tipo Init
    private class InitActionListener implements ActionListener {
        int idpanel; //panel
        int l; //paso
        int subobjetivo; //nuevo panel
        
        public InitActionListener(int idpanel, int l, int subobjetivo) {
            this.idpanel=idpanel;
            this.l=l;
            this.subobjetivo=subobjetivo;
        }
        
        public void actionPerformed(ActionEvent e) {
            borrarMensajeIncrustado(idpanel);
            //mostrar subobjetivo
            pilaVCO.push(new ElementoPilaVCO(idpanel,l));
            cargarPanel(subobjetivo);
            
            //listener cambio
            if (listenerCambio!=null) { listenerCambio.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"init")); }
        }
    }
    
    //Gestion de eventos de botones tipo Decide
    private class DecideActionListener implements ActionListener {
        int idpanel; //panel
        int l; //paso
        int go; //nuevo paso
        
        public DecideActionListener(int idpanel, int l, int go) {
            this.idpanel=idpanel;
            this.l=l;
            this.go=go;
        }

        public void actionPerformed(ActionEvent e) {
            borrarMensajeIncrustado(idpanel);
            //ofrecer dos opciones
            cambiarPaso(idpanel, l);
            activarBoton(idpanel, go-1, true);

            boton[idpanel][l].setBackground(colorBotonActivoRe);            
            
            lineaActual=go-1; //tenemos dos pasos activos
          
            //listener cambio
            if (listenerCambio!=null) { listenerCambio.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"decide")); }
            
            //Decide en nuevos modos
            if (modoDecide==2) { //En la barra de estado
                        Controlador.getInstance().cambiarEstado(boton[idpanel][l-1].getText().substring(boton[idpanel][l-1].getText().indexOf("</font>")+8,boton[idpanel][l-1].getText().length()-7)+ //);
                                "  (SI => paso "+ go + "; NO => paso " + (l+1)+")");

            } else if (modoDecide==3) { //Ventanita
                JOptionPane.showMessageDialog(
                        Controlador.getInstance().getVco2(), boton[idpanel][l-1].getText()+"\nSI => paso "+ go + "; NO => paso " + (l+1),
                        "Decide",
                        JOptionPane.NO_OPTION);

            } else if (modoDecide==4) { //Incrustado en la VCO
                
                //crear contenedor
                panelDecideIncrustado = new JPanel();
                panelDecideIncrustado.setBackground(colorFondo);
                panelDecideIncrustado.setLayout(new BoxLayout(panelDecideIncrustado,BoxLayout.X_AXIS));

                JLabel labelDecideIncrustado = new JLabel(
                boton[idpanel][l-1].getText().substring(0,boton[idpanel][l-1].getText().length()-7)+
                        "<br>SI => paso "+ go + "<br>NO => paso " + (l+1)+
                        "<br>&nbsp;</html>");
                labelDecideIncrustado.setForeground(colorLabelDecide);

                //añadir icono y label
                panelDecideIncrustado.add(new JLabel("          "));
                panelDecideIncrustado.add(labelDecideIncrustado);
                panelDecideIncrustado.setAlignmentX(0);
                
                pilaPanelesIncrustados.add(panelDecideIncrustado);
                      
                int ll=1; 
                int b;
                for (b=0; b<l-1; b++) {
                    if (boton[idpanel][b].getX()!=0) ll++; //contar solo los que no son paneles incrustados
                }
                panel[idpanel].add(panelDecideIncrustado, 2*ll);
                
            } else if (modoDecide==5) { //Decide siempre visible
                //Destacar color
                int ll=0;
                int b;
                for (b=0; b<l-1; b++) {
                    if (boton[idpanel][b].getX()==0) ll++; //contar solo los que son paneles incrustados
                }
                labelDestacado=(JLabel)((JPanel)panel[idpanel].getComponent(2*l-ll)).getComponent(1);
                labelDestacado.setForeground(colorLabelDecide); //color mas fuerte
                labelDestacado.setText(boton[idpanel][l-1].getText().substring(0,boton[idpanel][l-1].getText().length()-7)+
                        "<br>SI => paso "+ go + "<br>NO => paso " + (l+1)+
                        "<br>&nbsp;</html>");
            }
        }
    }
    
    //Gestion de eventos de botones tipo Make
    private class MakeActionListener implements ActionListener {
        int idpanel; //panel
        int l; //paso
        
        public MakeActionListener(int idpanel, int l) {
            this.idpanel=idpanel;
            this.l=l;
        }
        
        public void actionPerformed(ActionEvent e) {
            borrarMensajeIncrustado(idpanel);
            //enlazar funcionalidad
            if (tipoPanel[idpanel]=='M') {
                cambiarPaso(idpanel, l);
            } else {
                finalizar(0); //si hay dos selectores anidados tambien funciona
            }
            
            //listener cambio
            if (listenerCambio!=null) { listenerCambio.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"make")); }
        }
    }
    
    //Gestion de eventos de botones tipo Return
    private class ReturnActionListener implements ActionListener {
        int idpanel; //panel
        int despl; //desplazamiento (0=Return, 1=Cancel)
        
        public ReturnActionListener(int idpanel, int despl) {
            this.idpanel = idpanel;
            this.despl=despl;
        }
        public void actionPerformed(ActionEvent e) {
            borrarMensajeIncrustado(idpanel);
            //Finalizamos y volvemos al objetivo padre si existe
            finalizar(despl);
            
            //listener cambio
            if (listenerCambio!=null) { listenerCambio.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"return")); }
        }
    }
    
    //Elemento de la pila de objetivos
    private class ElementoPilaVCO{
        int numPanel;
        int pasoSiguiente;

        //Constructor
        ElementoPilaVCO (int numPanel,int paso){
            this.numPanel=numPanel;
            pasoSiguiente=paso;
        }
    }
    
    private void borrarMensajeIncrustado(int numPanel) { //si hay un mensaje incrustado del decide lo borramos
        for (JPanel pdi: pilaPanelesIncrustados) { //puede haber varios decides a la vez, asi que usamos una pila y ahora los borramos todos
            panel[numPanel].remove(pdi);
        }

        pilaPanelesIncrustados.clear(); //vaciamos la pila
        
        //Si hay un decide destacado, dejarlo normal
        if ((modoDecide==5) && (labelDestacado!=null)) { 
            labelDestacado.setForeground(colorLetraBotonInactivo); //color que tenia
            labelDestacado.setText(labelDestacado.getText().substring(0, labelDestacado.getText().indexOf("<br>"))+"<br>&nbsp;</html>");
            labelDestacado=null;
        }
    }
 
    //Regresa al ultimo metodo de la pila
    //despl (0=Return, 1=Cancel)
    private void finalizar(int despl){
        //Si hay metodos padres, entonces volvemos a el anterior			
        if (!pilaVCO.empty()){			
            //Recuperamos el objetivo anterior
            ElementoPilaVCO padre=(ElementoPilaVCO)pilaVCO.pop();

            //si no hemos pulsado en un boton de cancelar y es un selector, seguir
            if ((despl==0) && (tipoPanel[padre.numPanel]!='M')) {
                finalizar(despl);
            } else {
                //Cargamos su panel
                cargarPanel(padre.numPanel);
                cambiarPaso(padre.numPanel,padre.pasoSiguiente+1-despl);
            }                                
        }
    }
    
    //Metodo para activar/desactivar un boton y su respectivo icono del panel
    private void activarBoton(int idpanel, int linea, boolean estado) {
        if (estado) { //activar boton e icono
                boton[idpanel][linea].setEnabled(true);
                boton[idpanel][linea].setBackground(colorBotonActivo);
                boton[idpanel][linea].setForeground(colorLetraBotonActivo);
                icono[idpanel][linea].setEnabled(true);
        } else { //desactivar boton e icono
                boton[idpanel][linea].setEnabled(false);
                boton[idpanel][linea].setBackground(colorBotonInactivo);
                boton[idpanel][linea].setForeground(colorLetraBotonInactivo);
                icono[idpanel][linea].setEnabled(false);
        }
    }
    
    //Metodo para activar/desactivar un boton cancelable y su respectivo icono del panel
    private void activarBotonCancelable(int idpanel, boolean estado) {
        if (estado) { //activar boton e icono
                bCancelable[idpanel].setEnabled(true);
                bCancelable[idpanel].setBackground(colorBotonCancelableActivo);
                bCancelable[idpanel].setForeground(colorLetraBotonActivo);
                icCancelable[idpanel].setEnabled(true);
        } else { //desactivar boton e icono
                bCancelable[idpanel].setEnabled(false);
                bCancelable[idpanel].setBackground(colorBotonInactivo);
                bCancelable[idpanel].setForeground(colorLetraBotonInactivo);
                icCancelable[idpanel].setEnabled(false);
        }
    }

    /**
     * Activa un paso de un m&eacute;todo
     * @param numPanel Identificador del panel
     * @param linea N&uacute;mero del paso a activar
     */
    public void cambiarPaso(int numPanel,int linea){
        if (bloqueo) return; //para que no se pueda avanzar
        
        JButton bot;
        String cond;
        Integer val;
        
        //limpiar la barra de estado
        if (numPanel!=0 || linea!=0) Controlador.getInstance().cambiarEstado("Listo");
        //Numero de lineas del panel principal
        int numeroLineas = boton[numPanel].length;
                
       //Inicialmente desactivamos todos los botones
        for (int i=0;i<numeroLineas;i++){
            activarBoton(numPanel, i, false);
        }
        
        //si es un metodo
        if (tipoPanel[numPanel]=='M') {

            //Activamos el boton correspondiente, si esta dentro de los limites
            if (linea<numeroLineas){
                //comprobacion del disableIf
                bot=boton[numPanel][linea];
                cond=condicionesDI.get(bot);
                val=valoresDI.get(bot);
                
                if (cond==null || val==null || this.getEstado(cond)!=val) {
                    activarBoton(numPanel, linea, true); 
                } else { //y si ese boton es disable, pasar al siguiente
                    cambiarPaso(numPanel, linea+1);
                }
                
                lineaActual=linea;
            //En otro caso finalizamos, y volvemos si se puede
            }else{
                finalizar(0);
            }
            
        } else { //si es un selector
        
            //Filtro Disable
            for (int i=0; i<numeroLineas;i++) {
                bot = boton[numPanel][i];
                cond = condicionesDI.get(bot);
                val = valoresDI.get(bot);
                if (cond==null || val==null || this.getEstado(cond)!=val) {
                    activarBoton(numPanel, i, true);
                }
            }
        }
        
        //Filtro Disable para el boton de Cancelar
        if (bCancelable[numPanel]!=null) { //si existe
                cond = condicionesDI.get(bCancelable[numPanel]);
                val = valoresDI.get(bCancelable[numPanel]);
                if (cond==null || val == null || this.getEstado(cond)!=val) {
                    activarBotonCancelable(numPanel, true); //activar
                } else { //y si se cumple el disableIf
                    activarBotonCancelable(numPanel, false); //desactivar
                }
        }

        //Modalidades del Decide
        if ((linea<numeroLineas)&&(tipoboton[numPanel][linea]=='d')&&(modoDecide>=2)) { //si es un decide
            for(ActionListener al: boton[numPanel][linea].getActionListeners()) { //simular click en decide
                al.actionPerformed(new ActionEvent(boton[numPanel][linea],ActionEvent.ACTION_PERFORMED,"simulado"));
            }
        }
    }

    /**
     * Activa un paso del m&eacute;todo actual
     * @param linea N&uacute;mero del paso a activar
     */
    public void cambiarPaso(int linea) {
        cambiarPaso(this.idPanelActual, linea);
    }

    /**
     * Simula la pulsaci&oacute;n de un bot&oacute;n ante un evento, solo funciona si setAutoClick est&aacute; activado
     * @see #setAutoClick(boolean) 
     */
    public void simularClick() {
        if (autoclick==true) {
            //para cada boton
            for (JButton bot: boton[idPanelActual]) {
                //el primero que este activado
                if (bot.isEnabled()) {
                    //simulamos un evento click
                    for(ActionListener al: bot.getActionListeners()) {
                        al.actionPerformed(new ActionEvent(bot,ActionEvent.ACTION_PERFORMED,"simulado"));
                    }
                    //y dejamos de iterar sobre los botones
                    break; 
                }
            }
        }
    }
    
    /**
     * Carga un panel
     * @param numPanel Identificador del panel a mostrar
     */
    private void cargarPanel(int numPanel){
        //si numPanel es un selector para el sistema
        if (SELcondiciones.containsKey(numPanel)) {
            //decidir y actuar
            if (getEstado(SELcondiciones.get(numPanel)) == 0) {
                cargarPanel(SELrama0.get(numPanel));
            } else {
                cargarPanel(SELrama1.get(numPanel));
            }
        } else { //en caso normal

            //Eliminamos panel anterior
            if (pActual!=null) {
                this.remove(pActual);
            }

            //Si es un metodo solo habilitamos el primer paso
            //if (tipoPanel[numPanel]=='M'){
            cambiarPaso(numPanel,0);
            //}

            //Añadimos panel nuevo
            this.add(panel[numPanel]);

            //foco
            panel[numPanel].requestFocus();

            //dimensiones adecuadas
            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(15, 15, 15)
                    .addComponent(panel[numPanel], GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE)));
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(panel[numPanel], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

            //Establecemos el nuevo panel principal
            pActual=panel[numPanel];
            idPanelActual=numPanel;

            //Actualizaremos el panel
            //this.validateTree();
            this.validate();
        }
    }
    
    //ESTADOS

    /**
     * Cambia (o establece si no exist&iacute;a) el valor de un estado
     * @param estado Nombre del estado
     * @param valor Nuevo valor
     * @see #effect(java.lang.String, java.lang.Integer)
     */
    public void setEstado(String estado, Integer valor) {
        estados.put(estado, valor);
    }
    
    /**
     * Devuelve el valor de un estado
     * @param estado Nombre del estado
     * @return El valor que contiene el estado, o 0 si no existe
     */
    public Integer getEstado(String estado) {
        if (estados.get(estado)==null) { //devuelve 0 si no existe
            return 0;
        } else {
            return estados.get(estado);
        }
    }
    
    /**
     * Cambia (o establece si no exist&iacute;a) el valor de un estado
     * @param estado Nombre del estado
     * @param valor Nuevo valor
     * @see #setEstado(java.lang.String, java.lang.Integer)
     */
    public void effect(String estado, Integer valor) {
        setEstado(estado, valor);
    }
    
    /**
     * Establece la subrutina a la que se le informa de cambios
     * @param lc Subrutina que se ejecutar&aacute; cuando se produzcan cambios
     */
    public void setListenerCambio(ActionListener lc) {
        this.listenerCambio=lc;
    }
    
    //Devuelve el camino por todos los subjetivos de la pila

    /**
     * Devuelve el camino por todos los subobjetivos en el momento de la llamada
     * @return Cadena con los subobjetivos formateados
     */
    public String camino() {
        String camino = "<html>Objetivo actual: ";
        
        //Iteramos sobre la pila
        for (ElementoPilaVCO epv : pilaVCO) {
            camino = camino +  "<font color=#606060>" + titulos[epv.numPanel].getText() + "</font> > ";
        }
        
        camino = camino + "<u>" + titulos[idPanelActual].getText()+"</u></html>";
        
        return camino;
    }
    
    //Métodos para permitir/impedir que se avance al paso siguiente

    /**
     * Impide que se avance al paso siguiente
     * @see #desbloquear()
     */
    public void bloquear() {
        bloqueo=true;
    }

    /**
     * Permite que se avance al paso siguiente
     * @see #bloquear() 
     */
    public void desbloquear() {
        bloqueo=false;
    }

}
