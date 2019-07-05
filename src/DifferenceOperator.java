import java.io.RandomAccessFile;

/**
 * Responsável por realizar a operação de diferença entre dois arquivos.
 */
public class DifferenceOperator {
    public static void main(String[] args) throws Exception {
        System.out.println("[INFO] - Abrindo arquivos.");
        RandomAccessFile fileA = new RandomAccessFile("bolsaA.csv", "r");
//        RandomAccessFile bTreeFileB = new RandomAccessFile("BTreeBolsaB.dat", "r");
        RandomAccessFile fileDiff = new RandomAccessFile("diferenca.csv", "rw");

        // Percorrendo o arquivo A linearmente.
        System.out.println("[INFO] - Realizando operação de diferença.");
        String line;
        String nis;
        String column[];
        fileDiff.write(fileA.readLine().getBytes());
        while(fileA.getFilePointer() < fileA.length()) {
            line = fileA.readLine();
            column = line.split("\t");
            nis = column[7];
            // Fazer busca no arquivo de árvore B, procurando pelo nis atual do arquivo A
            if (!BTreeSort.searchNis(nis)) {
                // Colocar as informações num 3° arquivo.
                fileDiff.write((line + "\n").getBytes());
            }
        }
        System.out.println("[INFO] - Finalizando operação.");
        fileA.close();
//        bTreeFileB.close();
        fileDiff.close();
    }
}
