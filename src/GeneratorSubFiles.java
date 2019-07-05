import java.io.RandomAccessFile;

/**
 * Gera 2 arquivos de ordem aleatória para ser manipulado pela busca binária e pela operação de diferença de conjunto.
 */
public class GeneratorSubFiles {
    public static void main(String[] args) throws Exception {
        // Criando os arquivos.
        RandomAccessFile bagFile = new RandomAccessFile("bolsa.csv", "r");
        RandomAccessFile ABagFile = new RandomAccessFile("bolsaA.csv", "rw");
        RandomAccessFile BBagFile = new RandomAccessFile("bolsaB.csv", "rw");

        // Percorrenco arquivo de bolsa para formar o primeiro arquivo.
        String line = bagFile.readLine() + "\n";
        int count = 1;
        while (bagFile.getFilePointer() < bagFile.length() && count <= 201) {
            ABagFile.write(line.getBytes());
            line = bagFile.readLine() + "\n";
            count++;
        }
        ABagFile.close(); // Fechando o arquivo.
        bagFile.seek(0); // Retornando o arquivo para a posição inicial. Isso é importante para realizar as operações de conjunto.

        // Percorrenco arquivo de bolsa para formar o segundo arquivo.
        line = bagFile.readLine() + "\n";
        count = 1;
        while (bagFile.getFilePointer() < bagFile.length() && count <= 101) {
            BBagFile.write(line.getBytes());
            line = bagFile.readLine() + "\n";
            count++;
        }
        BBagFile.close();
        bagFile.close();
    }
}
