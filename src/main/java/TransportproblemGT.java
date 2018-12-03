import gurobi.*;

public class TransportproblemGT {

    private static final int[][] c = {{4, 2, 3, 1}, {5, 6, 4, 0}};
    private static final int[] a = {20, 12};
    private static final int anzahlErzeuger = a.length;
    private static final int[] b = {10, 10, 5, 7};
    private static final int anzahlAbnehmer = b.length;

    public static void main(String[] args) throws GRBException {

        GRBEnv environment = new GRBEnv("linopt.log");
        GRBModel model = new GRBModel(environment);

        //initialize variables
        GRBVar[][] x = new GRBVar[anzahlErzeuger][anzahlAbnehmer];
        for (int i = 0; i < anzahlErzeuger; i++) {
            for (int j = 0; j < anzahlAbnehmer; j++) {
                x[i][j] = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "x" + i + "_" + j);
            }
        }
        model.update();

        //objective
        GRBLinExpr objective = new GRBLinExpr();
        for (int i = 0; i < anzahlErzeuger; i++) {
            for (int j = 0; j < anzahlAbnehmer; j++) {
                objective.addTerm(c[i][j], x[i][j]);
            }
        }
        model.setObjective(objective, GRB.MINIMIZE);

        //subject to
        GRBLinExpr bedarf;
        for (int j = 0; j < anzahlAbnehmer; j++) {
            bedarf = new GRBLinExpr();
            for (int i = 0; i < anzahlErzeuger; i++) {
                bedarf.addTerm(1, x[i][j]);
            }
            model.addConstr(bedarf, GRB.GREATER_EQUAL, b[j], "bedarf_" + j);
        }

        GRBLinExpr transportieren;
        for (int i = 0; i < anzahlErzeuger; i++) {
            transportieren = new GRBLinExpr();
            for (int j = 0; j < anzahlAbnehmer; j++) {
                transportieren.addTerm(1, x[i][j]);
            }
            model.addConstr(transportieren, GRB.GREATER_EQUAL, a[i], "transport_" + i);
        }

        //solve
        model.optimize();

        model.write("transportproblem_GT.lp");
        model.write("transportproblem_GT.sol");

        model.dispose();
        environment.dispose();
    }
}
