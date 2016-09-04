package fb_auto_poster;

public class Grupo {
    
    private String nome;
    private String endereco;
    private boolean check;
    private boolean postado;
    private int numero;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isPostado() {
        return postado;
    }

    public void setPostado(boolean postado) {
        this.postado = postado;
    }
    
    
}
