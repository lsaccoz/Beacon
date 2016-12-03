package com.bcn.beacon.beacon.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringAlgorithms {

    private static HashMap<Character, String> keyboardLayout = new HashMap<Character, String>()
    {
        {
            put('a' , "qwsz");  put('b', "vghn");   put('c', "xdfv");
            put('d', "ersfxc"); put('e', "wsdfr");  put('f', "rtdgcv");
            put('g', "tyfhvb"); put('h', "yugjbn"); put('i', "ujko");
            put('j', "uihknm"); put('l', "kop");    put('m', "njk");
            put('n', "bhjm");   put('o', "iklp");   put('p', "ol");
            put('q', "wsa");    put('r', "edft");   put('s', "weadzx");
            put('t', "rfgy");   put('u', "yhji");   put('v', "cfgb");
            put('w', "qeas");   put('x', "zsdc");   put('y', "tghu");
            put('z', "asx");
        }
    };

    /**
     * The purpose of this algorithm is to improve the search results
     * based on simple string matching mistakes typed by users.
     * The elements are possibilities of what this string could match with.
     * @param word: string that represents the user's search query
     * @return list of possible matching strings
     */
    public static List<String> getStringTypos(String word) {
        List<String> typos = new ArrayList<String>();
        if (word == null) return typos;
        typos.addAll(getSwitchTypos(word));
        typos.addAll(getKeyboardTypos(word));
        typos.addAll(getMissingCharTypos(word));
        return typos;
    }

    // typos associated with missing a letter in a word.
    private static List<String> getMissingCharTypos(String word) {
        List<String> typos = new ArrayList<String>();
        for (int i = 0; i < word.length(); i++)
            typos.add(delete(word, i));
        return typos;
    }

    // typos associated with incorrectly typing a letter with the keyboard.
    private static List<String> getKeyboardTypos(String word) {
        List<String> typos = new ArrayList<String>();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            String close = keyboardLayout.get(c);
            if (close == null) continue;
            for (int l = 0; l < close.length(); l++)
                typos.add(replace(word, i, close.charAt(l)));
        }
        return typos;
    }

    // typos associated with typing two letters in wrong order.
    private static List<String> getSwitchTypos(String word) {
        List<String> typos = new ArrayList<String>();
        for (int i = 0; i < word.length()-1; i++)
            typos.add(swap(word, i, i+1));
        return typos;
    }

    // returns the string s with the ith char changed to c.
    public static String replace(String s, int i, char c) {
        if (s == null || i >= s.length()) return null;
        return s.substring(0,i) + c + s.substring(i+1);
    }

    // returns the string s with the ith char deleted.
    public static String delete(String s, int i) {
        if (s == null || i >= s.length()) return null;
        return s.substring(0,i) + s.substring(i+1);
    }

    // returns the string s with the ith char swapped with the jth.
    public static String swap(String s, int i, int j) {
        if (s == null || i >= s.length() || j >= s.length()) return null;
        if (i > j) { int t = i; i = j; j = t; }
        char ci = s.charAt(i), cj = s.charAt(j);
        return s.substring(0,i) + cj + s.substring(i+1, j) + ci + s.substring(j+1);
    }

}
