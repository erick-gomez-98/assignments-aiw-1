package examples.slr;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.ArrayList;
import java.util.List;


public class SlrAgent extends Agent {
    private SLR slr = new SLR();
    private SlrGui myGui;
    public class SLR {
        public class Equation {
            private double b0;
            private double b1;

            public Equation(double b0, double b1) {
                this.b0 = b0;
                this.b1 = b1;
            }
        }

        public class Data {
            public int y;
            public int x;
            public int xy;
            public int x2;
            public int y2;

            public Data(int y, int x) {
                this.y = y;
                this.x = x;
            }
        }

        public List<Data> dataset = new ArrayList<>();
        public int n;
        public int Ex;
        public int Ey;
        public int Exy;
        public int Ex2;
        public int Ey2;

        public Equation findRegressionEquation(){
            for(int i = 0; i < dataset.size(); i++){
                Data e = dataset.get(i);
                e.xy = e.x * e.y;
                e.x2 = (int) Math.pow(e.x, 2);
                e.y2 = (int) Math.pow(e.y, 2);
                Ex += e.x;
                Ey += e.y;
                Exy += e.xy;
                Ex2 += e.x2;
                Ey2 += e.y2;
            }

            double b0 = ((Ey * Ex2) - (Ex * Exy)) / ((n * Ex2) - Math.pow(Ex, 2));
            double b1 = ((n * Exy) - (Ex * Ey)) / ((n * Ex2) - Math.pow(Ex, 2));
            return new Equation(b0, b1);
        }

        public double makePrediction(int x){
            Equation eq = findRegressionEquation();
            return eq.b0 + (eq.b1 * x);
        }

        public SLR(){
            dataset.add(new Data(651, 23));
            dataset.add(new Data(762, 26));
            dataset.add(new Data(856, 30));
            dataset.add(new Data(1063, 34));
            dataset.add(new Data(1190, 43));
            dataset.add(new Data(1298, 48));
            dataset.add(new Data(1421, 52));
            dataset.add(new Data(1440, 57));
            dataset.add(new Data(1518, 58));

            this.n = dataset.size();
        }
    }

    protected void setup() {
        // Create and show the GUI
        myGui = new SlrGui(this);
        myGui.showGui();

        System.out.println("Agent "+getLocalName()+" started.");
        SLR.Equation eq = slr.findRegressionEquation();
        System.out.println("Sales = " + eq.b0 + " + " + eq.b1 + " Advertising");
    }


    public void makePrediction(final int x) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                double prediction = slr.makePrediction(x);
                System.out.println("For the x value of " + x + " the prediction is: " + prediction);
            }
        } );
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Close the GUI
		myGui.dispose();

        // Printout a dismissal message
        System.out.println("SLR-agent "+getAID().getName()+" terminating.");
    }
}