import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por criar um arquivo que possui a ordenação binária do arquivo passado como parâmetro.
 */
public class BTreeSort {

    private static Page root;

    /**
     * Principal.
     *
     * @param args Caminho do arquivo que é desejado realizar a ordenação
     */
    public static void main(String[] args) {
        // Verificar passagem de argumento.
        if (args.length != 1) {
            System.err.println("O argumento não foi passado corretamente: utilize o nome do arquivo que deseja ordenar.");
            System.exit(1);
        }

        // Testando a existência do arquivo.
        try {
            RandomAccessFile file = new RandomAccessFile(args[0], "r");
            file.readLine();
            // Ordenar o arquivo.
            sort(file);
        } catch (FileNotFoundException e) {
            System.err.println("O arquivo não existe.");
            System.exit(2);
        } catch (IOException e) {
            System.err.println("Não foi poossível ler o arquivo.");
        }

    }

    /**
     * Função que inicia a inserção ordenada na árvore.
     *
     * @param file Arquivo.
     * @throws IOException
     */
    private static void sort(RandomAccessFile file) throws IOException {

        // A quantidade de chaves é o tamanho do bloco dividido pelo tamanho da string do NIS.
        short nisTextSize = 14;
        short blockSize = 1024;
        short keyQtd = (short) (blockSize / nisTextSize);
        System.out.println("[INFO] - Quantidade de chaves por página.");

        // Cria a Raíz
        root = new Page(keyQtd);

        root.add(readNis(file));

        insert(root, file);
    }

    /**
     * Função de inserção recursiva.
     *
     * @param root Raiz.
     * @param file Arquivo de leitura.
     */
    private static void insert(Page root, RandomAccessFile file) {

    }


    /**
     * Lê o arquivo e retorna o próximo NIS.
     *
     * @param file Arquivo de NIS.
     * @return NIS.
     * @throws IOException
     */
    private static String readNis(RandomAccessFile file) throws IOException {
        String line = file.readLine();
        String columns[] = line.split("\t");
        String nis = columns[7];
        return nis;
    }


    /**
     * Busca um NIS na árvore B.
     *
     * @param nis NIS.
     * @return true, se o NIS procurado existir na estrutura da árvore.
     */
    public static boolean searchNis(String nis) {
        return false;
    }

    /**
     * Página com um tamanho passado como parâmetro.
     */
    private static class Page {

        /**
         * Capacidade máxima de chaves por página.
         */
        protected short keyQtd;

        /**
         * Página da esquerda.
         */
        protected Page leftPage;

        /**
         * Lista de chaves.
         */
        protected List<Key> keys;

        /**
         * Construtor.
         *
         * @param keyQtd Quantidade máxima da chave.
         */
        public Page(short keyQtd) {
            this.keyQtd = keyQtd;
            keys = new ArrayList<>();
        }

        /**
         * Adiciona um NIS na página.
         *
         * @param nis NIS a ser adicionado.
         */
        public void add(String nis) {
            Key key = new Key(nis);
            keys.add(key);
        }

        /**
         * Verifica se a chave é um nó folha.
         *
         * @return true, se for nó folha.
         */
        public boolean isLeaf() {
            boolean isKeyLeaf = true;
            for (Key key : keys) {
                isKeyLeaf = key.rightPage != null;
                if (!isKeyLeaf) {
                    break;
                }
            }
            return leftPage == null && isKeyLeaf;
        }
    }

    /**
     * Chave.
     */
    private static class Key {
        /**
         * NIS.
         */
        String nis;

        /**
         * Página da direita.
         */
        Page rightPage = null;

        /**
         * Construtor.
         *
         * @param nis NIS.
         */
        public Key(String nis) {
            this.nis = nis;
        }
    }
}
