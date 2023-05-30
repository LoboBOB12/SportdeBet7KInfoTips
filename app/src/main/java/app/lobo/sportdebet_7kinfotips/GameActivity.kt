package app.lobo.sportdebet_7kinfotips

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GameActivity : AppCompatActivity() {
    private lateinit var arcImageView: ImageView
    private lateinit var footballerImageView: ImageView
    private lateinit var ballImageView: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var lifeTextView: TextView

    private var score = 0
    private var life = 3
    private var timeRemaining = 30

    private lateinit var gameTimer: CountDownTimer

    private var screenWidth = 0
    private var screenHeight = 0
    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeed = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        arcImageView = findViewById(R.id.arc)
        footballerImageView = findViewById(R.id.footballer)
        ballImageView = findViewById(R.id.ball)
        scoreTextView = findViewById(R.id.score)
        timeTextView = findViewById(R.id.time)
        lifeTextView = findViewById(R.id.life)

        // Set initial values
        scoreTextView.text = "Score: $score"
        timeTextView.text = "Time: $timeRemaining"
        lifeTextView.text = "Life: $life"

        // Set touch listener for controlling the footballer
        findViewById<View>(R.id.root_layout).setOnTouchListener { _, event ->
            handleTouch(event)
            true
        }

        // Start the game timer
        gameTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = (millisUntilFinished / 1000).toInt()
                timeTextView.text = "Time: $timeRemaining"
            }

            override fun onFinish() {
                finishGame()
            }
        }
        gameTimer.start()

        // Get screen dimensions and initialize ball position
        arcImageView.post {
            screenWidth = arcImageView.width
            screenHeight = arcImageView.height

            initializeGame()
        }
    }

    private fun initializeGame() {
        // Set initial position of the ball at the bottom of the screen
        ballX = Random().nextInt(screenWidth - ballImageView.width).toFloat()
        ballY = screenHeight - ballImageView.height.toFloat()
        ballImageView.translationX = ballX
        ballImageView.translationY = ballY

        // Start moving the ball
        startBallMovement()
    }

    private fun startBallMovement() {
        object : CountDownTimer(30000, 16) {
            override fun onTick(millisUntilFinished: Long) {
                moveBall()
            }

            override fun onFinish() {
                // Game finished
            }
        }.start()
    }

    private fun moveBall() {
        ballY -= ballSpeed
        ballImageView.translationY = ballY
        checkCollision()
    }

    private fun handleTouch(event: MotionEvent) {
        val x = event.x
        val screenWidth = arcImageView.width
        val footballerWidth = footballerImageView.width

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x < screenWidth / 2) {
                    // Move footballer to the left
                    if (footballerImageView.x > 0) {
                        footballerImageView.x -= footballerWidth
                    }
                } else {
                    // Move footballer to the right
                    if (footballerImageView.x < screenWidth - footballerWidth) {
                        footballerImageView.x += footballerWidth
                    }
                }
            }
        }
    }

    private fun finishGame() {
        gameTimer.cancel()
        val intent = Intent(this, finnish::class.java)
        startActivity(intent)
        // Perform actions to end the game, such as showing a game over screen or returning to the main menu.
    }

    private fun handleBallGoalCollision() {
        life--
        lifeTextView.text = "Life: $life"
        resetBall()
    }

    private fun handleBallGoalkeeperCollision() {
        score++
        scoreTextView.text = "Score: $score"
        resetBall()
    }

    private fun checkCollision() {
        val ballRect = Rect(
            ballImageView.left + ballImageView.translationX.toInt(),
            ballImageView.top + ballImageView.translationY.toInt(),
            ballImageView.right + ballImageView.translationX.toInt(),
            ballImageView.bottom + ballImageView.translationY.toInt()
        )

        val footballerRect = Rect(
            footballerImageView.left + footballerImageView.translationX.toInt(),
            footballerImageView.top,
            footballerImageView.right + footballerImageView.translationX.toInt(),
            footballerImageView.bottom
        )

        val goalRect = Rect(
            arcImageView.left,
            arcImageView.top,
            arcImageView.right,
            arcImageView.bottom
        )

        if (Rect.intersects(ballRect, footballerRect)) {
            handleBallGoalkeeperCollision()
        } else if (Rect.intersects(ballRect, goalRect)) {
            handleBallGoalCollision()
        }
    }

    private fun resetBall() {
        ballX = Random().nextInt(screenWidth - ballImageView.width).toFloat()
        ballY = screenHeight - ballImageView.height.toFloat()
        ballImageView.translationX = ballX
        ballImageView.translationY = ballY
    }
}