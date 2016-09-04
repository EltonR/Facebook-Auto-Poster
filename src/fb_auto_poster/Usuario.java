package fb_auto_poster;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Usuario implements Serializable{
    private String email;
    private String senha;
    private Date fim;
    private Date inicio;
    private String fim_s;
    private String inicio_s;
    private ArrayList<Grupo> lista_grupos;
    private int postagens;

    @Override
    public String toString() {
        return "Usuario{" + "email=" + email + ", senha=" + senha + ", fim=" + fim + ", lista_grupos=" + lista_grupos + '}';
    }

    public Usuario() {
        lista_grupos = new ArrayList<>();
        postagens = 0;
        inicio_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        fim_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

   

    public String posta(WebDriver driver, String postagem){
        for(int i=0; i<lista_grupos.size(); i++){
            if(lista_grupos.get(i).isCheck() && !lista_grupos.get(i).isPostado()){
                driver.get("http://facebook.com"+lista_grupos.get(i).getEndereco());
                int dialogResult = JOptionPane.showConfirmDialog (null, "Confirma postagem Aqui?");
                if(dialogResult == JOptionPane.NO_OPTION){
                    lista_grupos.get(i).setPostado(true);
                    return "OK - "+lista_grupos.get(i).getEndereco();
                }
                try{ //Tentativa #1 de encontrar a aba "Iniciar discussão"
                    WebElement we = driver.findElement(By.cssSelector("#pagelet_group_composer"));
                    List<WebElement> w1 = we.findElements(By.xpath(".//*"));
                    JavascriptExecutor js = (JavascriptExecutor)driver;
                    if(w1.get(13).getText().equalsIgnoreCase("Iniciar discussão")){
                        js.executeScript("arguments[0].click();", w1.get(13));
                        System.out.println("Clicado: "+13+">>>"+w1.get(13).getAttribute("innerHTML"));
                    }
                }catch(Exception e){
                    System.out.println("13: Falha ao tentar encontrar \"Iniciar Discussao\": "+e.getMessage());
                }
                try{//Tentativa #2 de encontrar a aba "Iniciar discussão"
                    WebElement we = driver.findElement(By.cssSelector("#pagelet_group_composer"));
                    List<WebElement> w1 = we.findElements(By.xpath(".//*"));
                    JavascriptExecutor js = (JavascriptExecutor)driver;
                    if(w1.get(14).getText().equalsIgnoreCase("Iniciar discussão")){
                        js.executeScript("arguments[0].click();", w1.get(14));
                        System.out.println("Clicado: "+14+">>>"+w1.get(14).getAttribute("innerHTML"));
                    }
                }catch(Exception e){
                    System.out.println("14: Falha ao tentar encontrar \"Iniciar Discussao\": "+e.getMessage());
                }
                
                boolean deu = false;
                int fudeu = 1;
                while(!deu){
                    if(fudeu==25){
                        lista_grupos.get(i).setPostado(true);
                        return "OK - "+lista_grupos.get(i).getNome();
                    }
                    try{
                        WebElement we = driver.findElement(By.cssSelector("#pagelet_group_composer"));
                        List<WebElement> w1 = we.findElements(By.xpath(".//*"));
                        w1.get(44).sendKeys(postagem);
                        deu = true;
                    }catch(Exception e){
                        System.out.println(">Erro "+fudeu+" inserindo post no grupo: "+lista_grupos.get(i).getNome()+"... Tentando novamente...\n");
                        fudeu++;
                    }
                }
                deu=false;
                while(!deu){
                    List<WebElement> wee = driver.findElements(By.xpath("//button[@type='submit'][@value='1'][@data-testid='react-composer-post-button']"));
                    for(int j=0; j<wee.size(); j++){
                        if(wee.get(j).getText().equalsIgnoreCase("Publicar")){
                            try{
                                //wee.get(j).click();
                                JavascriptExecutor js = (JavascriptExecutor)driver;
                                js.executeScript("arguments[0].click();", wee.get(j));
                                deu = true;
                                lista_grupos.get(i).setPostado(true);
                                return "OK - "+lista_grupos.get(i).getNome();
                            }catch(Exception e){
                                System.out.println(">Erro no botão Publicar no grupo: "+lista_grupos.get(i).getNome()+"... Tentando novamente...\n");
                            }
                        }
                    }
                }
            }
        }
        return "FINISH";
    }
    
    private void confirmaPost(){
        
    }
    
    public boolean terminou(){
        for(int i=0; i<lista_grupos.size(); i++){
            if(lista_grupos.get(i).isCheck())
                if(!lista_grupos.get(i).isPostado())
                    return false;
        }
        return true;
    }
    
    public void calculaFim(int intervalo){
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        inicio_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        cal.add(Calendar.MINUTE, intervalo); // adds one hour
        fim_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());        
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public ArrayList<Grupo> getLista_grupos() {
        return lista_grupos;
    }

    public void setLista_grupos(ArrayList<Grupo> lista_grupos) {
        this.lista_grupos = lista_grupos;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getFim_s() {
        return fim_s;
    }

    public void setFim_s(String fim_s) {
        this.fim_s = fim_s;
    }

    public String getInicio_s() {
        return inicio_s;
    }

    public void setInicio_s(String inicio_s) {
        this.inicio_s = inicio_s;
    }
    
}
