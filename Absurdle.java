// Cory Vo
// TA: Zane Lee
// Date: 10/30/2023
// P-Assignment 2: Absurdle
// Class Comment: This class is the game Absurdle. It purpose of the game is to guess a target word.
// However, the game attempts to prolong the game as much as possible, by picking the target word,
// at the last possible moment.
import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // Behavior: Goes through the dictionary specified by the user and takes away all of the words
    // that don't match the wordLength specified by the user.
    // Exception: If the wordLengh is less than 1, then an IllegalArgumentException will be thrown.
    // Return: Gives back the new set of words that contains only words that are of the wordLength
    // specified by the user.
    // Parameters: The list contents represents all of the words in the given dictionary. The int
    // wordLength represents the length of the word, the user wants to guess.
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if(wordLength < 1){
            throw new IllegalArgumentException();
        }
        Set<String> finalWords = new HashSet<>();
        for(String word : contents){
            if(wordLength == word.length()){
                finalWords.add(word);
            }
        }
        return finalWords;
    }

    // Behavior: Takes in the user's and goes through the possible set of words then finds which
    // pattern would prune the list the most. The pattern that would prune the list the least
    // will become the pattern that will represent the user's guess and be displayed to them.
    // The list of words that are associated to this final pattern will become the new list
    // that we will go through to find the possible target word.
    // Exception: If the set of words in the given set words, or if the user's guess does not have
    // same amount of words as the actual word, then an IllegalArgumentException will be thrown.
    // Return: Gives back the finalPattern that represents the user's guess after pruning the
    // possible list of words.
    // Parameters: String guess represents the user's guess. The set, words, represents all of the
    // words that we are going through which serve as all of the possible words that can be the
    // target word.
    public static String record(String guess, Set<String> words, int wordLength) {
        if(words.isEmpty() || guess.length() != wordLength){
            throw new IllegalArgumentException();
        }
        Map<String, Set<String>> pruneList = new TreeMap<>();
        // Tracks the string we will return
        String finalPattern = "";
        // Tracks the size of the largest set
        int largest = 0;
        for(String word : words){
            String currPattern = patternFor(word, guess);
            // If this is the first time we are seeing this instance of the pattern
            if(!pruneList.containsKey(currPattern)){
                Set<String> counter = new HashSet<String>();
                counter.add(word);
                pruneList.put(currPattern, counter);

            }
            // This is not the first time we are seeing this pattern
            else {
                Set<String> temp = pruneList.get(currPattern);
                temp.add(word);
                pruneList.put(currPattern, temp);
            }
        }
        for(String pattern : pruneList.keySet()){
            Set<String> tracker = pruneList.get(pattern);
            if(tracker.size() > largest){
                largest = tracker.size();
                finalPattern = pattern;
            }
        }
        words.clear();
        Set<String> temp = pruneList.get(finalPattern);
        words.addAll(temp);
        return finalPattern;
    }

    // Behavior: Takes a guess and a word then compares the two. Then, based on the rules of
    // Absurdle, it gives back how close the guess was by replacing the letters of the guess
    // with certain box colors. Green box if the guess was in the same place as the word. Yellow
    // box if the letter of the guess is contained in the word but is in the wrong place. Gray box
    // if the letter of the guess is not in the word.
    // Return: Gives back a pattern of corresponding boxes based on the user's guess.
    // Parameters: String word represents a word in the given file, while String guess represents
    //  the user's word guess.
    public static String patternFor(String word, String guess) {
        String[] input = new String[guess.length()];
        input = guess.split("");
        Map<Character, Integer> charTracker = new HashMap<>();
        for(int j = 0; j < word.length(); j++){
            if(!charTracker.containsKey(word.charAt(j))){
                charTracker.put(word.charAt(j), 1);
            }
            else {
                charTracker.put(word.charAt(j), charTracker.get(word.charAt(j)) + 1);
            }
        }
        // Green
        for(int i = 0; i < input.length; i++){
            if(word.charAt(i) == guess.charAt(i)){
                input[i] = GREEN;
                charTracker.put(word.charAt(i), charTracker.get(word.charAt(i)) - 1);
            }
        }
        // Yellow and Gray
        for(int k = 0; k < input.length; k++){
            if((charTracker.containsKey(guess.charAt(k))) && (charTracker.get(guess.charAt(k)) != 0) 
            && input[k] != GREEN){
                input[k] = YELLOW;
                charTracker.put(guess.charAt(k), charTracker.get(guess.charAt(k)) - 1);
            }
            else if(input[k] != GREEN){
                input[k] = GRAY;
            }
        }       
        return concatenate(input);
    }

    // Behavior: Goes through and puts all of the colored boxes jointly together.
    // Return: Gives back the user's guess with the corresponding colored boxes.
    // Parameters: Takes in String[] input which represents the user's guess with the
    // corresponding colored boxes.
    public static String concatenate(String[] input){
        String finished = "";
        for(int m = 0; m < input.length; m++){
            finished += input[m];
        }
        return finished;
    } 
}