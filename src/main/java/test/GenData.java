package test;

import jkset.*;

import java.io.IOException;


public class GenData {
    private static double[] run(Kset k, double[] data) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            k.setExternalStimulus(data[i]);
            k.run();
            out[i] = k.getOutput();
            Config.incTime();
        }
        return out;
    }

    public static void main(String[] args) throws IOException {
        double[][] in = DataIO.read("in.csv");
        double[][] out0 = {run(new KO(), in[0])};
        DataIO.write(out0, "out0.csv");

        double[][] out1 = {run(new KI(0.8, 1.1), in[0])};
        DataIO.write(out1, "out1.csv");

        double[][] out2 = {run(new KII(1.2, 1.5, -1, -1.0), in[0])};
        DataIO.write(out2, "out2.csv");

        double[][] in2 = DataIO.read("in2-1.csv");

        KIII k = new KIII(2, 16, 2);
        DataIO.write(k.run(in2), "out3_default_1.csv");

        k = new KIII(2, 16, 2);
        k.train(DataIO.read("train.csv"));
        DataIO.write(k.run(in2), "out3_trained_1.csv");

        in2 = DataIO.read("in2-2.csv");

        k = new KIII(2, 16, 2);
        DataIO.write(k.run(in2), "out3_default_2.csv");

        k = new KIII(2, 16, 2);
        k.train(DataIO.read("train.csv"));
        DataIO.write(k.run(in2), "out3_trained_2.csv");
    }
}
