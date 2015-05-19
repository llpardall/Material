package alpha.example.com.material;


import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Application;
import android.widget.Toast;

import java.util.List;

import alpha.example.com.material.domain.GamePlay;
import alpha.example.com.material.domain.Question;

public class QuizApplication extends Application{
	
	/*
	 * This object represents the current game being played - lets us
	 * keep track of the current users game
	 */
	private GamePlay currentGame;
	public void setCurrentGame(GamePlay currentGame) {
		this.currentGame = currentGame;
	}
	public GamePlay getCurrentGame() {
		return currentGame;
	}

	@Override public void onCreate() {
		super.onCreate();

		/*
		 * In this tutorial, we'll subclass ParseObject for convenience to
		 * create and modify our domain objects
		 */
		ParseObject.registerSubclass(Question.class);
		

		/*
		 * Turn on the local data store to support caching of questions
		 */
		Parse.enableLocalDatastore( this );

		//
		//* Fill in this section with your Parse credentials
		//
		Parse.initialize(this, "UhdTMoFbpE7SfUHF9uXEfhR42imC0Jfv9hKf4erG", "8QM0hNgmEs4icqaf3jCets6ZvJEsXs3mGe5TmLaG");

		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
		ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		
		
		/*
		 * Now all the parse stuff is setup, we will grab the latest questions and cache them locally
		 * this will save us fetching them every time but will allow us to easily update questions 
		 * for all our active users. This will only fetch questions at app startup
		 */
		ParseQuery<Question> query = ParseQuery.getQuery("Question");
		final Application app = this;
		query.findInBackground( new FindCallback<Question>() {
			@Override public void done(List<Question> questions, ParseException e) {
				if ( e == null ){
					//We have updated questions from the cloud so we will cache them all
					ParseObject.unpinAllInBackground( "QUESTIONS" );
					ParseObject.pinAllInBackground( "QUESTIONS",  questions );
				} else {
					//alert the user that unable to fetch questions -this should be more robust in production apps
					Toast.makeText(app, 
							"Error updating questions - please make sure you have internet connection", 
							Toast.LENGTH_LONG).show();
				}
			}
		});		
	}
}