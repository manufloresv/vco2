# API Librería VCO2

La clase VCO2 ha sido desarrollada con la intención de poder ser usada en otros proyectos. Permite crear y gestionar la Ventana Conductora de Objetivos de una aplicación con estilo de interacción IGO desde código de forma dinámica y puede ser usada en otras aplicaciones con una interfaz basada en la metodología IGO. La implementación de esta clase ha seguido la definición teórica de la notación IGO, la clase extiende de JPanel donde se añade toda la lógica de una VCO mediante los métodos que se verán a continuación y queda representada como componente gráfico de la librería Swing. A continuación se expone una guía de uso y la documentación generada mediante Javadoc.

## Cómo empezar

Importar el paquete igo:

```java
import igo.*;
```

Declararación:

```java
private VCO2 vco2;
```

Crear la Ventana Conductora de Objetivos:

```java
vco2 = new VCO2();
```

Ajustes de comportamiento:

```java
vco2.setModoDecide(1);
vco2.setAutoClick(true);
```

Añadir todos los objetivos y pasos, usando un identificador numérico para cada panel:

```java
vco2.definePanel(0, "Diseñar cocina", 'M');

vco2.defineBotonInit(0, "Dibujar plano", 1);
vco2.defineBotonInit(0, "Amueblar", 11);
vco2.defineBotonDecide(0, "¿Hacer otro diseño?", 1);
vco2.defineBotonInit(0, "Encargar pedido", 24);
vco2.defineBotonReturn(0);

vco2.definePanel(1, "Dibujar plano", 'M');

...

vco2.definePanel(9, "Introducir medidas", 'M');
vco2.defineBotonMake(9, "Lado A");
vco2.defineBotonMake(9, "Lado B");
vco2.defineBotonDecide(9, "¿Corregir medidas?", 1);
vco2.defineBotonReturn(9);
vco2.defineBotonCancelar(9);

...

vco2.definePanel(11, "Amueblar", 'M');

...
```

También se puede añadir una rutina a un panel o a un botón:

```java
vco2.defineBotonMake(13, "Pinche un mueble", new ActionListener(){
    public void actionPerformed(ActionEvent e){
        //acciones
    }});
```

Cargar el panel con el objetivo principal:

```java
vco2.cargarPanelInicial();
```

Añadir la VCO a un panel contenedor que contenga el área de trabajo a la derecha:

```java
contenedorPpal1.add(vco2, BorderLayout.WEST);
```
