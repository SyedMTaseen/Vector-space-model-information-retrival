/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorspacemodel;

import java.util.ArrayList;

/**
 *
 * @author Taseen Syed
 */
public class PostingListData {

    String docID;
    ArrayList<Integer> pos;//Creating arraylist  
    Double tf;
    Double tfidf;
    Double idf;

    public PostingListData(String docID, ArrayList<Integer> pos) {
        this.docID = docID;
        this.pos = pos;
    }

    public ArrayList<Integer> getPos() {
        return pos;

    }

    public void setPos(ArrayList<Integer> pos) {
        this.pos = pos;
    }

    public void putPos(Integer i) {
        this.pos.add(i);
    }

    public String getDocID() {
        return docID;
    }

    public Double gettf() {
        tf = 1 + Math.log10(pos.size());

        return (double) pos.size();// to math th gold standard
    }

    public void settfidf(int df) {
        Double idf = Math.log10(50.0 / df);
        tfidf = gettf() * idf;

    }

    public Double gettfidf() {
        return tfidf;
    }

}
