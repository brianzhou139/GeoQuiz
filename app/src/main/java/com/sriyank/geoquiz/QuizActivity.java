package com.sriyank.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    //this comment is for testing purposes yeah yeah
    //this comment is for testing purposes
    private static final String TAG="QuizActivity";
    private static final String KEY_INDEX="index";
    private static final String KEY_SCORE="score";
    private static final String EXTRA_ANSWER_IS_TRUE="com.sriyank.geoquiz_isTrue";
    private static final String EXTRA_ANSWER_SHOWN="com.sriyank.geoquiz_extra_answer_shown";
    private static final int REQUEST_CODE_CHEAT=0;

    public static Intent newIntent(Context context,boolean answerIsTrue){
        Intent intent=new Intent(context,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank=new Question[]{
            new Question(R.string.question_australia,true),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_africa,false),
            new Question(R.string.question_americas,true),
            new Question(R.string.question_asia,true)
    };

    private int mCurrentIndex=0;
    private int mCurrentScore=0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate (Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState!=null){
            mCurrentIndex=savedInstanceState.getInt(KEY_INDEX);
            mCurrentScore=savedInstanceState.getInt(KEY_SCORE);
        }

        mTrueButton=(Button)findViewById(R.id.true_button);
        mFalseButton=(Button)findViewById(R.id.false_button);
        mNextButton=(ImageButton) findViewById(R.id.next_button);
        mPrevButton=(ImageButton) findViewById(R.id.prev_button);
        mCheatButton=(Button)findViewById(R.id.cheat_button);

        mQuestionTextView=(TextView)findViewById(R.id.question_text_view);

        updateQuestion();
        //int question=mQuestionBank[mCurrentIndex].getTextResId();
        //mQuestionTextView.setText(question);

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex=(mCurrentIndex+1)%mQuestionBank.length;
                updateQuestion();
            }
        });//end of mQuestin..

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pressed true
                checkAnswer(true);
                disableButtons();
            }
        });//end of mTrueButton

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pressed false
                checkAnswer(false);
                disableButtons();
            }
        });//end of mFalseButton

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex!=0){
                    mCurrentIndex=(mCurrentIndex-1);
                }
                updateQuestion();
                enableButtons();
            }
        });//end of mPrevButt

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex=(mCurrentIndex+1)%mQuestionBank.length;
                mIsCheater=false;
                updateQuestion();
                enableButtons();
            }
        });//end of mNextButton

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue=mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent=newIntent(QuizActivity.this,answerIsTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

    }//end of onCreate

    private void updateQuestion(){
        int question=mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }//end of update question

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue=mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId=0;

        if(mIsCheater){
            messageResId=R.string.judgment_toast;
        }else{
            if(userPressedTrue==answerIsTrue){
                messageResId=R.string.correct_toast;
                mCurrentScore++;
            }else{
                messageResId=R.string.incorrect_toast;
            }
        }
        Toast.makeText(getApplicationContext(),messageResId ,Toast.LENGTH_LONG).show();
    }//end of checkAnswer

    private void disableButtons(){
        mTrueButton.setClickable(false);
        mFalseButton.setClickable(false);
    }

    private void enableButtons(){
        mTrueButton.setClickable(true);
        mFalseButton.setClickable(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,"onSaveInstance() called");
        outState.putInt(KEY_INDEX,mCurrentIndex);
        outState.putInt(KEY_SCORE,mCurrentScore);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }

        if(requestCode==REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            mIsCheater=wasAnswerShown(data);
        }

    }
}//end of QuizActivity
