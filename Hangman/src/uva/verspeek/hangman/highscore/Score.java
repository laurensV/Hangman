package uva.verspeek.hangman.highscore;

public class Score implements Comparable<Score> {
	private String scoreInfo;
	public int scoreNum;
	
	public Score(String info, int num){
	    scoreInfo=info;
	    scoreNum=num;
	}
	public int compareTo(Score sc){
	    //return 0 if equal
	    //1 if passed greater than this
	    //-1 if this greater than passed
	    return sc.scoreNum>scoreNum? 1 : sc.scoreNum<scoreNum? -1 : 0;
	}
	public String getScoreText()
	{
	    return scoreInfo+" Score: "+scoreNum;
	}
}
