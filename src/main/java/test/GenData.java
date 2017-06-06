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
        DataIO.write(out0, "k0.csv");

        double[][] out1 = {run(new KI(0.8, 1.1), in[0])};
        DataIO.write(out1, "k1.csv");

        double[][] out2 = {run(new KII(1.2, 1.5, -1, -1.0), in[0])};
        DataIO.write(out2, "k2.csv");

        double[][] out3 = new KIII(2, 6, 1).run(DataIO.read("in2.csv"));
        DataIO.write(out3, "k3.csv");

        double[][] in2 = DataIO.read("in2_cont.csv");

        KIII k = new KIII(2, 6, 1);
        DataIO.write(k.run(in2), "k3_cont.csv");
    }
}
