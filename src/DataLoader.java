import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class DataLoader {

    private ArrayList<ArrayList<Double>[]>[] train;
    private ArrayList<ArrayList<Double>[]>[] validation;
    private int numInput;
    private int numOutput;
    private int batchSize;
    private Network model;

    public DataLoader(String fileName, int batchSize, int numInput, int numOutput, double valSplit, int seed) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

        this.numInput = numInput;
        this.numOutput = numOutput;

        String line;
        ArrayList<ArrayList<Double>[]> fullTrain = new ArrayList<>();
        Random r = new Random(seed);
        ArrayList<ArrayList<Double>[]> fullVal = new ArrayList<>();

        while((line = br.readLine()) != null && !line.equals("")){
            StringTokenizer str = new StringTokenizer(line);

            boolean position = r.nextDouble() > valSplit;

            if(position){
                fullTrain.add(new ArrayList[2]);
                fullTrain.get(fullTrain.size() - 1)[0] = new ArrayList<>();
                fullTrain.get(fullTrain.size() - 1)[1] = new ArrayList<>();

            }
            else{
                fullVal.add(new ArrayList[2]);
                fullVal.get(fullVal.size() - 1)[0] = new ArrayList<>();
                fullVal.get(fullVal.size() - 1)[1] = new ArrayList<>();

            }



            for (int i = 0; i < numInput; i++) {
                if(position) fullTrain.get(fullTrain.size() - 1)[0].add(Double.parseDouble(str.nextToken()));
                else{
                    fullVal.get(fullVal.size() - 1)[0].add(Double.parseDouble(str.nextToken()));
                }
            }

            for (int i = 0; i < numOutput; i++) {
                if(position) fullTrain.get(fullTrain.size() - 1)[1].add(Double.parseDouble(str.nextToken()));
                else fullVal.get(fullVal.size() - 1)[1].add(Double.parseDouble(str.nextToken()));
            }
        }

        int numTrain = fullTrain.size() / batchSize;
        int numVal = fullVal.size() / batchSize;

        train = new ArrayList[numTrain];
        validation = new ArrayList[numVal];

        for (int i = 0; i < numTrain; i++) {
            train[i] = new ArrayList<>();
        }

        for (int i = 0; i < numVal; i++) {
            validation[i] = new ArrayList<>();
        }

//        for (ArrayList<Double>[] arrayLists : fullTrain) {
//            train[r.nextInt(numTrain)].add(arrayLists);
//        }
//
//        for (ArrayList<Double>[] arrayLists : fullVal) {
//            validation[r.nextInt(numVal)].add(arrayLists);
//        }

        for (int i = 0; i < fullTrain.size(); i++) {
            train[i].add(fullTrain.get(i));
        }

        this.batchSize = batchSize;

        System.out.println("Finished Loading Data");
    }

    public void setModel(Network model) throws Exception {
        if(model.getInputNodes() != numInput) throw new Exception("Your input size for the model doesn't match input provided.");
        else if(model.getOutputNodes() != numOutput) throw new Exception("Your output size for the model doesn't match output provided.");
        this.model = model;
    }

    public void train(int numEpochs, double learningRate) throws Exception {
        for (int i = 0; i < numEpochs; i++) {
            double avgLoss = 0;

            Random r = new Random();

            avgLoss = model.train(train[r.nextInt(train.length)], learningRate);

//            for (ArrayList<ArrayList<Double>[]> arrayLists : train) {
//                avgLoss += model.train(arrayLists, learningRate);
//            }


//            for (ArrayList<ArrayList<Double>[]> list : validation) {
//                avgLoss += model.validate(list);
//            }
//
//            avgLoss /= (double) train.length;

            System.out.printf("Epoch %d : loss = %.5f\n", i, avgLoss);
        }
    }

    public Matrix predict(ArrayList<Double> input) throws Exception {
        return model.predict(input);
    }

    public static void main(String[] args) throws Exception {
        DataLoader d = new DataLoader("xor3.txt", 1, 2, 1, 0,100);
        d.setModel(new Network(2,2,1, 100));
        d.train(1000, .1);

        ArrayList<Double> input = new ArrayList<>();
        input.add(0.0);
        input.add(1.0);

        System.out.println(d.predict(input));
    }


}
