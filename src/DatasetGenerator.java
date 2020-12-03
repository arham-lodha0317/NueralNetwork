import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class DatasetGenerator {

    public static void main(String[] args) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new FileOutputStream("xor3.txt"));
        StringBuilder sb = new StringBuilder();


        for (int i = 0; i <= 1<<5; i++) {
            for (int j = 0; j <= 1<<5; j++) {
                sb.append(String.format("%d %d %d\n", i , j, i^j));
            }
        }

        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }

}
