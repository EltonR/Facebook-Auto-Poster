package fb_auto_poster;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Menu extends javax.swing.JFrame {
    
    
    
    
    boolean continua_ultima = false;

    WebDriver driver;
    ArrayList<Usuario> lista_users;
    ArrayList<String> tabs;
    ArrayList<String> listaEnderecos;
    int num_Selecionado=0;
    
    int tempo_intervalo = 0;
    int num_postagens = 0;
    String postagem;
    Random random;
    
    public Menu() {
        initComponents();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.get("http://www.facebook.com");
        random = new Random();
        lista_users=new ArrayList<>();
        if(!continua_ultima){
            try{
                FileWriter fw = new FileWriter("grupos.txt", false);
                fw.write("");
                fw.close();
            }catch(Exception e){
                
            }
        }
            
    }

    private void dorme(int miliseconds){
        try {
            Thread.sleep(miliseconds);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void getGrupos(){
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
        tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0));
        driver.get("https://www.facebook.com/groups/?category=membership");
        rolagem();
        String[] html = driver.getPageSource().split("\n");
        for(int i=0; i<html.length; i++){
            if(html[i].contains("href=\"/groups/")){
                String[] s = html[i].split("href=\"/groups/");
                for(int j=0; j<s.length; j++){
                    if(s[j].contains("class=\"groupsRecommendedTitle\">")){
                        Grupo g = new Grupo();
                        String[] s2 = s[j].split("class=\"groupsRecommendedTitle\">");
                        g.setEndereco("/groups/"+s2[0].replace("\"", ""));
                        String[] s3 = s2[1].split("</a>");
                        g.setNome(s3[0]);
                        g.setCheck(false);
                        if(checaGrupoJaPostados(g.getEndereco()))
                            g.setPostado(true);
                        else
                            g.setPostado(false);
                        lista_users.get(0).getLista_grupos().add(g);
                    }
                }
            }
        }
        for(int i=0; i<lista_users.size(); i++){
            for(int j=0; j<lista_users.get(i).getLista_grupos().size(); j++)
                lista_users.get(i).getLista_grupos().get(j).setNumero(j);
        }
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"w");
        jLabel11.setVisible(false);
        int dialogResult = JOptionPane.showConfirmDialog (null, "Checar Grupos a partir de arquivo?");
        if(dialogResult == JOptionPane.YES_OPTION){
            FileReader fr; 
            try {
                fr = new FileReader("grupos.txt");
                BufferedReader br = new BufferedReader(fr); 
                String s; 
                while((s = br.readLine()) != null) { 
                    for(int i=0; i<lista_users.size(); i++){
                        for(int j=0; j<lista_users.get(i).getLista_grupos().size(); j++){
                            if(lista_users.get(i).getLista_grupos().get(j).getEndereco().equalsIgnoreCase(s.trim()))
                                lista_users.get(i).getLista_grupos().get(j).setPostado(true);
                        }
                    }
                }
                fr.close(); 
            } catch (Exception ex) {
                
            }
            jLabel11.setVisible(false);
            atualizaGrupos();
            return;
        }
        atualizaGrupos();
    }
    
    private boolean checaGrupoJaPostados(String endereco){
        for(int i=0; i<lista_users.size(); i++){
            for(int j=0; j<lista_users.get(i).getLista_grupos().size(); j++){
                if(lista_users.get(i).getLista_grupos().get(j).getEndereco().equalsIgnoreCase(endereco))
                    if(lista_users.get(i).getLista_grupos().get(j).isPostado())
                        return true;
            }
        }
        return false;
    }
    
    private void rolagem(){
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        for(int i=0; i<50; i++){
            jse.executeScript("window.scrollBy(0,3000)", "");
            try {
                Thread.sleep(240);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void atualizaGrupos(){
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        while(model.getRowCount()>0)
            model.removeRow(0);
        String parm1 = jTextField6.getText();
        String parm2 = jTextField7.getText();
        String parm3 = jTextField8.getText();
        for(int i=0; i<lista_users.get(0).getLista_grupos().size(); i++){
            if(jCheckBox7.isSelected() && jCheckBox5.isSelected()){
                if(lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm1.toLowerCase()) || lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm2.toLowerCase()) || lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm3.toLowerCase())){
                    model.addRow(new Object[]{lista_users.get(0).getLista_grupos().get(i).getNumero(), lista_users.get(0).getLista_grupos().get(i).getNome(), lista_users.get(0).getLista_grupos().get(i).getEndereco(), lista_users.get(0).getLista_grupos().get(i).isCheck()});
                }
            }
            else if(jCheckBox5.isSelected()){
                if(lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm1.toLowerCase()) || lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm2.toLowerCase())){
                    model.addRow(new Object[]{lista_users.get(0).getLista_grupos().get(i).getNumero(), lista_users.get(0).getLista_grupos().get(i).getNome(), lista_users.get(0).getLista_grupos().get(i).getEndereco(), lista_users.get(0).getLista_grupos().get(i).isCheck()});
                }
            }else{
                if(lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(parm1.toLowerCase())){
                    model.addRow(new Object[]{lista_users.get(0).getLista_grupos().get(i).getNumero(), lista_users.get(0).getLista_grupos().get(i).getNome(), lista_users.get(0).getLista_grupos().get(i).getEndereco(), lista_users.get(0).getLista_grupos().get(i).isCheck()});
                }
            }
        }
        jButton10.setEnabled(true);
        jButton12.setEnabled(true);
    }
    
    private void posta(){
        postagem = jTextArea1.getText();
        tempo_intervalo = Integer.valueOf(jTextField4.getText());
        num_postagens = Integer.valueOf(jTextField5.getText());
        
        while(lista_users.get(0).getFim_s().compareTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())) > 0){
            jTextArea2.append(lista_users.get(0).getEmail()+"> Agora: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())+". Recomeço: "+lista_users.get(0).getFim_s()+". Dormindo 15 segundos.");
            dorme(15000);
        }
        for(int i=0; i<num_postagens; i++){
            String r = lista_users.get(0).posta(driver, postagem);
            if(r.equalsIgnoreCase("FINISH")){
               if(terminou()){
                   return;
               }
            }else if(r.startsWith("OK")){
                String[] s = r.split(" - ");
                marcaPostado(s[1]);
                dorme(6000);
            }
            jTextArea2.append(lista_users.get(0).getEmail()+r);
        }
        lista_users.get(0).calculaFim(tempo_intervalo);
        logout();
        login();
    }
    
    private void login(){
        if(lista_users.size()<1){
            String usuario = jTextField2.getText();
            String senha = jTextField3.getText();
            TableModel model = jTable1.getModel();
            lista_users = new ArrayList<>();
            for (int count = 0; count < model.getRowCount(); count++){
                Usuario u = new Usuario();
                u.setEmail(model.getValueAt(count, 0).toString());
                u.setSenha(model.getValueAt(count, 1).toString());
                lista_users.add(u);
            }
            if(jCheckBox1.isSelected()){
                long seed = System.nanoTime();
                Collections.shuffle(lista_users, new Random(seed));
            }
            Usuario u = lista_users.get(0); //MANTER EM ZERO!!!!!!!!!!!!!!!!!!!!!!!
            usuario = u.getEmail();
            senha = u.getSenha();
            WebElement wMail = driver.findElement(By.cssSelector("#email"));
            WebElement wPass = driver.findElement(By.cssSelector("#pass"));
            wMail.sendKeys(usuario);
            wPass.sendKeys(senha);
        }else{
            Usuario u = new Usuario();
            u.setEmail(lista_users.get(0).getEmail());
            u.setSenha(lista_users.get(0).getSenha());
            u.setInicio(lista_users.get(0).getInicio());
            u.setFim(lista_users.get(0).getFim());
            u.setFim_s(lista_users.get(0).getFim_s());
            u.setInicio_s(lista_users.get(0).getInicio_s());
            u.setLista_grupos(lista_users.get(0).getLista_grupos());
            lista_users.remove(0);
            lista_users.add(u);
            WebElement wMail = driver.findElement(By.cssSelector("#email"));
            WebElement wPass = driver.findElement(By.cssSelector("#pass"));
            wMail.sendKeys(lista_users.get(0).getEmail());
            wPass.sendKeys(lista_users.get(0).getSenha());
        }
        WebElement w = driver.findElement(By.cssSelector("#loginbutton"));
        w.click();
        if(lista_users.get(0).getLista_grupos().size()<1){
            Thread one = new Thread() {
                public void run() {
                    getGrupos();
                }  
            };
            one.start();
        }else{
            atualizaGrupos();
            posta();
        }
        jLabel9.setText(lista_users.get(0).getEmail());
        jLabel9.setForeground(Color.black);
        jButton2.setEnabled(false);
        jButton9.setEnabled(true);
    } 
    
    private void logout(){
        driver.manage().deleteAllCookies();
        driver.get("www.facebook.com");
        jButton9.setEnabled(false);
        jButton2.setEnabled(true);
    }
    
    private boolean terminou(){
        for(int i=0; i<lista_users.size(); i++)
            if(!lista_users.get(i).terminou())
                return false;
        return true;
    }
    
    private void marcaPostado(String enderecoGrupo){
        for(int i=0; i<lista_users.size(); i++){
            for(int j=0; j<lista_users.get(i).getLista_grupos().size(); j++){
                if(lista_users.get(i).getLista_grupos().get(j).getEndereco().equalsIgnoreCase(enderecoGrupo)){
                   lista_users.get(i).getLista_grupos().get(j).setPostado(true);
                   break;
                }
            }
        }
        escreve(enderecoGrupo, "grupos.txt");
    }
    
    private static void escreve(String string, String file){
        try{
            FileWriter fw = new FileWriter(file, true); //the true will append the new data
            fw.write(string);//appends the string to the file
            fw.close();
        }catch(Exception ioe){
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jTextField7 = new javax.swing.JTextField();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jTextField8 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton7 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Elton Rasch's Facebook Auto Publisher");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User", "Password"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setText("Login List (for random login)");

        jButton3.setText("Add User to List");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("User");

        jLabel3.setText("Password:");

        jCheckBox1.setText("Random");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jButton2.setText("Login");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel8.setText("Status:");

        jLabel9.setForeground(new java.awt.Color(255, 0, 51));
        jLabel9.setText("Not logged!");

        jButton9.setText("Logout");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jButton9))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(312, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Login", jPanel1);

        jButton10.setText("Select None");
        jButton10.setEnabled(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField6KeyTyped(evt);
            }
        });

        jLabel10.setText("Find (which contains):");

        jCheckBox4.setText("AND");

        jCheckBox5.setText("OR");

        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jCheckBox6.setText("AND");

        jCheckBox7.setText("OR");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Group Number", "Group Name", "Group Adress", "Include to Post List?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable3MouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(jTable3);

        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jButton12.setText("Select All");
        jButton12.setEnabled(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autopostar/waiting.gif"))); // NOI18N
        jLabel11.setText("Loading...");

        jButton4.setText("Export Selected");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Open 15 Selected");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox5)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jButton10)
                    .addComponent(jLabel11)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Groups", jPanel5);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("This is a test post!");
        jScrollPane2.setViewportView(jTextArea1);

        jCheckBox2.setText("Remover Links");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jCheckBox2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Content", jPanel2);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Como postar"));

        jLabel5.setText("Wait");

        jTextField4.setText("5");
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel6.setText("minutes for every");

        jTextField5.setText("13");

        jLabel7.setText("posts");

        jCheckBox3.setText("Use the interval to post with other users of the random list");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jButton7.setText("Post");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jLabel12.setText("Output:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton7))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addComponent(jCheckBox3)
                            .addComponent(jLabel12))
                        .addGap(0, 414, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Options", jPanel4);

        jLabel1.setText("Open URL:");

        jTextField1.setText("http://www.facebook.com");

        jButton1.setText("Open");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(43, 43, 43)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String url = jTextField1.getText();
        driver.get(url);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        posta();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed

    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField6KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyTyped

    }//GEN-LAST:event_jTextField6KeyTyped

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        atualizaGrupos();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        login();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        if(jCheckBox1.isSelected()){
            jTextField2.setEnabled(false);
            jTextField3.setEnabled(false);
        }else{
            jTextField2.setEnabled(true);
            jTextField3.setEnabled(true);
        }
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{"",""});
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int n = jTable1.getSelectedRow();
        if(jTextField2.isEnabled()){
            TableModel model = jTable1.getModel();
            jTextField2.setText(model.getValueAt(n, 0).toString());
            jTextField3.setText(model.getValueAt(n, 1).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        logout();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        for(int i=0; i<lista_users.get(0).getLista_grupos().size(); i++)
            if(lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(jTextField6.getText().toLowerCase()))
                lista_users.get(0).getLista_grupos().get(i).setCheck(true);
        atualizaGrupos();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        for(int i=0; i<lista_users.get(0).getLista_grupos().size(); i++)
            if(lista_users.get(0).getLista_grupos().get(i).getNome().toLowerCase().contains(jTextField6.getText().toLowerCase()))
                lista_users.get(0).getLista_grupos().get(i).setCheck(false);
        atualizaGrupos();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTable3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseReleased
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        for(int i=0; i<model.getRowCount(); i++){
            if((Boolean) model.getValueAt(i, 3)){
                lista_users.get(0).getLista_grupos().get((int) model.getValueAt(i, 0)).setCheck(true);
            }else{
                lista_users.get(0).getLista_grupos().get((int) model.getValueAt(i, 0)).setCheck(false);
            }
        }
    }//GEN-LAST:event_jTable3MouseReleased

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        atualizaGrupos();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        atualizaGrupos();
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        listaEnderecos = new ArrayList<>();
        for(int i=0; i<lista_users.get(0).getLista_grupos().size(); i++){
            if(lista_users.get(0).getLista_grupos().get(i).isCheck()){
                listaEnderecos.add("www.facebook.com"+lista_users.get(0).getLista_grupos().get(i).getEndereco()+"\n");
            }
        }
        try {
            FileWriter fw = new FileWriter("SelectedGroups.txt", false);
            for(int i=0; i<listaEnderecos.size(); i++){
                fw.write(listaEnderecos.get(i));
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int n = num_Selecionado;
        while(n < num_Selecionado+15){
            try {
                driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
                driver.get(listaEnderecos.get(n));
                Thread.sleep(800);
            } catch (Exception ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
            n++;
        }
        num_Selecionado = n;
    }//GEN-LAST:event_jButton5ActionPerformed

  
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
