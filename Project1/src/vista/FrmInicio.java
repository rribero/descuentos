
package vista;

import finaldescuentos.Categoria;
import finaldescuentos.Concepto;
import finaldescuentos.Descuento;
import finaldescuentos.Empleado;
import finaldescuentos.Tipo;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author rribero
 */
public class FrmInicio extends javax.swing.JFrame {

    //variables utiles
    private int i = -1;
    private boolean busqueda = true;
    
    //modelo de lista
    DefaultListModel listamodelo = new DefaultListModel();
    
    //listas y vectores para los combobox
    Vector<Descuento> vectordescuentos = new Vector<Descuento>();
    Vector<Empleado> coleccionempleados = new Vector<Empleado>();

    //formato de numeros double
    DecimalFormat df = new DecimalFormat("#.00");
    
    //codigo incremental
    public int sumaCod() {
        i++;
        txtCod.setText(String.valueOf(i));
        return i;
    }

    //codigo para descontar segun concepto, tipo y categpria
    public double Descontar(Double importe, Concepto concepto, Tipo tipo, Categoria categoria){
            double descuentofinal = categoria.descuento(tipo.descuento(concepto.descuento(importe)));
            return descuentofinal;
    }
    
    //array de caracteres no permitidos
    String caracteres = "/*-+,<>!\"·$%&()=?¿'¡[]`´{}@|#~€¬";
    char[] arraycaracteres = caracteres.toCharArray();
    
    //codigo para detectar caracteres no permitidos
    public int CaracterNoPermitido(java.awt.event.KeyEvent evt) {
        int caracternovalido = 0;
        
        for (char c:arraycaracteres){
            if (evt.getKeyChar()==c){
                caracternovalido=1;
            }
        }

        return caracternovalido;
    }
    
    //mensaje para cuando detecta un caracter o letra no permitido
    public void TeclaPresionadaEsLetra(java.awt.event.KeyEvent evt) {
        if(Character.isLetter(evt.getKeyChar()) || CaracterNoPermitido(evt)==1 || Character.isSpaceChar(evt.getKeyChar())) {
            JOptionPane.showMessageDialog(frmDescuentos, "Solo se admiten números");
            evt.consume(); //ignora tecla presionada
        } 
    }
    
    //mensaje para cuando detecta un caracter o número no permitido
    public void TeclaPresionadaEsNumero(java.awt.event.KeyEvent evt) {
        if(Character.isDigit(evt.getKeyChar()) || CaracterNoPermitido(evt)==1) {
            JOptionPane.showMessageDialog(frmDescuentos, "Solo se admiten letras");
            evt.consume();
        } 
    }
    
    //codigo para exportar tabla a excel
    public void exportarExcel(JTable t) throws IOException {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Guardar archivo");
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String ruta = chooser.getSelectedFile().toString().concat(".xls");
                try {
                    File archivoXLS = new File(ruta);
                    if (archivoXLS.exists()) {
                        archivoXLS.delete();
                    }
                    archivoXLS.createNewFile();
                    Workbook libro = new HSSFWorkbook();
                    FileOutputStream archivo = new FileOutputStream(archivoXLS);
                    Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");
                    hoja.setDisplayGridlines(false);
                    for (int f = 0; f < t.getRowCount(); f++) {
                        Row fila = hoja.createRow(f);
                        for (int c = 0; c < t.getColumnCount(); c++) {
                            Cell celda = fila.createCell(c);
                            if (f == 0) {
                                celda.setCellValue(t.getColumnName(c));
                            }
                        }
                    }
                    int filaInicio = 1;
                    for (int f = 0; f < t.getRowCount(); f++) {
                        Row fila = hoja.createRow(filaInicio);
                        filaInicio++;
                        for (int c = 0; c < t.getColumnCount(); c++) {
                            Cell celda = fila.createCell(c);
                            if (t.getValueAt(f, c) instanceof Double) {
                                celda.setCellValue(Double.parseDouble(t.getValueAt(f, c).toString()));
                            } else if (t.getValueAt(f, c) instanceof Float) {
                                celda.setCellValue(Float.parseFloat((String) t.getValueAt(f, c)));
                            } else {
                                celda.setCellValue(String.valueOf(t.getValueAt(f, c)));
                            }
                        }
                    }
                    libro.write(archivo);
                    archivo.close();
                    Desktop.getDesktop().open(archivoXLS);
                } catch (IOException | NumberFormatException e) {
                    throw e;
                }
            }
        }


    /** Creates new form fRAME1 */
    public FrmInicio() {
        initComponents();
        
        //seteo de icono
        setIconImage(new ImageIcon(getClass().getResource("/impuestos.png")).getImage());
        
        txtCod.setText(String.valueOf(i));
        txtImporte.grabFocus();
        
        //creo los objetos para los combobox
        Concepto c1 = Concepto.Indumentaria;
        Concepto c2 = Concepto.Supermercado;
        Concepto c3 = Concepto.Combustible;
        Tipo t1 = Tipo.Basico;
        Tipo t2 = Tipo.Especial;        
        Categoria cat1 = Categoria.Uno;
        Categoria cat2 = Categoria.Dos;
        Categoria cat3 = Categoria.Tres;
        
        
        //creo empleados
        Empleado e1 = new Empleado(100, "Roberto Ribero", 37934142, cat2);
        Empleado e2 = new Empleado(101, "James Monroe", 35087652, cat3);
        Empleado e3 = new Empleado(102, "Donald Trump", 28954615, cat1);
        Empleado e4 = new Empleado(103, "Barak Obama", 28954615, cat1);
        Empleado e5 = new Empleado(104, "George Washington", 28954615, cat3);
        Empleado e6 = new Empleado(105, "Thomas Jefferson", 28954615, cat2);
        Empleado e7 = new Empleado(106, "Rutherford B. Hayes", 28954615, cat1);
        Empleado e8 = new Empleado(107, "Theodore Roosevelt", 28954615, cat1);
        Empleado e9 = new Empleado(108, "Woodrow Wilson", 28954615, cat2);
        Empleado e10 = new Empleado(109, "Franklin D. Roosevelt", 28954615, cat3);
        Empleado e11 = new Empleado(110, "John F. Kennedy", 28954615, cat2);
        Empleado e12 = new Empleado(111, "Richard (Big Nose) Nixon", 28954615, cat3);

        //agrego los objetos a sus respectivas colecciones
        coleccionempleados.add(e1);
        coleccionempleados.add(e2);
        coleccionempleados.add(e3);
        coleccionempleados.add(e4);
        coleccionempleados.add(e5);
        coleccionempleados.add(e6);
        coleccionempleados.add(e7);
        coleccionempleados.add(e8);
        coleccionempleados.add(e9);
        coleccionempleados.add(e10);
        coleccionempleados.add(e11);
        coleccionempleados.add(e12);
        
        //instancio los combobox y seteo el contenido
        //ComboBox Concepto
        List<Concepto> listconcept = new ArrayList<Concepto>();
        listconcept = Concepto.getConceptos();
        cboConcep.setModel(new DefaultComboBoxModel<Concepto>(listconcept.toArray(new Concepto[0])));
        
        //ComboBox Tipo        
        List<Tipo> listtipo = new ArrayList<Tipo>();
        listtipo = Tipo.getTipos();
        cboTipo.setModel(new DefaultComboBoxModel<Tipo>(listtipo.toArray(new Tipo[0])));
        
        //ComboBox Empleado
        ComboBoxModel<Empleado> concEmp = new DefaultComboBoxModel<>(coleccionempleados);
        concEmp.setSelectedItem(e1);
        cboEmpleados.setModel(concEmp);

        
        //FRAME AGREGAR EMPLEADOS
        List<Categoria> listcategorias = new ArrayList<Categoria>();
        listcategorias = Categoria.getCategorias();
        cboCategoria.setModel(new DefaultComboBoxModel<Categoria>(listcategorias.toArray(new Categoria[0])));

        //ComboBox Categoria
        /**
        ComboBoxModel<Categoria> concCategorias = new DefaultComboBoxModel<>(coleccioncategorias);
        cboCategoria.setModel(concCategorias);
        
        List<Categoria2> listacat = new ArrayList<Categoria2>();
        listacat=Categoria2.getCategorias();
        cboCategoria.setModel(new DefaultComboBoxModel<Categoria2>(listacat.toArray(new Categoria2[0])));
        */
        
        //agrego descuentos a mano para ver funcionalidad
        Descuento descprueba1 = new Descuento(sumaCod(), 20000, Descontar(20000.0, c1, t1, e1.getCategoria()), t1, c1, e1, 20000-Descontar(20000.0, c1, t1, e1.getCategoria()));
        Descuento descprueba2 = new Descuento(sumaCod(), 10000, Descontar(10000.0, c3, t2, e2.getCategoria()), t2, c3, e2, 10000-Descontar(10000.0, c3, t2, e2.getCategoria()));
        Descuento descprueba3 = new Descuento(sumaCod(), 15000, Descontar(35000.0, c2, t2, e3.getCategoria()), t2, c2, e3, 35000-Descontar(35000.0, c2, t2, e3.getCategoria()));
        Descuento descprueba4 = new Descuento(sumaCod(), 20000, Descontar(35000.0, c2, t1, e4.getCategoria()), t1, c2, e4, 35000-Descontar(35000.0, c2, t1, e4.getCategoria()));
        Descuento descprueba5 = new Descuento(sumaCod(), 15000, Descontar(15000.0, c1, t2, e5.getCategoria()), t2, c1, e5, 15000-Descontar(15000.0, c1, t2, e5.getCategoria()));
        Descuento descprueba6 = new Descuento(sumaCod(), 20000, Descontar(20000.0, c3, t2, e6.getCategoria()), t2, c3, e6, 20000-Descontar(20000.0, c3, t2, e6.getCategoria()));
        Descuento descprueba7 = new Descuento(sumaCod(), 15000, Descontar(35000.0, c2, t2, e7.getCategoria()), t2, c2, e7, 35000-Descontar(35000.0, c2, t2, e7.getCategoria()));
        Descuento descprueba8 = new Descuento(sumaCod(), 10000, Descontar(10000.0, c1, t1, e8.getCategoria()), t1, c1, e8, 10000-Descontar(10000.0, c1, t1, e8.getCategoria()));
        Descuento descprueba9 = new Descuento(sumaCod(), 20000, Descontar(20000.0, c3, t2, e9.getCategoria()), t2, c3, e9, 20000-Descontar(20000.0, c3, t2, e9.getCategoria()));
        Descuento descprueba10 = new Descuento(sumaCod(), 10000, Descontar(35000.0, c2, t2, e10.getCategoria()), t2, c2, e10, 35000-Descontar(35000.0, c2, t2, e10.getCategoria()));
        Descuento descprueba11 = new Descuento(sumaCod(), 15000, Descontar(15000.0, c1, t1, e11.getCategoria()), t1, c1, e11, 15000.0-Descontar(15000.0, c1, t1, e11.getCategoria()));
        Descuento descprueba12 = new Descuento(sumaCod(), 20000, Descontar(20000.0, c3, t2, e12.getCategoria()), t2, c3, e12, 20000.0-Descontar(20000.0, c3, t2, e12.getCategoria()));
        Descuento descprueba13 = new Descuento(sumaCod(), 10000, Descontar(35000.0, c2, t2, e5.getCategoria()), t2, c2, e5, 35000.0-Descontar(35000.0, c2, t2, e5.getCategoria()));

        //agrego los descuentos a la lista
        vectordescuentos.add(descprueba1);
        vectordescuentos.add(descprueba2);
        vectordescuentos.add(descprueba3);
        vectordescuentos.add(descprueba4);
        vectordescuentos.add(descprueba5);
        vectordescuentos.add(descprueba6);
        vectordescuentos.add(descprueba7);
        vectordescuentos.add(descprueba8);
        vectordescuentos.add(descprueba9);
        vectordescuentos.add(descprueba10);
        vectordescuentos.add(descprueba11);
        vectordescuentos.add(descprueba12);
        vectordescuentos.add(descprueba13);
        
        sumaCod(); 
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        frmAgregarEmpleado = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        btnAgregarEmpleado = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtNombreEmpleado = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtCodigoEmpleado = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtDNI = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cboCategoria = new javax.swing.JComboBox();
        btnCancelarAgregarEmpleado = new javax.swing.JButton();
        frmDescuentos = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        JscrollDescuentos = new javax.swing.JScrollPane();
        listaDescuentos = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        btnCerrar = new javax.swing.JButton();
        btnEliminarDescuento = new javax.swing.JButton();
        btnEditarDescuento = new javax.swing.JButton();
        panelEditar = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        txtEditCodigo = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtEditImporte = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        cboEditConcep = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        cboEditTipo = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        cboEditEmpleados = new javax.swing.JComboBox();
        btnCancelar = new javax.swing.JButton();
        btnAgregar2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        frmOpciones = new javax.swing.JFrame();
        jPanel6 = new javax.swing.JPanel();
        rbtnCod = new javax.swing.JRadioButton();
        rbtnNombre = new javax.swing.JRadioButton();
        frmTabla = new javax.swing.JFrame();
        panel1 = new java.awt.Panel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnExportar = new javax.swing.JButton();
        panelContenedor = new java.awt.Panel();
        panelAgregar = new java.awt.Panel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCod = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cboConcep = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboEmpleados = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtImporteConDescuento = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnAgregarItem = new javax.swing.JButton();
        lblError = new javax.swing.JLabel();
        btnColorError = new javax.swing.JButton();
        txtImporte = new javax.swing.JTextField();
        panelOpciones = new javax.swing.JPanel();
        btnAgregarEmpleados = new javax.swing.JButton();
        btnVerDescuentos = new javax.swing.JButton();
        btnCerrarApp = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnHistorico = new javax.swing.JButton();
        btnTabla = new javax.swing.JButton();

        frmAgregarEmpleado.setMinimumSize(new java.awt.Dimension(300, 400));
        frmAgregarEmpleado.setResizable(false);
        frmAgregarEmpleado.setIconImage(new ImageIcon(getClass().getResource("/usuario.png")).getImage());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Agregar Emplado"));

        btnAgregarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/agregar.png"))); // NOI18N
        btnAgregarEmpleado.setText("Agregar");
        btnAgregarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarEmpleadoActionPerformed(evt);
            }
        });

        jLabel10.setText("Nombre");

        txtNombreEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreEmpleadoKeyTyped(evt);
            }
        });

        jLabel11.setText("Código");

        txtCodigoEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoEmpleadoKeyTyped(evt);
            }
        });

        jLabel12.setText("DNI");

        txtDNI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDNIKeyTyped(evt);
            }
        });

        jLabel13.setText("Categoria");

        cboCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnCancelarAgregarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/cancelar.png"))); // NOI18N
        btnCancelarAgregarEmpleado.setText("Cancelar");
        btnCancelarAgregarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarAgregarEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCodigoEmpleado, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cboCategoria, javax.swing.GroupLayout.Alignment.LEADING, 0, 80, Short.MAX_VALUE)
                        .addComponent(txtDNI, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnCancelarAgregarEmpleado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAgregarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCodigoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarAgregarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        frmAgregarEmpleado.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        frmDescuentos.setMinimumSize(new java.awt.Dimension(900, 530));
        frmDescuentos.setResizable(false);
        frmDescuentos.setIconImage(new ImageIcon(getClass().getResource("/impuestos.png")).getImage());

        jPanel3.setLayout(new java.awt.BorderLayout());

        listaDescuentos.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de descuentos"));
        listaDescuentos.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        JscrollDescuentos.setViewportView(listaDescuentos);

        jPanel3.add(JscrollDescuentos, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnCerrar.setText("Cerrar");
        btnCerrar.setAlignmentX(0.5F);
        btnCerrar.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnEliminarDescuento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/cancelar.png"))); // NOI18N
        btnEliminarDescuento.setText("Eliminar descuento");
        btnEliminarDescuento.setMaximumSize(new java.awt.Dimension(249, 21));
        btnEliminarDescuento.setMinimumSize(new java.awt.Dimension(249, 21));
        btnEliminarDescuento.setPreferredSize(new java.awt.Dimension(95, 21));
        btnEliminarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarDescuentoActionPerformed(evt);
            }
        });

        btnEditarDescuento.setText("Editar descuento");
        btnEditarDescuento.setMaximumSize(new java.awt.Dimension(249, 21));
        btnEditarDescuento.setMinimumSize(new java.awt.Dimension(249, 21));
        btnEditarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarDescuentoActionPerformed(evt);
            }
        });

        panelEditar.setBorder(javax.swing.BorderFactory.createTitledBorder("Editar"));
        panelEditar.setEnabled(false);

        jLabel19.setText("Código");

        txtEditCodigo.setEditable(false);

        jLabel20.setText("Importe");

        txtEditImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEditImporteKeyTyped(evt);
            }
        });

        jLabel21.setText("Concepto");

        cboEditConcep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel22.setText("Tipo");

        cboEditTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel23.setText("Empleado");

        cboEditEmpleados.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboEditEmpleados.setMaximumSize(new java.awt.Dimension(40, 21));
        cboEditEmpleados.setMinimumSize(new java.awt.Dimension(40, 21));

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnAgregar2.setText("OK");
        btnAgregar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregar2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEditarLayout = new javax.swing.GroupLayout(panelEditar);
        panelEditar.setLayout(panelEditarLayout);
        panelEditarLayout.setHorizontalGroup(
            panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(txtEditCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEditImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addContainerGap(214, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregar2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(27, 27, 27)
                        .addComponent(cboEditEmpleados, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(27, 27, 27)
                        .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboEditTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboEditConcep, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        panelEditarLayout.setVerticalGroup(
            panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEditCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEditImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(cboEditConcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboEditTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(cboEditEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnAgregar2))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEditar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnEliminarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCerrar))
        );

        jPanel3.add(jPanel5, java.awt.BorderLayout.LINE_END);

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarKeyTyped(evt);
            }
        });

        jLabel14.setText("Búsqueda por:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Código", "Empleado" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(329, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        frmDescuentos.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        frmOpciones.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frmOpciones.setMinimumSize(new java.awt.Dimension(300, 130));
        frmOpciones.setResizable(false);

        rbtnCod.setText("Búsqueda por código");
        rbtnCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnCodActionPerformed(evt);
            }
        });

        rbtnNombre.setText("Búsqueda por Nombre de Empleado");
        rbtnNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnNombreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtnNombre)
                    .addComponent(rbtnCod))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(rbtnCod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtnNombre)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        frmOpciones.getContentPane().add(jPanel6, java.awt.BorderLayout.CENTER);

        frmTabla.setMinimumSize(new java.awt.Dimension(600, 400));

        panel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(jTable1);

        panel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/guardar.png"))); // NOI18N
        btnExportar.setText("Exportar en Excel");
        btnExportar.setMaximumSize(new java.awt.Dimension(99, 40));
        btnExportar.setMinimumSize(new java.awt.Dimension(99, 40));
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });
        panel1.add(btnExportar, java.awt.BorderLayout.PAGE_END);

        frmTabla.getContentPane().add(panel1, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Descuentos");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(430, 410));
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        panelContenedor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelContenedor.setName(""); // NOI18N
        panelContenedor.setPreferredSize(new java.awt.Dimension(600, 400));

        panelAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelAgregar.setPreferredSize(new java.awt.Dimension(300, 350));

        jLabel3.setText("Ingrese item a descontar");

        jLabel4.setText("Codigo");

        txtCod.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCod.setFocusable(false);
        txtCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodActionPerformed(evt);
            }
        });

        jLabel1.setText("Concepto");

        cboConcep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboConcep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboConcepActionPerformed(evt);
            }
        });

        jLabel2.setText("Tipo");

        cboTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Empleado");

        cboEmpleados.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboEmpleados.setMaximumSize(new java.awt.Dimension(40, 21));
        cboEmpleados.setMinimumSize(new java.awt.Dimension(40, 21));
        cboEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEmpleadosActionPerformed(evt);
            }
        });

        jLabel6.setText("Importe");

        jLabel7.setText("$");

        jLabel8.setText("Importe despues de aplicar descuentos:");

        txtImporteConDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImporteConDescuento.setEnabled(false);

        jLabel9.setText("$");

        btnAgregarItem.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAgregarItem.setForeground(new java.awt.Color(0, 153, 153));
        btnAgregarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/agregar.png"))); // NOI18N
        btnAgregarItem.setText("AGREGAR ITEM");
        btnAgregarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarItemActionPerformed(evt);
            }
        });

        lblError.setText("Ok");

        btnColorError.setBackground(new java.awt.Color(51, 255, 51));
        btnColorError.setAlignmentX(0.5F);
        btnColorError.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnColorError.setMaximumSize(new java.awt.Dimension(14, 14));
        btnColorError.setMinimumSize(new java.awt.Dimension(14, 14));
        btnColorError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorErrorActionPerformed(evt);
            }
        });

        txtImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImporteKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelAgregarLayout = new javax.swing.GroupLayout(panelAgregar);
        panelAgregar.setLayout(panelAgregarLayout);
        panelAgregarLayout.setHorizontalGroup(
            panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAgregarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAgregarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(panelAgregarLayout.createSequentialGroup()
                        .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelAgregarLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtImporte))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAgregarLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAgregarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtImporteConDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAgregarLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAgregarLayout.createSequentialGroup()
                        .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboConcep, 0, 172, Short.MAX_VALUE)
                            .addComponent(cboTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelAgregarLayout.createSequentialGroup()
                        .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(panelAgregarLayout.createSequentialGroup()
                                .addComponent(btnColorError, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelAgregarLayout.setVerticalGroup(
            panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAgregarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(10, 10, 10)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboConcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImporteConDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarItem, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addGroup(panelAgregarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnColorError, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblError))
                .addGap(7, 7, 7))
        );

        panelOpciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 153, 153))); // NOI18N
        panelOpciones.setToolTipText("");
        panelOpciones.setPreferredSize(new java.awt.Dimension(180, 350));

        btnAgregarEmpleados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/empleado.png"))); // NOI18N
        btnAgregarEmpleados.setText("  Agregar empleado");
        btnAgregarEmpleados.setAlignmentX(0.5F);
        btnAgregarEmpleados.setMaximumSize(new java.awt.Dimension(177, 38));
        btnAgregarEmpleados.setMinimumSize(new java.awt.Dimension(177, 38));
        btnAgregarEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarEmpleadosActionPerformed(evt);
            }
        });

        btnVerDescuentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/impuestos.png"))); // NOI18N
        btnVerDescuentos.setText("Ver Descuentos");
        btnVerDescuentos.setMaximumSize(new java.awt.Dimension(177, 38));
        btnVerDescuentos.setMinimumSize(new java.awt.Dimension(177, 38));
        btnVerDescuentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDescuentosActionPerformed(evt);
            }
        });

        btnCerrarApp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        btnCerrarApp.setText("Cerrar");
        btnCerrarApp.setMaximumSize(new java.awt.Dimension(177, 38));
        btnCerrarApp.setMinimumSize(new java.awt.Dimension(177, 38));
        btnCerrarApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarAppActionPerformed(evt);
            }
        });

        jButton1.setText("Descuentos por Categoria");
        jButton1.setMaximumSize(new java.awt.Dimension(177, 38));
        jButton1.setMinimumSize(new java.awt.Dimension(177, 38));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnHistorico.setText("Descuentos Historico");
        btnHistorico.setMaximumSize(new java.awt.Dimension(177, 38));
        btnHistorico.setMinimumSize(new java.awt.Dimension(177, 38));
        btnHistorico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricoActionPerformed(evt);
            }
        });

        btnTabla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/guardar.png"))); // NOI18N
        btnTabla.setText("  Exportar Tabla");
        btnTabla.setMaximumSize(new java.awt.Dimension(177, 38));
        btnTabla.setMinimumSize(new java.awt.Dimension(177, 38));
        btnTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTablaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOpcionesLayout = new javax.swing.GroupLayout(panelOpciones);
        panelOpciones.setLayout(panelOpcionesLayout);
        panelOpcionesLayout.setHorizontalGroup(
            panelOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCerrarApp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnVerDescuentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnAgregarEmpleados, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );
        panelOpcionesLayout.setVerticalGroup(
            panelOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOpcionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAgregarEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVerDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(51, 51, 51)
                .addComponent(btnCerrarApp, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout panelContenedorLayout = new javax.swing.GroupLayout(panelContenedor);
        panelContenedor.setLayout(panelContenedorLayout);
        panelContenedorLayout.setHorizontalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContenedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(panelOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        panelContenedorLayout.setVerticalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContenedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(panelContenedor);

        pack();
    }//GEN-END:initComponents

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
    }//GEN-LAST:event_txtCodActionPerformed

    private void btnAgregarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarItemActionPerformed
        if (!txtImporte.getText().isEmpty()) {
            //traigo los atributos para descontar
            int cod = i;
            double importe = Double.parseDouble(txtImporte.getText());
            Concepto concepto = (Concepto) cboConcep.getModel().getSelectedItem();
            Tipo tipo = (Tipo) cboTipo.getModel().getSelectedItem();
            Empleado empleado = (Empleado) cboEmpleados.getModel().getSelectedItem();
            Categoria categoria = empleado.getCategoria();
            //aplico el descuento
            double descuentofinal = Descontar(importe, concepto, tipo, categoria);
            //creo el descuento
            Descuento d1 = new Descuento(cod, importe, descuentofinal, tipo, concepto, empleado, importe - descuentofinal);
            vectordescuentos.add(d1);
            txtImporteConDescuento.setText(String.valueOf(df.format(descuentofinal)));
            sumaCod();
            txtImporte.setText("");
            txtImporteConDescuento.setText("");
            JOptionPane.showMessageDialog(frmDescuentos, "Descuento agregado con éxito.");
            lblError.setText("Descuento agregado con éxito.");
            btnColorError.setBackground(Color.green);
            txtImporte.grabFocus();
            }
        else {
            lblError.setText("Debe ingresar un importe");
            btnColorError.setBackground(Color.red);
            JOptionPane.showMessageDialog(frmDescuentos, "Debe ingresar un importe");
        }
    }//GEN-LAST:event_btnAgregarItemActionPerformed

    private void cboConcepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConcepActionPerformed
    }//GEN-LAST:event_cboConcepActionPerformed

    private void cboEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEmpleadosActionPerformed
    }//GEN-LAST:event_cboEmpleadosActionPerformed

    private void btnAgregarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarEmpleadoActionPerformed
        if (txtCodigoEmpleado.getText().isEmpty() || txtNombreEmpleado.getText().isEmpty() || txtDNI.getText().isEmpty()){
            JOptionPane.showMessageDialog(frmAgregarEmpleado, "Falta completar algún campo");
        }
        else{
            int codigoempleado = Integer.parseInt(txtCodigoEmpleado.getText());
            String nombreempleado = txtNombreEmpleado.getText();
            int dni = Integer.parseInt(txtDNI.getText());
            Categoria categoriaempleado = (Categoria) cboCategoria.getModel().getSelectedItem();
            Empleado e1 = new Empleado(codigoempleado, nombreempleado, dni, categoriaempleado);
            coleccionempleados.add(e1);
            txtCodigoEmpleado.setText("");
            txtNombreEmpleado.setText("");
            txtDNI.setText("");
            frmAgregarEmpleado.dispose();
        }
        
        //vuelvo a cargar el combo, sino se tilda
        ComboBoxModel<Empleado> concEmp = new DefaultComboBoxModel<>(coleccionempleados);
        cboEmpleados.setModel(concEmp);
    }//GEN-LAST:event_btnAgregarEmpleadoActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        frmDescuentos.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnEliminarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarDescuentoActionPerformed
        if ((listaDescuentos.getSelectedIndex()>=0)){
            vectordescuentos.remove(vectordescuentos.indexOf(listaDescuentos.getSelectedValue()));

            for (Descuento desc:vectordescuentos){
                listamodelo.addElement(desc);
            }
            
            listamodelo.clear();
            listaDescuentos.setModel(listamodelo);
            }
        else {
            JOptionPane.showMessageDialog(frmDescuentos, "Ningún elemento seleccionado");
        }
            
        if (panelEditar.isVisible()){
            panelEditar.setVisible(false);
        }
            
        txtBuscar.setText("");
        txtBuscar.grabFocus();
            
        //presiono enter
        try{
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        }catch(AWTException a){
        }
    }//GEN-LAST:event_btnEliminarDescuentoActionPerformed

    private void btnColorErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorErrorActionPerformed
    }//GEN-LAST:event_btnColorErrorActionPerformed

    private void btnEditarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarDescuentoActionPerformed
        if ((listaDescuentos.getSelectedIndex()>=0)){
            int indice = vectordescuentos.indexOf(listaDescuentos.getSelectedValue());
            Descuento descuentoeditar = vectordescuentos.get(indice);
            panelEditar.setVisible(true);
            
            txtEditCodigo.setText(String.valueOf(descuentoeditar.getCod()));
            txtEditImporte.setText(String.valueOf(descuentoeditar.getImporte()));
            
            List<Concepto> listconcept = new ArrayList<Concepto>();
            listconcept = Concepto.getConceptos();
            ComboBoxModel<Concepto> concConcept = new DefaultComboBoxModel<Concepto>(listconcept.toArray(new Concepto[0]));
            concConcept.setSelectedItem(descuentoeditar.getConcepto());
            cboEditConcep.setModel(concConcept);
            
            List<Tipo> listtipo = new ArrayList<Tipo>();
            listtipo = Tipo.getTipos();
            ComboBoxModel<Tipo> concTipo = new DefaultComboBoxModel<Tipo>(listtipo.toArray(new Tipo[0]));
            concTipo.setSelectedItem(descuentoeditar.getTipo());
            cboEditTipo.setModel(concTipo);
        
            ComboBoxModel<Empleado> concEmp = new DefaultComboBoxModel<>(coleccionempleados);
            concEmp.setSelectedItem(descuentoeditar.getEmpleado());
            cboEditEmpleados.setModel(concEmp);
        }
        else{
            JOptionPane.showMessageDialog(frmDescuentos, "Ningún elemento seleccionado");
        }    
    }//GEN-LAST:event_btnEditarDescuentoActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        panelEditar.setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAgregar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregar2ActionPerformed
        //si existe, traigo el item seleccionado
        if (!vectordescuentos.isEmpty()){
            int indice = vectordescuentos.indexOf(listaDescuentos.getSelectedValue());
            Descuento descuentoeditar = (Descuento)vectordescuentos.get(indice);
            
            //traigo los atributos para descontar
            descuentoeditar.setImporte(Double.parseDouble(txtEditImporte.getText()));
            Concepto concepto = (Concepto) cboEditConcep.getModel().getSelectedItem();
            descuentoeditar.setConcepto(concepto);
            Tipo tipo = (Tipo) cboEditTipo.getModel().getSelectedItem();
            descuentoeditar.setTipo(tipo);
            Empleado empleado = (Empleado) cboEditEmpleados.getModel().getSelectedItem();
            Categoria categoria = empleado.getCategoria();
            descuentoeditar.setEmpleado(empleado);
            
            //aplico el descuento
            double descuentofinal = Descontar(Double.parseDouble(txtEditImporte.getText()), concepto, tipo, categoria);
            descuentoeditar.setImportecondesc(descuentofinal);
            descuentoeditar.setTotaldescuento(Double.parseDouble(txtEditImporte.getText())-descuentofinal);
            
            listaDescuentos.setModel(listamodelo);
            panelEditar.setVisible(false);
        }
        else{
            JOptionPane.showMessageDialog(frmDescuentos, "Elemento perdido");
        }
    
    }//GEN-LAST:event_btnAgregar2ActionPerformed

    private void txtEditImporteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditImporteKeyTyped
        TeclaPresionadaEsLetra(evt);
    }//GEN-LAST:event_txtEditImporteKeyTyped

    private void txtCodigoEmpleadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoEmpleadoKeyTyped
        TeclaPresionadaEsLetra(evt);
    }//GEN-LAST:event_txtCodigoEmpleadoKeyTyped

    private void txtNombreEmpleadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreEmpleadoKeyTyped
        TeclaPresionadaEsNumero(evt);
    }//GEN-LAST:event_txtNombreEmpleadoKeyTyped

    private void txtDNIKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIKeyTyped
        TeclaPresionadaEsLetra(evt);
    }//GEN-LAST:event_txtDNIKeyTyped

    private void btnCancelarAgregarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarAgregarEmpleadoActionPerformed
        frmAgregarEmpleado.dispose();
    }//GEN-LAST:event_btnCancelarAgregarEmpleadoActionPerformed

    private void txtImporteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyTyped
        TeclaPresionadaEsLetra(evt);
        /**
         * cargo cualquier ComboBox
         * sino se tildan
         **/
    
        List<Concepto> listconcept = new ArrayList<Concepto>();
        listconcept = Concepto.getConceptos();
        ComboBoxModel<Concepto> concConcept = new DefaultComboBoxModel<Concepto>(listconcept.toArray(new Concepto[0]));
        cboConcep.setModel(concConcept);
    }//GEN-LAST:event_txtImporteKeyTyped

    private void btnCerrarAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarAppActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCerrarAppActionPerformed

    private void btnVerDescuentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDescuentosActionPerformed
        frmDescuentos.setVisible(true);
        frmDescuentos.setLocationRelativeTo(null);
        
        listamodelo.clear();
        
        for (Descuento desc:vectordescuentos){
            listamodelo.addElement(desc);
        }
        
        listaDescuentos.setModel(listamodelo);
        panelEditar.setVisible(false);
    }//GEN-LAST:event_btnVerDescuentosActionPerformed

    private void btnAgregarEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarEmpleadosActionPerformed
        frmAgregarEmpleado.setVisible(true);
        frmAgregarEmpleado.setLocationRelativeTo(null);
    }//GEN-LAST:event_btnAgregarEmpleadosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultPieDataset datosgrafico = new DefaultPieDataset();
        
        for (Descuento ld:vectordescuentos){
            datosgrafico.setValue(ld.getEmpleado().getCategoria().toString(), ld.getTotaldescuento());
        }
        
        JFreeChart chart = ChartFactory.createPieChart3D("Grafico de gastos por categoria", datosgrafico, true, true, false);
        ChartFrame framegrafico = new ChartFrame("Gastos por categoria", chart);
        framegrafico.pack();
        framegrafico.setLocationRelativeTo(null);
        framegrafico.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnHistoricoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricoActionPerformed
        DefaultCategoryDataset datoshistorico = new DefaultCategoryDataset();
        
        //agrego datos al grafico
        for (Descuento h:vectordescuentos){
            datoshistorico.addValue(h.getTotaldescuento(), h.getEmpleado().toString(), h.getConcepto().toString());
        }
        
        JFreeChart chart2 = ChartFactory.createBarChart3D("Descuentos Historico", "Concepto", "Descuento aplicado en pesos ($)", datoshistorico, PlotOrientation.VERTICAL, true, false, false);
        ChartFrame framegrafico2 = new ChartFrame("Descuentos Historico", chart2);
        framegrafico2.pack();
        framegrafico2.setLocationRelativeTo(null);
        framegrafico2.setVisible(true);
    }//GEN-LAST:event_btnHistoricoActionPerformed

    private void rbtnCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnCodActionPerformed
        busqueda = true;
        txtBuscar.setText("");
        txtBuscar.grabFocus();
        frmOpciones.setVisible(false);
    }//GEN-LAST:event_rbtnCodActionPerformed

    private void rbtnNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnNombreActionPerformed
        busqueda = false;
        txtBuscar.setText("");
        txtBuscar.grabFocus();
        frmOpciones.setVisible(false);
    }//GEN-LAST:event_rbtnNombreActionPerformed

    private void txtBuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyTyped
        String buscar = txtBuscar.getText().toLowerCase();
        listamodelo.clear();
        
        if (busqueda){
            for (Descuento desc:vectordescuentos){
                String buscaren = String.valueOf(desc.getCod());
                if (buscaren.contains(buscar)){
                listamodelo.addElement(desc);
                }
            }
        }
        else{
            for (Descuento desc:vectordescuentos){
                String buscaren = String.valueOf(desc.getEmpleado().toString().toLowerCase());
                if (buscaren.contains(buscar)){
                listamodelo.addElement(desc);
                }
            }
        }

        listaDescuentos.setModel(listamodelo);
        
        //presiona enter
        try{
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        }catch(AWTException a){
        }
        
    }//GEN-LAST:event_txtBuscarKeyTyped

    private void btnTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTablaActionPerformed
        String[] nombrecolumnas =  {"Codigo", "Empleado", "Importe", "Importe final", "Conceptos"};
        Vector<Integer> codigos = new Vector<Integer>();
        Vector<String> empleados = new Vector<String>();
        Vector<Double> importes = new Vector<Double>();
        Vector<Double> importescondescuento = new Vector<Double>();
        Vector<String> conceptos = new Vector<String>();
        
        for (Descuento ld:vectordescuentos){
            codigos.add(ld.getCod());
            empleados.add(ld.getEmpleado().toString());
            importes.add(ld.getImporte());
            // recorto los decimales
            BigDecimal bd = new BigDecimal(ld.getTotaldescuento());
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            
            importescondescuento.add(bd.doubleValue());
            conceptos.add(ld.getConcepto().toString());
        }
        
        DefaultTableModel tabla = new DefaultTableModel();
        tabla.addColumn(nombrecolumnas[0], codigos);
        tabla.addColumn(nombrecolumnas[1], empleados);
        tabla.addColumn(nombrecolumnas[2], importes);
        tabla.addColumn(nombrecolumnas[3], importescondescuento);
        tabla.addColumn(nombrecolumnas[4], conceptos);

        jTable1.setModel(tabla);
        frmTabla.setLocationRelativeTo(null);
        frmTabla.setVisible(true);
    }//GEN-LAST:event_btnTablaActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        try {
            exportarExcel(jTable1);
        } catch (IOException e) {
        }
    }//GEN-LAST:event_btnExportarActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (jComboBox1.getModel().getSelectedItem().toString().equals("Código")){
            busqueda = true;
        }
        else{
            busqueda = false;
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmInicio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JscrollDescuentos;
    private javax.swing.JButton btnAgregar2;
    private javax.swing.JButton btnAgregarEmpleado;
    private javax.swing.JButton btnAgregarEmpleados;
    private javax.swing.JButton btnAgregarItem;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCancelarAgregarEmpleado;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCerrarApp;
    private javax.swing.JButton btnColorError;
    private javax.swing.JButton btnEditarDescuento;
    private javax.swing.JButton btnEliminarDescuento;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnHistorico;
    private javax.swing.JButton btnTabla;
    private javax.swing.JButton btnVerDescuentos;
    private javax.swing.JComboBox cboCategoria;
    private javax.swing.JComboBox cboConcep;
    private javax.swing.JComboBox cboEditConcep;
    private javax.swing.JComboBox cboEditEmpleados;
    private javax.swing.JComboBox cboEditTipo;
    private javax.swing.JComboBox cboEmpleados;
    private javax.swing.JComboBox cboTipo;
    private javax.swing.JFrame frmAgregarEmpleado;
    private javax.swing.JFrame frmDescuentos;
    private javax.swing.JFrame frmOpciones;
    private javax.swing.JFrame frmTabla;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblError;
    private javax.swing.JList listaDescuentos;
    private java.awt.Panel panel1;
    private java.awt.Panel panelAgregar;
    private java.awt.Panel panelContenedor;
    private javax.swing.JPanel panelEditar;
    private javax.swing.JPanel panelOpciones;
    private javax.swing.JRadioButton rbtnCod;
    private javax.swing.JRadioButton rbtnNombre;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtCodigoEmpleado;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtEditCodigo;
    private javax.swing.JTextField txtEditImporte;
    private javax.swing.JTextField txtImporte;
    private javax.swing.JTextField txtImporteConDescuento;
    private javax.swing.JTextField txtNombreEmpleado;
    // End of variables declaration//GEN-END:variables

}
