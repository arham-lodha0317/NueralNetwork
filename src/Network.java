import java.lang.reflect.Array;
import java.util.ArrayList;

public class Network {

    private int inputNodes;
    private int hiddenNodes;
    private int outputNodes;

    private ArrayList<Matrix> network;
    private ArrayList<ArrayList<Matrix>> gradients;
    private ArrayList<Double> losses;

    public Network(int numI, int numH, int numO, int seed){
        inputNodes = numI;
        hiddenNodes = numH;
        outputNodes = numO;

        network = new ArrayList<>();
        network.add(new Matrix(numH, numI + 1));
        network.add(new Matrix(numO, numH + 1));

        for (Matrix matrix : network) {
            matrix.randomGaussian(seed);
        }
    }

//    public Network(String string, int[] layers){
//        inputNodes = layers[0];
//        hiddenNodes = layers[1];
//        outputNodes = layers[layers.length - 1];
//        network = new ArrayList<>();
//        network.add(new Matrix(layers[1], layers[0] + 1));
//        network.add(new Matrix(layers[layers.length - 1], layers[1] + 1));
//
//        int index = 0;
//        String[] matrix = string.split(" ");
//        int layerNum = 1;
//        for (Matrix value : network) {
//
//            for (int j = 0; j <= layers[layerNum - 1]; j++) {
//
//                for (int k = 0; k < layers[layerNum]; k++) {
//                    value.setValue(j, k, Double.parseDouble(matrix[index]));
//                    index++;
//                }
//
//            }
//
//            layerNum++;
//
//        }
//
//        gradients = new ArrayList<>();
//
//
//    }

    public Matrix forward(ArrayList<Matrix> activations, ArrayList<Double> input) throws Exception {

        ArrayList<ArrayList<Double>> inputs = new ArrayList<>();
        inputs.add((ArrayList<Double>) input.clone());

        activations.add(new Matrix(inputs));


        for (int i = 0; i < network.size(); i++) {
            activations.get(i).addFirstToRow(1);
            activations.add(giveInput(activations.get(i), i, gradients.get(gradients.size() - 1)));
        }

        return activations.get(activations.size() -1);
    }

    public Matrix predict(ArrayList<Double> input) throws Exception {
        return forward(new ArrayList<Matrix>(), input);
    }

    public Matrix giveInput(Matrix input, int layer, ArrayList<Matrix> gradient) throws Exception {

        // calculate weighted sum
        Matrix sum = input.multiply(network.get(layer));

        //apply activation
        sum = sum.sigmoid();
        gradient.add(new Matrix(network.get(layer).getWidth(), network.get(layer).getHeight(), 0));
        return sum;
    }

    public double train(ArrayList<ArrayList<Double>[]> batch, double learningRate) throws Exception{
        gradients = new ArrayList<>();

        double loss = 0.0;

        for (int i = 0; i < batch.size(); i++) {

            if(batch.get(i)[0].size() != inputNodes) throw new Exception("Input size mismatch");
            else if(batch.get(i)[1].size() != outputNodes) throw new Exception("Output size mismatch");

            ArrayList<Matrix> activations = new ArrayList<>();
            gradients.add(new ArrayList<>());
            Matrix predicted = forward(activations, batch.get(i)[0]);

            ArrayList<ArrayList<Double>> targets = new ArrayList<>();
            targets.add(batch.get(i)[1]);

            Matrix target = new Matrix(targets);
            Matrix difference = predicted.subtract(target);
            loss += predicted.squaredError(target);

            calculateGradient(gradients.get(i), difference, activations);
        }

        loss /= (double) batch.size();

        ArrayList<Matrix> averageGrad = new ArrayList<>();

        for (int i = 0; i < gradients.size(); i++) {
            for (int j = 0; j < gradients.get(i).size(); j++) {
                if(i == 0)averageGrad.add(gradients.get(i).get(j));
                averageGrad.get(j).add_(gradients.get(i).get(j));
            }
        }

        for (Matrix matrix : averageGrad) {
            matrix.multiply_(-learningRate / (double) gradients.size());
        }

        for (int i = 0; i < network.size(); i++) {
            network.get(i).add_(averageGrad.get(i));
        }

        return loss;
    }

    public double validate(ArrayList<ArrayList<Double>[]> batch) throws Exception {
        double loss = 0.0;

        gradients = new ArrayList<>();


        for (int i = 0; i < batch.size(); i++) {
            ArrayList<Matrix> activations = new ArrayList<>();
            gradients.add(new ArrayList<>());

            ArrayList<ArrayList<Double>> out = new ArrayList<>();
            out.add(batch.get(i)[1]);

            Matrix predicted = forward(activations, batch.get(i)[0]);
            Matrix target = new Matrix(out);
            loss += predicted.squaredError(target);
        }

        return loss/(double)batch.size();
    }

    public void calculateGradient(ArrayList<Matrix> gradient, Matrix difference, ArrayList<Matrix> activations){

        for (int c = 0; c < gradient.get(gradient.size() - 1).getMatrix().get(0).size(); c++) {
            gradient.get(gradient.size() - 1).setColumn(c, 2 * difference.getValue(0 , c));
        }



        for (int i = gradient.size() - 2; i >= 0; i--) {

            for (int c = 0; c < gradient.get(i).getMatrix().get(0).size(); c++) {

                double grad = 0.0;

                for (int j = 0; j < gradient.get(i + 1).getMatrix().get(0).size(); j++) {
                    double product = gradient.get(i + 1).getValue(0, j);

                    double activation = activations.get(i + 1).getValue(activations.get(i + 1).getMatrix().size() - 1, c + 1);

                    product *= activation * (1-activation);

                    grad += product;
                }

                gradient.get(i).setColumn(c, grad);

            }

        }

        for (int i = 0; i < gradient.size(); i++) {
            for (int r = 0; r < gradient.get(i).getMatrix().size(); r++) {
                for (int c = 0; c < gradient.get(i).getMatrix().get(r).size(); c++) {
                    gradient.get(i).setValue(r, c, gradient.get(i).getValue(r,c) * activations.get(i).getValue(0, c));
                }
            }
        }

    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        network.forEach(n -> stringBuilder.append(n).append(" "));

        return stringBuilder.toString();
    }

    public int getInputNodes() {
        return inputNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

}
