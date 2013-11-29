package uva.verspeek.hangman.gameplay;

import java.io.BufferedReader;
import uva.verspeek.hangman.gameplay.MainActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class ControlWords {

	public ArrayList<String> populateWords(InputStream stream){
		ArrayList<String> words;
		words = new ArrayList<String>();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(inputStreamReader);

			String line;
			while ((line = br.readLine()) != null) {
				words.add(line);
			}

			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return words;
	}
	
	public String getWord(int length, ArrayList<String> words) {
		String randomWord = "";
		while (randomWord.length() != length) {
			randomWord = words.get(new Random().nextInt(words.size() - 1));
		}

		return randomWord;

	}
}
