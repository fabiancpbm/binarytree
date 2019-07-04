import java.io.RandomAccessFile;

public class GeneratorSubFiles {
    public static void main(String[] args) throws Exception {
        RandomAccessFile bagFile = new RandomAccessFile("bolsa.csv", "r");
        RandomAccessFile ABagFile = new RandomAccessFile("bolsaA.csv", "rw");
        RandomAccessFile BBagFile = new RandomAccessFile("bolsaB.csv", "rw");

        String line = bagFile.readLine()  + "\n";
        int count = 1;
        while (bagFile.getFilePointer() < bagFile.length() && count <= 200) {
            ABagFile.write(line.getBytes());
            line = bagFile.readLine()  + "\n";
            count++;
        }

        ABagFile.close();
        bagFile.seek(0);

        line = bagFile.readLine() + "\n";
        count = 1;
        while (bagFile.getFilePointer() < bagFile.length() && count <= 100) {
            BBagFile.write(line.getBytes());
            line = bagFile.readLine()  + "\n";
            count++;
        }
        BBagFile.close();
        bagFile.close();
    }
}
