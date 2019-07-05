import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsável por criar um arquivo que possui a ordenação binária do arquivo passado como parâmetro.
 */
public class BTreeSort {

    /** Raiz da árvore B. */
    private Page root;

    /**
     * Construtor.
     *
     * @param filePath Caminho do arquivo que será estruturado em árvore B.
     */
    public BTreeSort(String filePath) {
        renderBTree(filePath);
    }

    /**
     * Busca um NIS na árvore B.
     *
     * @param nis NIS.
     * @return true, se o NIS procurado existir na estrutura da árvore.
     */
    public boolean searchNis(String nis) {
        return search(root, nis);
    }

    private boolean search(Page page, String nis) {
        if (page == null) {
            return false;
        }
        for (Key key : page.keys) {
            if (nis.compareTo(key.nis) == 0) {
                return true;
            } else if (nis.compareTo(key.nis) < 0) {
                if (key.equals(page.keys.get(0))) {
                    return search(page.leftPage, nis);
                } else if (nis.compareTo(page.keys.get(page.keys.indexOf(key) - 1).nis) > 0) {
                    return search(page.keys.get(page.keys.indexOf(key) - 1).rightPage, nis);
                } else {
                    continue;
                }
            } else {
                if (key.equals(page.keys.get(page.keys.size() - 1)) || nis.compareTo(page.keys.get(page.keys.indexOf(key) + 1).nis) < 0) {
                    return search(key.rightPage, nis);
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    /**
     * Constrói a raiz da árvore B feita para o arquivo B.
     */
    private void renderBTree(String filePath) {
        // Testando a existência do arquivo.
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
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
    private void sort(RandomAccessFile file) throws IOException {

        // A quantidade de chaves é o tamanho do bloco dividido pelo tamanho da string do NIS.
        short nisTextSize = 14;
        short blockSize = 1024;
        short keyQtd = (short) (blockSize / nisTextSize);
        System.out.println("[INFO] - Quantidade de chaves por página: " + keyQtd);

        // Cria a Raíz
        root = new Page(keyQtd);

        file.readLine();
        while (file.getFilePointer() < file.length()) {
            String nis = readNis(file);
            insert(root, nis);
        }
    }

    /**
     * Função de inserção recursiva.
     *
     * @param page Raiz.
     * @param nis  NIS.
     */
    private void insert(Page page, String nis) {
        if (page.isLeaf()) {
            page.add(new Key(nis));
            if (page.keys.size() > page.keyQtd) {
                page.split();
            }
        } else {
            Page childrenPage = page.getNextChildrenPage(nis);
            insert(childrenPage, nis);
        }
    }


    /**
     * Lê o arquivo e retorna o próximo NIS.
     *
     * @param file Arquivo de NIS.
     * @return NIS.
     * @throws IOException
     */
    private String readNis(RandomAccessFile file) throws IOException {
        String line = file.readLine();
        String columns[] = line.split("\t");
        String nis = columns[7];
        return nis;
    }


    /**
     * Página com um tamanho passado como parâmetro.
     */
    private class Page {

        /**
         * Capacidade máxima de chaves por página.
         */
        protected short keyQtd;

        /**
         * Página do pai.
         */
        protected Page parent;

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
         * @param key Chave.
         */
        public void add(Key key) {
            keys.add(key);
            keys.sort((o1, o2) -> o1.nis.compareTo(o2.nis));

        }

        /**
         * Verifica se a chave é um nó folha.
         *
         * @return true, se for nó folha.
         */
        public boolean isLeaf() {
            boolean isKeyLeaf = true;
            for (Key key : keys) {
                isKeyLeaf = key.rightPage == null;
                if (!isKeyLeaf) {
                    break;
                }
            }
            return leftPage == null && isKeyLeaf;
        }

        public Page getNextChildrenPage(String nis) {
            for (int x = 0; x < keys.size(); x++) {
                Key key = keys.get(x);
                if (x == 0 && nis.compareTo(key.nis) < 0 && leftPage != null) {
                    return leftPage;
                } else if (nis.compareTo(key.nis) > 0) {
                    if (x == keys.size() - 1) {
                        return key.rightPage;
                    } else if (nis.compareTo(keys.get(x + 1).nis) < 0) {
                        return key.rightPage;
                    }
                }
            }
            return null;
        }

        public void split() {
            int middle = keys.size() / 2;
            Page leftPage = copyLeftChildren(middle);
            Page rightPage = copyRightChildren(middle);

            if (parent == null) {
                parent = new Page(keyQtd);
                root = parent;
            }

            Key promotedKey = keys.get(middle);
            parent.add(promotedKey);
            if (parent.keys.size() > parent.keyQtd) {
                parent.split();
            }

            if (parent.keys.get(0) == promotedKey) {
                parent.leftPage = leftPage;
            } else {
                parent.keys.get(parent.keys.indexOf(promotedKey) - 1).rightPage = leftPage;
            }
            promotedKey.rightPage = rightPage;
        }

        private Page copyLeftChildren(int middle) {
            Page page = new Page(keyQtd);
            for (int i = 0; i < middle; i++) {
                page.add(keys.get(i));
            }
            return page;
        }

        public Page copyRightChildren(int middle) {
            middle = middle + 1;
            Page page = new Page(keyQtd);
            for (int i = middle; i < keys.size(); i++) {
                page.add(keys.get(i));
            }
            return page;
        }
    }

    /**
     * Chave.
     */
    private class Key {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(nis, key.nis);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nis);
        }
    }
}
