package com.example.guessmaster;

//importing required classes and interfaces
import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;

public class GuessMaster extends AppCompatActivity { //main activity class for the game
    private TextView entityName; //textview to display name of current entity
    private TextView ticketSum; //textview to display total tickets user has won
    private Button guessButton; //button to submit guess
    private EditText userIn; //edittext to let user type their guess
    private Button btnClearContent; //button that clears input and changes entity
    private ImageView entityImage; //imageview displays image of entity
    String answer; //stores users answer
    private int entityId; //stores current entity id
    private String entName; //stores name of current entity
    private Entity[] entities; //stores all entites in game
    private int numOfEntities; //number of entities in game
    private int numOfTickets; //number of tickets won in round
    private Entity currentEntity; //current entity
    private int totalTickets = 0; //total tickets won

    protected void onCreate(Bundle savedInstanceState) { //oncreate method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_activity);
        //connects member variables
        entityName = (TextView) findViewById(R.id.entityName);
        ticketSum = (TextView) findViewById(R.id.ticket);
        guessButton = (Button) findViewById(R.id.btnGuess);
        userIn = (EditText) findViewById(R.id.guessinput);
        btnClearContent = (Button) findViewById(R.id.btnclearContent);
        entityImage = (ImageView) findViewById(R.id.entityImage);
        //creates entity objects
        Country usa = new Country("United States", new Date("July", 4, 1776), "Washington DC", 0.1);
        Person myCreator = new Person("My Creator", new Date("May", 6, 1800), "Male", 1);
        Politician trudeau = new Politician("Justin Trudeau", new Date("December",25,1971), "Male", "Liberal", 0.25);
        Singer dion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female", "La voix du bon Dieu", new Date("November",6,1981), 0.5);

        new GuessMaster(); //creates new guessmaster object

        //adds entities to the game
        addEntity(usa);
        addEntity(myCreator);
        addEntity(trudeau);
        addEntity(dion);
        changeEntity(); //sets an entity to start at

        welcomeToGame(currentEntity); //displays welcome message

        btnClearContent.setOnClickListener(new View.OnClickListener() { //onclick listener for clear button
            public void onClick(View v) {
                changeEntity(); //change to a new entity when button is pressed
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() { //onclick listener for guess button
            public void onClick(View v) {
                    playGame(currentEntity); //call playgame with the current entity when button is pressed
            }
        });
    }
    public void changeEntity() { //method to change entity when playing
        userIn.getText().clear(); //clears user input
        entityId = genRandomEntityId(); //generates new entity
        Entity entity = entities[entityId]; //gets entity from array
        entName = entity.getName(); //gets name of entity
        entityName.setText(entName); //sets entity name
        ImageSetter(); //sets entity image
        currentEntity = entity; //updates entity
    }
    public void ImageSetter() { //method to set image for the current entity
        switch (entName) {
            case "Justin Trudeau":
                entityImage.setImageResource(R.drawable.justint); //set trudeau's image
                break;
            case "United States":
                entityImage.setImageResource(R.drawable.usaflag); //set USA's image
                break;
            case "Celine Dion":
                entityImage.setImageResource(R.drawable.celidion); //set dion's image
                break;
            case "My Creator":
                entityImage.setImageResource(R.drawable.download); //set my creator's image
                break;
        }
    }
    public void welcomeToGame(Entity entity) { //method to display welcome message when the game starts
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(GuessMaster.this); //creates alertdialog
        welcomeAlert.setTitle("GuessMaster Game v3"); //sets title
        welcomeAlert.setMessage(entity.welcomeMessage()); //sets message
        welcomeAlert.setCancelable(false); //make it non cancelable
        welcomeAlert.setNegativeButton("START GAME", new DialogInterface.OnClickListener() { //sets negative button
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Game is Starting... Enjoy", Toast.LENGTH_SHORT).show(); //displays toast message
            }
        });

        AlertDialog dialog = welcomeAlert.create(); //create dialog
        dialog.show(); //show dialog
    }
    public void playGame(Entity entity) { //method to play the game
        entityName.setText(entity.getName()); //sets entity name
        answer = userIn.getText().toString(); //get input from user
        answer = answer.replace("\n", "").replace("\r",""); //remove new lines and carriage returns
        Date date = new Date(answer); //parse date from input

        if (date.precedes(entity.getBorn())) { //checks if the date the user inputted is before the entities birthday
            AlertDialog.Builder born = new AlertDialog.Builder(GuessMaster.this);
            born.setTitle("Incorrect");
            born.setMessage("Try a later date.");
            born.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = born.create();
            dialog.show();

        } else if (entity.getBorn().precedes(date)) { //checks if the date the user inputted is after the entities birthday
            AlertDialog.Builder born = new AlertDialog.Builder(GuessMaster.this);
            born.setTitle("Incorrect.");
            born.setMessage("Try an earlier date.");
            born.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = born.create();
            dialog.show();

        } else { //checks to see if the date the user inputted is correct
            numOfTickets = entity.getAwardedTicketNumber(); //get the number of tickets the user should get
            totalTickets += numOfTickets; //add it to the total tickets
            String totalTicketNumString = getString(R.string.total_tickets_text, totalTickets); //creates string for total tickets

            AlertDialog.Builder winner = new AlertDialog.Builder(GuessMaster.this);
            winner.setTitle("You Won");
            winner.setMessage("BINGO! " + entity.closingMessage());
            winner.setCancelable(false);

            winner.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    totalTickets += entity.getAwardedTicketNumber(); //update total tickets
                    Toast.makeText(getBaseContext(), "You won: " + numOfTickets + " tickets!", Toast.LENGTH_SHORT).show(); //display toast message
                    ticketSum.setText(totalTicketNumString); //update ticket sum
                    continueGame(); //continue the game
                }
            });
            AlertDialog dialog = winner.create();
            dialog.show();
        }
    }
    public void continueGame(){ //method to continue the game after the user wins
        userIn.getText().clear(); //clear the user input
        changeEntity(); //change the entity
    }

    public GuessMaster() { //guessmaster constructor
        numOfEntities = 0; //initialize # of entities
        entities = new Entity[10]; //initialize array of entities
    }

    public void addEntity(Entity entity) { //method that adds a new entity to the game
        entities[numOfEntities++] = entity.clone(); //clones entity and adds to te array
    }

    public void playGame(int entityId) { //method to play game with an entity id
        Entity entity = entities[entityId]; //gets the entity from the array
        playGame(entity); //plays game with the entity
    }

    public void playGame() { //method to start a round
        int entityId = genRandomEntityId(); //generates random entity id
        playGame(entityId); //plays game with the id
    }
    public int genRandomEntityId() { //method to generate a rando entity id
        Random randomNumber = new Random(); //creates random object
        return randomNumber.nextInt(numOfEntities); //returns number corresponding to a random entity
    }
}