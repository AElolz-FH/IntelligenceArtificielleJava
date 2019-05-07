import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**noeud d'un arbre pour algo MCTS jouant au TicTacToe
* grille = 9 entiers (0 si case vide, 1 si pion IA, 2 si pion humain)
*/
public class MCTSNodeTTT {
    /**objet pour selection au hasard*/
    private static Random r = new Random();
    /**liste des noeuds visites pour le jeu en cours*/
    private static List<MCTSNodeTTT> visited = new LinkedList<>();
    /**ia=true si noeud joué par IA*/
    private boolean ia = true;
    /**pour eviter les divisions / 0*/
    private static double epsilon = 1e-6;
    /**fils du noeud*/
    private MCTSNodeTTT[] children = null;
    /**noeud terminal = partie gagnee, perdue ou nulle*/
    private boolean terminal = false;
    /**feuille -> noeud genere ne correspondant pas à une fin de jeu*/
    private boolean leaf = true;
    /**nb de fois que le noeud a été visite*/
    private double nbVisites = 0;
    /**somme des valeurs du noeud et de ses fils*/
    private double sommeGains = 0;
    // donnees typique à l'application, au jeu, ici le tictactoe ou aligne-3
    /**nb d'actions maxi initialement = 9 pour les 9 cases*/
    private int nbActions = 9;
    /**grille sous forme de tableau d'entiers (1 pour jeu IA, 2 pour jeu humain, 0 pour case vide)*/
    private int[] grille = new int[9];
    /**action  = no de case joué dans le noeud(0->(0,0), 3->(0,1), 8->(2,2)*/
    private int action = 0;
    /**ligne faite par IA*/
    private static int[] ligneAI = {1,1,1};
    /**ligne faite par Humain*/
    private static int[] ligneHumain = {2,2,2};


    MCTSNodeTTT(){}
    MCTSNodeTTT(int _nbActions){nbActions = _nbActions;}
    MCTSNodeTTT(int _nbActions, boolean _me){this(_nbActions); ia =_me;}
    MCTSNodeTTT(int _nbActions, boolean _me, int[] _grille){
        this(_nbActions);
        ia =_me;
        System.arraycopy(_grille, 0, grille, 0, grille.length);
    }

    /**à partir du noeud courant, <br>
     * - descendre jusqu'à un noeud feuille en choisissant le ''meilleur'' chemin <br>
     * - en creer des fils et jouer une partie complete
     * */
    public void selectAction() {
        visited.clear();
        terminal = false;
        var node = this;
        //descente dans l'arbre
        visited.add(this);
        while (!node.isLeaf()) {
            node = node.select();
            visited.add(node);
        }
        //arrivé à un noeud feuille.. creer ses fils directs
        double value=0;
        //si le noeud feuille trouve est terminal et deja visite, recuperer sa valeur
        if (node.terminal) value = node.sommeGains;
        else if(node.nbActions==0)
        {        //si le noeud feuille trouve est terminal et non deja visite, calculer sa valeur
            node.terminal = true;
            if(node.isWinner(1)) node.sommeGains = 1;
            else if(node.isWinner(2)) node.sommeGains = 0;
            else
            {
                if(node.nbActions==0) node.sommeGains = 0.5;
            }
            value = node.sommeGains;
        }
        else
        {   //si c'est un noeud feuille non terminal.. creer ses fils directs
            node.expand();
            //choisir un noeud
            var newNode = node.select();
            // jouer jusqu'au bout
            value = rollOut(newNode);
        }
        // re-evaluer les noeuds utilises
        for (MCTSNodeTTT n : visited)  n.updateStats(value);
    }


    /**ajouter des noeuds fils avec chacun une action différente*/
    public void expand() {
        leaf = false;
        if(children ==null) children = new MCTSNodeTTT[nbActions];
        MCTSNodeTTT child = null;
        int token=(ia ?1:2);
        //TODO: creer des fils avec chacun une grille modifiee ou le noeud courant aurant pose un pion
    }

    /**choisir un noeud parmi les fils en utilisant les valeurs
     * d'exploitation (somme des gains du fils/ nb de ses visites)
     * et d'exploration racine carree du (log(nb de visite du noeud)/ nb visite du fils)
     * */
    private MCTSNodeTTT select() {
        MCTSNodeTTT selectedNode = null;
        var bestValue = Double.NEGATIVE_INFINITY;
        var c = 1.5;
        for (MCTSNodeTTT child : children) {
            double value =
                    child.sommeGains / (child.nbVisites + epsilon) +
                            Math.sqrt(Math.log(nbVisites +1) / (child.nbVisites + epsilon)) * c  +  r.nextDouble() * epsilon;
            if (value > bestValue) {
                selectedNode = child;
                bestValue = value;
            }
        }
        return selectedNode;
    }



    /**determine si le joeur de coef 1 ou 2 a gagne dans ce noeud
    * si coef=1, retourne vrai si une ligne, une colone 
    *  ou une dagonale de 1 a été trouvee dans la grille.
    * idem si coef = 2, ...
    */
    public boolean isWinner(int coef)
    {
        boolean gain = false;
        //TODO: vereifier la presence d'une ligne, colonne ou diagonal de 'coef' dans la grille
        return gain;
    }


    /**continuer une partie jusqu'a une victoire ou un match nul :
     * etend le noeud, choisit un fils au hasard, l'étend, etc...
     * Le noeud terminal si c'est une noeud de victoire pour l'ia ou l'humaine
     * ou s'il n'y a plus d'actions possible (nbActions==0)
     * si noeud terminal et victoire ia -> retourne 1
     * si noeud terminal et victoire humain -> retourne 0
     * si noeud terminal et situation bloquée -> retourne 0
     * si noeud non terminal, etendre le noeud, prendre un de ses fils au hasard 
     * et retourner la valeur de rollOut de ce fils...
     */
    public double rollOut(MCTSNodeTTT tn) {
        visited.add(tn);
        double val = 0;
        //TODO: completer la fonction
        return val;
    }

    /**incremente le nb de visites et cumule la valeur value au total*/
    public void updateStats(double value) {
        nbVisites++;
        sommeGains += value;
    }

    /**retourne la dimension du noeud*/
    public int arity() {
        return children == null ? 0 : children.length;
    }

    /**retourne le noeud fils dont la valeur moyenne est maximale */
    public MCTSNodeTTT bestChild()
    {
        var max=-1d;
        MCTSNodeTTT bestChild = null;
        for(int i=0; i<arity(); i++)
        {
            var child = children[i];
            var interest = child.sommeGains /child.nbVisites;
            if( interest > max)
            {
                max = interest;
                bestChild = child;
            }
        }
        return bestChild;
    }

    /**retourne le fils correspondant à l'action (0 pour un jeu en (0,0), 1 pour un jeu en (1,0), 8 pour un jeun en (2,2)*/
    public MCTSNodeTTT findChild(int action)
    {
        MCTSNodeTTT child = null;
        if(children!=null)
        {
            int i = 0;
            while (child==null && i<children.length)
            {
                if(children[i].action == action) child = children[i];
                i++;
            }
        }
        return child;
    }

    /**retourne leaf*/
    public boolean isLeaf() {
        return leaf;
    }

    public int getAction(){return action;}
    public int getNbActions(){return nbActions;}


    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("niveau " + this.nbActions +"\n");
        for(int i=0; i<3; i++) {
            sb.append("\t+");
            for (int j = 0; j < 3; j++)
                sb.append(grille[i * 3 + j]).append("+");
            sb.append("\n");
        }
        return sb.toString();
    }

}
