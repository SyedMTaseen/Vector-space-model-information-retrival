/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorspacemodel;

import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Taseen Syed
 */
public class FXMLDocumentController implements Initializable {

    static ArrayList<String> StopWordList = new ArrayList<String>();
    static HashMap<String, LinkedList> Dictionary = new HashMap<String, LinkedList>();

    @FXML
    private Text DocCount;
    @FXML
    private ListView<Map.Entry<String, Double>> DocListView;
    @FXML
    private JFXTextField searchInput;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try {
            // TODO
            // initializedInvertedIndex();
            //checking if inverted index exist
            checkInvertedIndex();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onEnter(ActionEvent event) {
        queryProcess();
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        ((Stage) DocCount.getScene().getWindow()).close();
    }

    @FXML
    private void handleSearchButtonActions(ActionEvent event) {
        queryProcess();
    }

    private void checkInvertedIndex() throws IOException {
        File file = new File("Dictionary\\Dictionary.txt");
        if (file.length() != 0)// no inverted index
        {
            System.out.println("Read from TXT");
            readFromTXT();
        } else {
            System.out.println("Create inverted index");
            initializedInvertedIndex();
        }
    }

    private void initializedInvertedIndex() throws IOException {
        LinkedList<PostingListData> postingList;
        //Remove stopword
        File file1 = new File("StopWords\\Stopword-List.txt");

        BufferedReader br = new BufferedReader(new FileReader(file1));
        String st;
        while ((st = br.readLine()) != null) {
            StopWordList.add(st);
        }

        System.out.println(StopWordList);
        for (int a = 1; a <= 50; a++) //   int a = 1;
        {
            String str1 = Integer.toString(a);
            File file = new File("StortStories\\" + str1 + ".txt");//read stories

            BufferedReader br1 = new BufferedReader(new FileReader(file));
            int posi = 1;
            String st1;

            while ((st1 = br1.readLine()) != null) {
                StringTokenizer sts = new StringTokenizer(st1, " ");

                while (sts.hasMoreTokens()) {
                    String ns = sts.nextToken();
                    ns = ns.replaceAll("[^a-zA-Z0-9]", " ");
                    StringTokenizer stsr = new StringTokenizer(ns, " ");
                    while (stsr.hasMoreTokens()) {
                        String n = stsr.nextToken();

                        if (!StopWordList.contains(n.toLowerCase())) {
                            //   System.out.println(n);

                            if (!Dictionary.containsKey(n.toLowerCase())) {
                                //     System.out.println(n);

                                postingList = new LinkedList<PostingListData>();
                                ArrayList<Integer> posArray = new ArrayList<Integer>();//Creating arraylist
                                posArray.add(posi);
                                PostingListData pd = new PostingListData(str1, posArray);
                                // System.out.println(pd);
                                postingList.add(pd);
                                Dictionary.put(n.toLowerCase(), postingList);
                            } else {

                                postingList = Dictionary.get(n.toLowerCase());

                                PostingListData pd = postingList.getLast();
                                if (pd.getDocID().equals(str1)) {//  System.out.println(pd);
                                    ArrayList<Integer> posArray = pd.getPos();//Creating arraylist
                                    posArray.add(posi);
                                    PostingListData pds = new PostingListData(str1, posArray);
//                        pd.setPos(posArray);
//                        System.out.println(posArray);
                                    if (pd.getDocID().equals(str1)) {
                                        postingList.removeLast();
                                    }

                                    postingList.add(pds);
                                } else {
                                    ArrayList<Integer> posArray = new ArrayList<Integer>();//Creating arraylist
                                    posArray.add(posi);
                                    PostingListData pds = new PostingListData(str1, posArray);
                                    postingList.add(pds);
                                }

                                Dictionary.replace(n.toLowerCase(), postingList);
                            }

                        }
                        posi++;
                    }
                }

                // TimeUnit.SECONDS.sleep(5);
            }

        }
//        double d = 3.0 / 2.0;
//        System.out.println(Math.log10(3.0 / 2));
//
//     set tf idf  in dictionary
        for (Map.Entry m : Dictionary.entrySet()) {
            //System.out.print(m.getKey() + " ");
            LinkedList<PostingListData> list = (LinkedList<PostingListData>) m.getValue();
            int df = list.size();

            for (PostingListData b : list) {
                //System.out.print(b.docID + "(" + b.getPos() + ")" + " ");
                b.settfidf(df);

            }
            //  System.out.println(list.size());
            // System.out.println(" ");

        }
    }

    public void readFromTXT() throws FileNotFoundException, IOException {

        File StopWordFile = new File("StopWords\\Stopword-List.txt");

        BufferedReader br1 = new BufferedReader(new FileReader(StopWordFile));
        String sto;

        while ((sto = br1.readLine()) != null) {
            StopWordList.add(sto);
        }

        System.out.println(StopWordList);
        LinkedList<PostingListData> postingList;
        File file = new File("Dictionary\\Dictionary.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        while ((s = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(s, "-");
            String word = st.nextToken();
            // System.out.print(word+ " ");
            postingList = new LinkedList<PostingListData>();
            while (st.hasMoreTokens()) {
                String listData = st.nextToken();
                listData = listData.replaceAll(" ", "");
                listData = listData.replaceAll("\\[", " ");
                listData = listData.replaceAll("]", " ");
                StringTokenizer st1 = new StringTokenizer(listData, " ");
                String docID = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
                ArrayList<Integer> posArray = new ArrayList<Integer>();
                while (st2.hasMoreTokens()) {
                    posArray.add(parseInt(st2.nextToken()));
                }
                // System.out.print(word+" ");
                PostingListData pds = new PostingListData(docID, posArray);
                postingList.add(pds);
            }
            Dictionary.put(word, postingList);
            System.out.println("");

        }
        for (Map.Entry m : Dictionary.entrySet()) {

            LinkedList<PostingListData> list = (LinkedList<PostingListData>) m.getValue();
            int df = list.size();

            for (PostingListData b : list) {

                b.settfidf(df);

            }

        }

    }
// settting ui list 

    private void setListItem() {
        DocListView.setCellFactory((ListView<Map.Entry<String, Double>> p) -> {
            ListCell<Map.Entry<String, Double>> cell = new ListCell<Map.Entry<String, Double>>() {

                @Override
                protected void updateItem(Map.Entry<String, Double> t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t != null) {


                      
                            setText("Doc ID: " + t.getKey() + "\n Cosin Similarity : " + t.getValue());
                        

                    }
                }

            };

            return cell;
        });
    }

    private void queryProcess() {
        HashMap<String, Double> Query = new HashMap<String, Double>(); //store query with tfidf
        TreeMap<String, Double> lists = new TreeMap<String, Double>(); // store vetor multplication of query and doc
        TreeMap<String, Double> Mag = new TreeMap<String, Double>();// store doc magintude
        LinkedHashMap<String, Double> Ans = new LinkedHashMap<String, Double>();// store doc magintude
        String query = searchInput.getText();
        query = query.replaceAll("[^a-zA-Z0-9]", " ");
        StringTokenizer st = new StringTokenizer(query, " ");

        while (st.hasMoreTokens()) {
            String ns = st.nextToken();
            while (StopWordList.contains(ns.toLowerCase())) {
                // gap++;
                ns = st.nextToken();

            }
            if (!Dictionary.containsKey(ns.toLowerCase())) {
                alertPopup(ns.toLowerCase());
                return;
            }
            if (!Query.containsKey(ns.toLowerCase())) {
                LinkedList<PostingListData> postingList1 = Dictionary.get(ns.toLowerCase());
                int df = postingList1.size();
                Double idf = Math.log10(50.0 / df);
                Pattern pattern = Pattern.compile(ns);
                Matcher matcher = pattern.matcher(query);
                int count = 0;
                while (matcher.find()) {
                    count++;
                }
                Double tf = 1 + Math.log10(count);
                Query.put(ns.toLowerCase(), count * idf);//count use to match sample query
            }

        }

        Double queryMeg = 0.0;  // calsulate query megnitude
        //vector multiply
        for (Map.Entry m : Query.entrySet()) {
            LinkedList<PostingListData> list = Dictionary.get(m.getKey());
            queryMeg += Math.pow((Double) m.getValue(), 2);
            for (PostingListData b : list) {
                String s1 = b.docID;
                Double mul = b.gettfidf() * (Double) m.getValue();
                if (lists.containsKey(s1)) {
                    Double m1 = lists.get(s1);
                    lists.replace(s1, m1 + mul);

                } else {
                    lists.put(s1, mul);
                }
            }
        }

        //calculte doc magnitude
        for (Map.Entry m : Dictionary.entrySet()) {

            LinkedList<PostingListData> list = (LinkedList<PostingListData>) m.getValue();
            for (PostingListData b : list) {
                String s1 = b.docID;
                Double mag = Math.pow(b.gettfidf(), 2);
                if (Mag.containsKey(s1)) {
                    Double m2 = Mag.get(s1);
                    Mag.put(s1, mag + m2);
                } else {
                    Mag.put(s1, mag);
                }

            }
        }
        // For cosin simillarity
        for (Map.Entry m : lists.entrySet()) {

            Double mag = Mag.get(m.getKey());
            Double den = Math.sqrt(mag) * Math.sqrt(queryMeg);
            Double num = (double) m.getValue();
            Double val = num / den;
            String q = m.getKey().toString();
            lists.replace(q, val);

        }
        //      System.out.println("Query Meg:" + Math.sqrt(queryMeg));
//        
        //Sorting in order by value
        Map<String, Double> sorted = lists
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        int docCount = 0;
        for (Map.Entry m : sorted.entrySet()) {
            if ((double) m.getValue() < 0.005) {
                break;
            }
            System.out.println(m.getKey() + " " + m.getValue());

            Ans.put((String) m.getKey(), (double) m.getValue());
            docCount++;

        }

        ObservableList<Map.Entry<String, Double>> items = FXCollections.observableArrayList(Ans.entrySet());

        DocListView.setItems(items);
        setListItem();
        DocCount.setText("\t\t\t\t" + Integer.toString(docCount));

    }

    void alertPopup(String s) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("AlertFXML.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stage = new Stage(StageStyle.DECORATED);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("NOT FOUND");
        stage.setScene(new Scene(parent));
        stage.show();
    }

}
